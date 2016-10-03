package com.geoslab.tracking.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.geoslab.tracking.persistence.domain.Location;
import com.geoslab.tracking.persistence.domain.Node;
import com.geoslab.tracking.persistence.domain.Sensor;
import com.geoslab.tracking.persistence.domain.SensorData;

public interface SensorDataRepository extends CrudRepository<SensorData, Long>{
	
	List<SensorData> findByNodeNodeIdAndSensorSensorId(long nodeId, long sensorId);
	
	// Devuelve el último número de datos de sensor indicados con Pageable para un nodo en concreto
	List<SensorData> findByNodeAndSensorOrderByTimeDesc(Node node, Sensor sensor, Pageable pageable);
	List<SensorData> findByNodeNodeIdAndSensorSensorIdOrderByTimeDesc(long nodeId, long sensorId);
	List<SensorData> findByNodeNodeIdAndSensorSensorIdOrderByTimeDesc(long nodeId, long sensorId, Pageable pageable);
}