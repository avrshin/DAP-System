package org.ocha.hdx.persistence.dao.ckan;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocha.hdx.config.DummyConfigurationCreator;
import org.ocha.hdx.persistence.dao.config.ResourceConfigurationDAO;
import org.ocha.hdx.persistence.entity.ckan.CKANResource;
import org.ocha.hdx.persistence.entity.ckan.CKANResource.WorkflowState;
import org.ocha.hdx.persistence.entity.configs.ResourceConfiguration;
import org.ocha.hdx.validation.itemvalidator.MinMaxValidatorCreator;
import org.ocha.hdx.validation.util.DummyEntityCreatorWrapper;
import org.ocha.hdx.validation.util.DummyEntityCreatorWrapper.DummyEntityCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/ctx-config-test.xml", "classpath:/ctx-core.xml", "classpath:/ctx-dao.xml", "classpath:/ctx-persistence-test.xml" })
public class CKANResourceDAOImplTest {

	@Autowired
	private CKANResourceDAO ckanResourceDAO;
	
	@Autowired
	private DummyConfigurationCreator dummyConfigurationCreator;

	@Autowired
	private DummyEntityCreatorWrapper dummyEntityCreatorWrapper;
	
	@Autowired
	private ResourceConfigurationDAO resourceConfigurationDAO;

	@After
	public void tearDown() {
		ckanResourceDAO.deleteAllCKANResourcesRecords();
	}

	@Test
	@Transactional
	public void testStandardWorkflow() {
		Assert.assertEquals(0, ckanResourceDAO.listCKANResources().size());
		DummyEntityCreator entityCreator	= dummyEntityCreatorWrapper.generateNewEntityCreator();
		entityCreator.createNeededIndicatorTypeAndSource();
		
		final ResourceConfiguration tempConfig	= this.dummyConfigurationCreator.createConfiguration();
		final ResourceConfiguration savedConfig	= resourceConfigurationDAO.createResourceConfig("test", tempConfig.getGeneralConfigEntries(), tempConfig.getIndicatorConfigEntries() );
		Long configurationId = savedConfig.getId();	
		
		final Date revisionTs = new Date();
		ckanResourceDAO.newCKANResourceDetected("newUnitTestResourceId", "newUnitTestResourceRevId", "newUnitTestResourceName", revisionTs, "theParent", "parentDataset_id",
				"parentDataset_revision_id", revisionTs);

		Assert.assertEquals(1, ckanResourceDAO.listCKANResources().size());

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.DETECTED_NEW, r.getWorkflowState());
			Assert.assertEquals(revisionTs, r.getRevision_timestamp());
			Assert.assertNull(r.getDownloadDate());
			
		}

		ckanResourceDAO.flagCKANResourceAsDownloaded("newUnitTestResourceId", "newUnitTestResourceRevId");

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.DOWNLOADED, r.getWorkflowState());
			Assert.assertEquals(revisionTs, r.getRevision_timestamp());
			Assert.assertNotNull(r.getDownloadDate());
		}
		Assert.assertEquals(1, ckanResourceDAO.listCKANResources().size());
		
		final Date revision2Ts = new Date();
		ckanResourceDAO.newCKANResourceDetected("newUnitTestResourceId", "newUnitTestResourceRevId2", "newUnitTestResourceName2", revision2Ts, "theParent", "parentDataset_id",
				"parentDataset_revision_id", revision2Ts);

		// no change expected, the resource already exist
		Assert.assertEquals(2, ckanResourceDAO.listCKANResources().size());

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.DOWNLOADED, r.getWorkflowState());
			Assert.assertEquals(revisionTs, r.getRevision_timestamp());
			Assert.assertNotNull(r.getDownloadDate());
		}

		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId2");
			Assert.assertEquals(WorkflowState.DETECTED_REVISION, r.getWorkflowState());
			Assert.assertEquals(revision2Ts, r.getRevision_timestamp());
			Assert.assertNull(r.getDownloadDate());
		}
		
		ckanResourceDAO.flagCKANResourceAsConfigured("newUnitTestResourceId", "newUnitTestResourceRevId", savedConfig);
		{
			final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
			Assert.assertEquals(WorkflowState.CONFIGURED, r.getWorkflowState());
			Assert.assertNotNull(r.getResourceConfiguration());
			Assert.assertEquals("test", r.getResourceConfiguration().getName());
			Assert.assertNotNull(r.getResourceConfiguration().getGeneralConfigEntries());
			Assert.assertNotNull(r.getResourceConfiguration().getIndicatorConfigEntries());
			Assert.assertTrue(r.getResourceConfiguration().getGeneralConfigEntries().size() > 0);
			Assert.assertTrue(r.getResourceConfiguration().getIndicatorConfigEntries().size() > 0);
		}
		ckanResourceDAO.flagCKANResourceAsConfigured("newUnitTestResourceId", "newUnitTestResourceRevId", null);
		resourceConfigurationDAO.deleteResourceConfiguration(configurationId);
		entityCreator.deleteNeededIndicatorTypeAndSource();
	}

	@Test
	public void testCKANResourceWithEmptyConfig() {
		final Date revisionTs = new Date();
		ckanResourceDAO.newCKANResourceDetected("newUnitTestResourceId", "newUnitTestResourceRevId", "newUnitTestResourceName", revisionTs, "theParent", "parentDataset_id",
				"parentDataset_revision_id", revisionTs);
		final CKANResource r = ckanResourceDAO.getCKANResource("newUnitTestResourceId", "newUnitTestResourceRevId");
		
		assertTrue(r!=null);
	}
}
