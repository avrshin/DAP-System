package org.ocha.hdx.persistence.dao.dictionary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.ocha.hdx.persistence.entity.configs.ResourceConfiguration;
import org.ocha.hdx.persistence.entity.curateddata.Entity;
import org.ocha.hdx.persistence.entity.dictionary.RegionDictionary;
import org.springframework.transaction.annotation.Transactional;

public class RegionDictionaryDAOImpl implements RegionDictionaryDAO {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<RegionDictionary> listRegionDictionaries(final Long configId) {
        String qlString = "SELECT rd FROM RegionDictionary rd";
        if (configId != null)
            qlString += " WHERE rd.configuration = " + configId;
        qlString += " ORDER BY rd.id";

        final TypedQuery<RegionDictionary> query = em.createQuery(qlString, RegionDictionary.class);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void createRegionDictionary(final String unnormalizedName, final String importer, final Entity entity, final ResourceConfiguration configuration) {
		final RegionDictionary regionDictionary = new RegionDictionary(unnormalizedName, importer, entity, configuration);
		em.persist(regionDictionary);
	}

	@Override
	@Transactional
	public void deleteRegionDictionary(final RegionDictionary regionDictionary) {
		em.remove(em.contains(regionDictionary) ? regionDictionary : em.merge(regionDictionary));
	}

	@Override
	@Transactional
	public void deleteRegionDictionary(final String unnormalizedName, final String importer) {
		final RegionDictionary regionDictionary = new RegionDictionary(unnormalizedName, importer, null, null);
		em.remove(em.contains(regionDictionary) ? regionDictionary : em.merge(regionDictionary));
	}
}
