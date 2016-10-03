package com.geoslab.persistence.integration;

import com.geoslab.config.JPAConfiguration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.geoslab.persistence.domain.fixture.JPAAssertions.assertTableExists;
import static com.geoslab.persistence.domain.fixture.JPAAssertions.assertTableHasColumn;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JPAConfiguration.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class NodeMappingIntegrationTests {

	@Autowired
	EntityManager manager;

	@Test
	public void thatItemCustomMappingWorks() throws Exception {
		assertTableExists(manager, "NODES");

		assertTableHasColumn(manager, "NODES", "nodeId");
		
		assertTableHasColumn(manager, "NODES", "deviceVersion");
		
		assertTableHasColumn(manager, "NODES", "mode");
		
		assertTableHasColumn(manager, "NODES", "phoneNumber");
		assertTableHasColumn(manager, "NODES", "smsPollTime");
		assertTableHasColumn(manager, "NODES", "smsTransmitPeriod");
		
		assertTableHasColumn(manager, "NODES", "deviceDescription");
		
		assertTableHasColumn(manager, "NODES", "locationMode");
		assertTableHasColumn(manager, "NODES", "locationSysRef");
		assertTableHasColumn(manager, "NODES", "locationDataType");
		assertTableHasColumn(manager, "NODES", "locationRefreshRate");
		
		assertTableHasColumn(manager, "NODES", "powerMode");
		assertTableHasColumn(manager, "NODES", "powerDataUnits");
		assertTableHasColumn(manager, "NODES", "powerDataType");
		assertTableHasColumn(manager, "NODES", "powerMaximum");
		assertTableHasColumn(manager, "NODES", "powerMinimum");

	}

}
