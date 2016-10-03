package com.geoslab.persistence.integration;

import com.geoslab.config.JPAConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

import com.geoslab.persistence.domain.Location;
import com.geoslab.persistence.domain.Node;
import com.geoslab.persistence.repository.LocationRepository;
import com.geoslab.persistence.repository.NodeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfiguration.class})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class LocationRepositoryIntegrationTests {
	
	@Autowired
	LocationRepository locationsRepository;
	
	@Autowired
	NodeRepository nodesRepository;

	@Test
	public void thatItemIsInsertedIntoRepoWorks() throws Exception {

		Node node = new Node();
		node.setDeviceVersion("2.0");
		node.setNodeId(1);
		Node insertedNode = nodesRepository.save(node);
		
		
		Node retrievedNode = nodesRepository.findByNodeId(insertedNode.getNodeId());
		
		assertNotNull(retrievedNode);
		assertEquals("2.0", retrievedNode.getDeviceVersion());

		Location location = new Location("0.83123", null, "0.1234", null, retrievedNode, 100);
		
		Location insertedLocation = locationsRepository.save(location);

		Location retrievedLocation = locationsRepository.findById(insertedLocation.getId());

		assertNotNull(retrievedLocation);
		assertEquals(insertedLocation.getId(), retrievedLocation.getId());
		assertEquals(retrievedNode.getNodeId(), retrievedLocation.getNode().getNodeId());
	}
}