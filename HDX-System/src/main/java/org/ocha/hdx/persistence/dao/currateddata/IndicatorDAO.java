package org.ocha.hdx.persistence.dao.currateddata;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ocha.hdx.persistence.entity.ImportFromCKAN;
import org.ocha.hdx.persistence.entity.curateddata.Entity;
import org.ocha.hdx.persistence.entity.curateddata.Indicator;
import org.ocha.hdx.persistence.entity.curateddata.Indicator.Periodicity;
import org.ocha.hdx.persistence.entity.curateddata.IndicatorType;
import org.ocha.hdx.persistence.entity.curateddata.IndicatorValue;
import org.ocha.hdx.persistence.entity.curateddata.Source;

public interface IndicatorDAO {

	public List<Indicator> listLastIndicators(final int limit);

	public void createIndicator(final Source source, final Entity entity, final IndicatorType type, final Date start, final Date end, final Periodicity periodicity, final IndicatorValue value,
			final String initialValue, final String sourceLink, final ImportFromCKAN importFromCKAN);

	/**
	 * 
	 * @param countryCodes
	 *            optional filter to only get some countries (cannot deal other entityTypes yet)
	 */
	public List<Indicator> listIndicatorsByPeriodicityAndSourceAndIndicatorType(final Periodicity periodicity, final String sourceCode, final String indicatorTypeCode, final List<String> countryCodes);

	public List<Indicator> listIndicatorsByPeriodicityAndEntityAndIndicatorType(final Periodicity periodicity, final String entityType, final String entityCode, final String indicatorTypeCode);

	/**
	 * periodicity is implicitely YEAR
	 * 
	 * @param year
	 * @param sourceCode
	 * @param indicatorTypeCode
	 */
	public List<Indicator> listIndicatorsByYearAndSourceAndIndicatorType(final int year, final String sourceCode, final String indicatorTypeCode, final List<String> countryCodes);

	/**
	 * periodicity is implicitely YEAR
	 * 
	 * @param year
	 * @param sourceCode
	 * @param indicatorTypeCode
	 */
	public List<Indicator> listIndicatorsByYearAndSourceAndIndicatorTypes(final int year, final String sourceCode, final List<String> indicatorTypeCodes);

	/**
	 * Indicators for the country overview.
	 * 
	 * @param countryCode
	 * @param languageCode
	 */
	public List<Object[]> listIndicatorsForCountryOverview(String countryCode, String languageCode);

	/**
	 * Indicators for the country crisis history (for a given year).
	 * 
	 * @param countryCode
	 * @param year
	 * @param languageCode
	 * @return A map of data. Key is year, value is list of indicators for this year (Object[] for flexibility)
	 */
	public Map<Integer, List<Object[]>> listIndicatorsForCountryCrisisHistory(String countryCode, int fromYear, int toYear, String languageCode);

	public Map<Integer, List<Object[]>> listIndicatorsForCountryVulnerability(String countryCode, int fromYear, int toYear, String languageCode);

	public Map<Integer, List<Object[]>> list5YearsIndicatorsForCountry(String countryCode, int fromYear, int toYear, String languageCode);

	/**
	 * very likely to be used by the unit tests only
	 */
	public void deleteAllIndicators();

	public void deleteAllIndicatorsFromImport(long importId);

	public void deleteIndicator(final long indicatorId);

	public void deleteIndicators(final List<Long> indList);

	/**
	 * Based on the existing indicators
	 * 
	 * @param year
	 * @param indicatorTypeCode
	 * 
	 * @return the list of sources for which there is ar least one matching record in Indicators (Year, IndicatorType)
	 */
	public List<String> getExistingSourcesCodesForYearAndIndicatorType(final int year, final String indicatorTypeCode);

	/**
	 * Based on the existing indicators
	 * 
	 * @param indicatorTypeCode
	 * 
	 * @return the list of sources for which there is ar least one matching record in Indicators (IndicatorType)
	 */
	public List<String> getExistingSourcesCodesForIndicatorType(final String indicatorTypeCode);

}
