package org.ocha.dap.service;

import java.util.Date;
import java.util.List;

import org.ocha.dap.importer.PreparedIndicator;
import org.ocha.dap.persistence.entity.ImportFromCKAN;
import org.ocha.dap.persistence.entity.curateddata.Entity;
import org.ocha.dap.persistence.entity.curateddata.EntityType;
import org.ocha.dap.persistence.entity.curateddata.Indicator;
import org.ocha.dap.persistence.entity.curateddata.Indicator.Periodicity;
import org.ocha.dap.persistence.entity.curateddata.IndicatorType;
import org.ocha.dap.persistence.entity.curateddata.Source;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.DataTable;

public interface CuratedDataService {

	public List<EntityType> listEntityTypes();

	public void addEntityType(String code, String name);

	public List<Entity> listEntities();

	public void addEntity(final String code, final String name, final String entityTypeCode);

	public List<IndicatorType> listIndicatorTypes();

	public void addIndicatorType(String code, String name, String unit);

	public List<Source> listSources();

	public void addSource(String code, String name);

	/**
	 * Add an indicator for the provided parameters
	 * 
	 * The importFromCKAN param is not provided. This will be added to the default "dummy" import
	 */
	public void addIndicator(final String sourceCode, final long entityId, final String indicatorTypeCode, final Date start, final Date end, final Periodicity periodicity, final boolean numeric,
			final String value, final String initialValue);

	public void addIndicator(final PreparedIndicator preparedIndicator, ImportFromCKAN importFromCKAN);

	public List<Indicator> listLastIndicators(final int limit);

	public DataTable listIndicatorsByPeriodicityAndSourceAndIndicatorType(final Periodicity periodicity, final String sourceCode, final String indicatorTypeCode) throws TypeMismatchException;

}
