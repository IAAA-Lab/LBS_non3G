package com.geoslab.persistence.integration;

import com.geoslab.config.JPAConfiguration;
import com.geoslab.persistence.domain.Node;
import com.geoslab.persistence.domain.Operation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import static com.geoslab.persistence.domain.fixture.JPAAssertions.assertTableExists;
import static com.geoslab.persistence.domain.fixture.JPAAssertions.assertTableHasColumn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JPAConfiguration.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class OperationMappingIntegrationTests {

	@Autowired
	EntityManager manager;

	@Test
	public void thatItemCustomMappingWorks() throws Exception {
		assertTableExists(manager, "OPERATIONS");

		assertTableHasColumn(manager, "OPERATIONS", "id");
		
		assertTableHasColumn(manager, "OPERATIONS", "node");
		
		assertTableHasColumn(manager, "OPERATIONS", "operationName");
		assertTableHasColumn(manager, "OPERATIONS", "operationType");
		
		assertTableHasColumn(manager, "OPERATIONS", "p");
		
		assertTableHasColumn(manager, "OPERATIONS", "time");
		
		assertTableHasColumn(manager, "OPERATIONS", "seqId");
		
		assertTableHasColumn(manager, "OPERATIONS", "operationRef");
		
	}

}
