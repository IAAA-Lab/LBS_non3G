package com.geoslab.tracking.persistence.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.geoslab.tracking.persistence.domain.Node;
import com.geoslab.tracking.persistence.domain.Sensor;

public interface SensorRepository extends CrudRepository<Sensor, Long>{
	
	List<Sensor> findByNodeNodeId(long nodeId);
	
	List<Sensor> findByNodeNodeIdAndStatus(long nodeId, boolean status);
	
	Sensor findByNodeNodeIdAndSensorId(long nodeId, long sensorId);
	
	Sensor findByNodeAndSensorId(Node node, long sensorId);
	
}
