package org.ocha.hdx.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.ocha.hdx.config.DummyConfigurationCreator;
import org.ocha.hdx.importer.HDXWithCountryListImporter;
import org.ocha.hdx.importer.ImportReport;
import org.ocha.hdx.importer.PreparedData;
import org.ocha.hdx.importer.PreparedIndicator;
import org.ocha.hdx.importer.ScraperValidatingImporter;
import org.ocha.hdx.model.validation.ValidationReport;
import org.ocha.hdx.model.validation.ValidationStatus;
import org.ocha.hdx.persistence.dao.ImportFromCKANDAO;
import org.ocha.hdx.persistence.dao.currateddata.EntityDAO;
import org.ocha.hdx.persistence.dao.currateddata.EntityTypeDAO;
import org.ocha.hdx.persistence.dao.currateddata.IndicatorTypeDAO;
import org.ocha.hdx.persistence.dao.currateddata.SourceDAO;
import org.ocha.hdx.persistence.dao.dictionary.SourceDictionaryDAO;
import org.ocha.hdx.persistence.entity.ImportFromCKAN;
import org.ocha.hdx.persistence.entity.ckan.CKANDataset;
import org.ocha.hdx.persistence.entity.ckan.CKANDataset.Type;
import org.ocha.hdx.persistence.entity.configs.ResourceConfiguration;
import org.ocha.hdx.persistence.entity.curateddata.Indicator;
import org.ocha.hdx.validation.DummyValidator;
import org.ocha.hdx.validation.ScraperValidator;
import org.ocha.hdx.validation.itemvalidator.IValidatorCreator;
import org.ocha.hdx.validation.prevalidator.IPreValidatorCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FileEvaluatorAndExtractorImpl implements FileEvaluatorAndExtractor {

	private static Logger logger = LoggerFactory.getLogger(FileEvaluatorAndExtractorImpl.class);

	@Autowired
	private ImportFromCKANDAO importFromCKANDAO;

	@Autowired
	private EntityDAO entityDAO;

	@Autowired
	private EntityTypeDAO entityTypeDAO;

	@Autowired
	private SourceDictionaryDAO sourceDictionaryDAO;

	@Autowired
	private IndicatorTypeDAO indicatorTypeDAO;

	@Autowired
	private SourceDAO sourceDAO;

	@Autowired
	private CuratedDataService curatedDataService;

	@Autowired
	private IndicatorCreationService indicatorCreationService;

	@Resource
	private List<IValidatorCreator> validatorCreators;

	@Resource
	private List<IPreValidatorCreator> preValidatorCreators;

	@Autowired
	private DummyConfigurationCreator dummyConfigurationCreator;

	@Override
	public ValidationReport evaluateResource(final File file, final Type type) {
		// FIXME we probably want something else here, map of HDXValidator, or
		// Factory....
		switch (type) {
		case DUMMY:
			return new DummyValidator().evaluateFile(file);
		case SCRAPER_VALIDATING:
			final ValidationReport validationReport = new ScraperValidator().evaluateFile(file);
			// since we're using the same validator for both types, we're setting the correct type afterwards
			validationReport.setValidator(type);
			return validationReport;

		default:
			return this.defaultValidationFail(file);
		}
	}

	@Override
	public ImportReport transformAndImportDataFromResource(final File file, final Type type, final String resourceId, final String revisionId, final ResourceConfiguration config,
			final ValidationReport validationReport) {

		final ImportReport importReport = new ImportReport();

		// FIXME we probably want something else here, map of HDXImporter, or
		// Factory....
		final PreparedData preparedData;
		switch (type) {
		case DUMMY:
			preparedData = this.defaultImportFail(file);
			break;
		case SCRAPER_VALIDATING:
			final ScraperValidatingImporter importer = new ScraperValidatingImporter(this.sourceDictionaryDAO.getSourceDictionariesByResourceConfiguration(config), config, this.validatorCreators,
					this.preValidatorCreators, validationReport, this.indicatorCreationService);
			this.creatingMissingEntities(file, importer);
			preparedData = importer.prepareDataForImport(file);
			break;
		default:
			preparedData = this.defaultImportFail(file);
		}
		if (preparedData.isSuccess()) {
			logger.info(String.format("Import successful, about to persist %d values", preparedData.getIndicatorsToImport().size()));
			final List<Indicator> indicators = indicatorCreationService.createIndicators(preparedData.getIndicatorsToImport());
			// FIXME here we used to run importer.validations, and this should as well populate one of the report
			this.saveReadIndicatorsToDatabase(indicators, resourceId, revisionId);
		} else {
			logger.info("Import failed");
		}

		importReport.setOverallResult(preparedData.isSuccess());
		return importReport;

	}

	/**
	 * see {@link FileEvaluatorAndExtractor#incorporatePreparedDataForImport(PreparedData, String, String)}
	 */
	@Override
	@Deprecated
	public void incorporatePreparedDataForImport(final PreparedData preparedData, final String resourceId, final String revisionId) {
		final ImportFromCKAN importFromCKAN = this.importFromCKANDAO.createNewImportRecord(resourceId, revisionId, new Date());
		for (final PreparedIndicator preparedIndicator : preparedData.getIndicatorsToImport()) {
			try {
				this.curatedDataService.createIndicator(preparedIndicator, importFromCKAN);
			} catch (final Exception e) {
				logger.debug(String.format("Error trying to create preparedIndicator : %s", preparedIndicator.toString()));
			}
		}
	}

	private void saveReadIndicatorsToDatabase(final List<Indicator> indicators, final String resourceId, final String revisionId) {
		final ImportFromCKAN importFromCKAN = this.importFromCKANDAO.createNewImportRecord(resourceId, revisionId, new Date());
		for (final Indicator indicator : indicators) {
			try {
				this.curatedDataService.createIndicator(indicator, importFromCKAN);
			} catch (final Exception e) {
				logger.debug(String.format("Error trying to save Indicator : %s", indicator.toString()));
			}
		}
	}

	private ValidationReport defaultValidationFail(final File file) {
		final ValidationReport report = new ValidationReport(CKANDataset.Type.DUMMY);

		report.addEntry(ValidationStatus.ERROR, "Mocked evaluator, always failing");
		return report;
	}

	private PreparedData defaultImportFail(final File file) {
		final PreparedData preparedData = new PreparedData(false, null);
		return preparedData;
	}

	private void creatingMissingEntities(final File file, final HDXWithCountryListImporter importer) {
		for (final Entry<String, String> entry : importer.getCountryList(file).entrySet()) {
			try {
				this.curatedDataService.createEntity(entry.getKey(), entry.getValue(), "country");
			} catch (final Exception e) {
				logger.trace(String.format("Not creating country : %s already exist", entry.getKey()));
			}
		}
	}
}
