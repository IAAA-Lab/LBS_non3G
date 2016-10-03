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
import com.geoslab.persistence.domain.Power;
import com.geoslab.persistence.repository.NodeRepository;
import com.geoslab.persistence.repository.PowerRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfiguration.class})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class PowerRepositoryIntegrationTests {
	
	@Autowired
	PowerRepository powersRepository;
	
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

		Power power = new Power();
		power.setTime(100);
		power.setLevel(50);
		power.setNode(retrievedNode);
		
		Power insertedPower = powersRepository.save(power);

		Power retrievedPower = powersRepository.findById(insertedPower.getId());

		assertNotNull(retrievedPower);
		assertEquals(insertedPower.getId(), retrievedPower.getId());
		assertEquals(retrievedNode.getNodeId(), retrievedPower.getNode().getNodeId());
	}
}