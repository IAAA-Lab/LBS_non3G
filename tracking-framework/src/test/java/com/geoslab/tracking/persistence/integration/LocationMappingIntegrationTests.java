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
public class LocationMappingIntegrationTests {

	@Autowired
	EntityManager manager;

	@Test
	public void thatItemCustomMappingWorks() throws Exception {
		assertTableExists(manager, "LOCATIONS");

		assertTableHasColumn(manager, "LOCATIONS", "id");
		
		assertTableHasColumn(manager, "LOCATIONS", "time");
		
		assertTableHasColumn(manager, "LOCATIONS", "latitude");
		assertTableHasColumn(manager, "LOCATIONS", "nsIndicator");
		
		assertTableHasColumn(manager, "LOCATIONS", "longitude");
		assertTableHasColumn(manager, "LOCATIONS", "ewIndicator");
		
		assertTableHasColumn(manager, "LOCATIONS", "node");
	}

}
