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
import com.geoslab.persistence.domain.Operation;
import com.geoslab.persistence.repository.LocationRepository;
import com.geoslab.persistence.repository.NodeRepository;
import com.geoslab.persistence.repository.OperationRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JPAConfiguration.class})
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class OperationRepositoryIntegrationTests {
	
	@Autowired
	OperationRepository operationRepository;
	
	@Autowired
	NodeRepository nodeRepository;

	@Test
	public void thatItemIsInsertedIntoRepoWorks() throws Exception {
		// Se añade un nodo de prueba
		Node node = new Node();
		node.setDeviceVersion("2.0");
		node.setNodeId(1);
		Node insertedNode = nodeRepository.save(node);
		
		Node retrievedNode = nodeRepository.findByNodeId(insertedNode.getNodeId());
		
		assertNotNull(retrievedNode);
		assertEquals("2.0", retrievedNode.getDeviceVersion());
		
		// Se añade una petición y la respuesta correspondiente
		Operation request = new Operation();
		request.setNode(retrievedNode);
		request.setOperationName("DeviceGetInfo");

		Operation request_inserted = operationRepository.save(request);
		
		assertEquals(request_inserted.getOperationRef(), null);
		
		// Respuesta
		Operation answer = new Operation();
		answer.setNode(retrievedNode);
		Operation referenced_operation = 
				operationRepository.findByOperationNameAndNode("DeviceGetInfo", retrievedNode);
		answer.setOperationRef(referenced_operation);
		
		Operation answer_inserted = operationRepository.save(answer);
		
		request_inserted.setOperationRef(answer_inserted);
		Operation new_request_inserted = operationRepository.save(request_inserted);
		
		assertNotNull(new_request_inserted);
		assertNotNull(answer_inserted);
		assertEquals(new_request_inserted.getOperationRef(), answer_inserted);
		assertEquals(answer_inserted.getOperationRef(), new_request_inserted);
	}
}