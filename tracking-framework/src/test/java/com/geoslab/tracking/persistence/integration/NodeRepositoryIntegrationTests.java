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

import com.geoslab.persistence.domain.Node;
import com.geoslab.persistence.repository.NodeRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfiguration.class})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class NodeRepositoryIntegrationTests {
	
	@Autowired
	NodeRepository nodeRepository;
	
	@Autowired
	NodeRepository nodeRepository2;

	@Test
	public void thatItemIsInsertedIntoRepoWorks() throws Exception {
		Node node = new Node();
		node.setNodeId(1000);
		node.setDeviceVersion("1.0");

		Node insertedNode = nodeRepository.save(node);

		Node retrievedNode = nodeRepository.findByNodeId(insertedNode.getNodeId());

		assertNotNull(retrievedNode);
		assertEquals(insertedNode.getNodeId(), retrievedNode.getNodeId());
		assertEquals("1.0", retrievedNode.getDeviceVersion());
		
		// Existing node
		Node eNode = nodeRepository.findByNodeId(4);
		assertNotNull(eNode);
		assertEquals(eNode.getStatus(), "dead");
		eNode.setStatus("alive");
		nodeRepository.save(eNode);
		
		Node eNodeAgain = nodeRepository2.findByNodeId(4);
		assertNotNull(eNodeAgain);
		assertEquals(eNodeAgain.getStatus(), "alive");
		
		
	}
}
