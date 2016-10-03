package com.geoslab.tracking.config;

import com.geoslab.tracking.persistence.repository.LocationRepository;
import com.geoslab.tracking.persistence.repository.NodeRepository;
import com.geoslab.tracking.persistence.repository.OperationRepository;
import com.geoslab.tracking.persistence.repository.PowerRepository;
import com.geoslab.tracking.persistence.repository.SensorDataRepository;
import com.geoslab.tracking.persistence.repository.SensorRepository;

import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import java.sql.SQLException;

@Configuration
@EnableJpaRepositories(basePackages = "com.geoslab.tracking.persistence.repository", 
	includeFilters = {@ComponentScan.Filter(value = { NodeRepository.class }, type = FilterType.ASSIGNABLE_TYPE),
					  @ComponentScan.Filter(value = { LocationRepository.class }, type = FilterType.ASSIGNABLE_TYPE),
					  @ComponentScan.Filter(value = { PowerRepository.class }, type = FilterType.ASSIGNABLE_TYPE),
					  @ComponentScan.Filter(value = { OperationRepository.class }, type = FilterType.ASSIGNABLE_TYPE),
					  @ComponentScan.Filter(value = { SensorRepository.class }, type = FilterType.ASSIGNABLE_TYPE),
					  @ComponentScan.Filter(value = { SensorDataRepository.class }, type = FilterType.ASSIGNABLE_TYPE)
					 })
@EnableTransactionManagement
@PropertySource("classpath:database.properties")
public class JPAConfiguration {
	
    @Autowired
    private Environment environment;	// Variable que maneja los ficheros de propiedades

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }
    
	@Bean
	public DataSource dataSource() throws SQLException {
		// H2 configuration
//		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
//		return builder.setType(EmbeddedDatabaseType.H2).build();
		
		// PostgreSQL configuration
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		
		dataSource.setServerName(environment.getProperty("dburl"));
		dataSource.setDatabaseName(environment.getProperty("dbname"));
		dataSource.setUser(environment.getProperty("dbuser"));
		dataSource.setPassword(environment.getProperty("dbpasswd"));

        return dataSource;

	}

	@Bean
	public EntityManagerFactory entityManagerFactory() throws SQLException {

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(true);		// Activa el output log de las sentencias SQL ejecutadas
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setDatabasePlatform(environment.getProperty("hibernate.dialect"));
		
		
		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.geoslab.tracking.persistence.domain");
		factory.setDataSource(dataSource());
		factory.afterPropertiesSet();
		
		return factory.getObject();
	}

	@Bean
	public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
		return entityManagerFactory.createEntityManager();
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws SQLException {

		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(entityManagerFactory());
		return txManager;
	}

	@Bean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
}
