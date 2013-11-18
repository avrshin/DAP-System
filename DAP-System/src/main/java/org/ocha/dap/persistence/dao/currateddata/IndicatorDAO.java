package org.ocha.dap.persistence.dao.currateddata;

import java.util.Date;
import java.util.List;

import org.ocha.dap.persistence.entity.ImportFromCKAN;
import org.ocha.dap.persistence.entity.curateddata.Entity;
import org.ocha.dap.persistence.entity.curateddata.Indicator;
import org.ocha.dap.persistence.entity.curateddata.Indicator.Periodicity;
import org.ocha.dap.persistence.entity.curateddata.IndicatorType;
import org.ocha.dap.persistence.entity.curateddata.Source;

public interface IndicatorDAO {

	public List<Indicator> listLastIndicators(final int limit);

	public void addIndicator(final Source source, final Entity entity, final IndicatorType type, final Date start, final Date end, final Periodicity periodicity, final boolean numeric,
			final String value, final String initialValue, final ImportFromCKAN importFromCKAN);

}
