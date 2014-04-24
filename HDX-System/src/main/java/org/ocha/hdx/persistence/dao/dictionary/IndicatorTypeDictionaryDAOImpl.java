package org.ocha.hdx.persistence.dao.dictionary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.ocha.hdx.persistence.entity.configs.ResourceConfiguration;
import org.ocha.hdx.persistence.entity.curateddata.IndicatorType;
import org.ocha.hdx.persistence.entity.dictionary.IndicatorTypeDictionary;
import org.springframework.transaction.annotation.Transactional;

public class IndicatorTypeDictionaryDAOImpl implements IndicatorTypeDictionaryDAO {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<IndicatorTypeDictionary> listIndicatorTypeDictionaries(final Long configId) {
        String qlString = "SELECT itd FROM IndicatorTypeDictionary itd";
        if (configId != null)
            qlString += " WHERE itd.configuration = " + configId;
        qlString += " ORDER BY itd.id";

        final TypedQuery<IndicatorTypeDictionary> query = em.createQuery(qlString, IndicatorTypeDictionary.class);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void createIndicatorTypeDictionary(final String unnormalizedName, final String importer, final IndicatorType indicatorType, final ResourceConfiguration configuration) {
		final IndicatorTypeDictionary indicatorTypeDictionary = new IndicatorTypeDictionary(unnormalizedName, importer, indicatorType, configuration);
		em.persist(indicatorTypeDictionary);
	}

	@Override
	@Transactional
	public void deleteIndicatorTypeDictionary(final IndicatorTypeDictionary indicatorTypeDictionary) {
		em.remove(em.contains(indicatorTypeDictionary) ? indicatorTypeDictionary : em.merge(indicatorTypeDictionary));

	}

	@Override
	@Transactional
	public void deleteIndicatorTypeDictionary(final String unnormalizedName, final String importer) {
		final IndicatorTypeDictionary indicatorTypeDictionary = new IndicatorTypeDictionary(unnormalizedName, importer, null, null);
		em.remove(em.contains(indicatorTypeDictionary) ? indicatorTypeDictionary : em.merge(indicatorTypeDictionary));

	}

}
