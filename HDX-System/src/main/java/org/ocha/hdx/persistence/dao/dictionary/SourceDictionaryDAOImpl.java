package org.ocha.hdx.persistence.dao.dictionary;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.ocha.hdx.persistence.entity.curateddata.Source;
import org.ocha.hdx.persistence.entity.dictionary.SourceDictionary;
import org.springframework.transaction.annotation.Transactional;

public class SourceDictionaryDAOImpl implements SourceDictionaryDAO {

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<SourceDictionary> listSourceDictionaries() {
		final TypedQuery<SourceDictionary> query = em.createQuery("SELECT sd FROM SourceDictionary sd ORDER BY sd.id", SourceDictionary.class);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void createSourceDictionary(final String unnormalizedName, final String importer, final Source source) {
		final SourceDictionary sourceDictionary = new SourceDictionary(unnormalizedName, importer, source);
		em.persist(sourceDictionary);
	}

	@Override
	@Transactional
	public void deleteSourceDictionary(final SourceDictionary sourceDictionary) {
		em.remove(em.contains(sourceDictionary) ? sourceDictionary : em.merge(sourceDictionary));
	}

	@Override
	@Transactional
	public void deleteSourceDictionary(final String unnormalizedName, final String importer) {
		final SourceDictionary sourceDictionary = new SourceDictionary(unnormalizedName, importer, null);
		em.remove(em.contains(sourceDictionary) ? sourceDictionary : em.merge(sourceDictionary));
	}

	@Override
	public List<SourceDictionary> getSourceDictionariesByImporter(final String importer) {
		final TypedQuery<SourceDictionary> query = em.createQuery("SELECT sd FROM SourceDictionary sd WHERE sd.id.importer = :importer ORDER BY sd.id", SourceDictionary.class).setParameter(
				"importer", importer);
		return query.getResultList();
	}

	@Override
	@Transactional
	public void deleteAllSourceDictionaries() {
		em.createQuery("DELETE FROM SourceDictionary").executeUpdate();
	}

}
