package com.geoslab.tracking.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.geoslab.tracking.persistence.domain.Location;
import com.geoslab.tracking.persistence.domain.Node;

public interface LocationRepository extends CrudRepository<Location, Long>{
	
	List<Location> findByNodeNodeId(long nodeId);
	
//	@Query(value="SELECT * FROM LOCATIONS WHERE node = :node ORDER BY id DESC LIMIT 1", nativeQuery=true)
//	@Query("SELECT location FROM Location l1 WHERE l1.node = :node ORDER BY l1.id DESC LIMIT 1")
//	Location findNodeLastLocation(@Param("node") Node node);
	
	// Devuelve el último número de localizaciones indicadas con Pageable para un nodo en concreto
	List<Location> findByNodeOrderByTimeDesc(Node node, Pageable pageable);
	List<Location> findByNodeNodeIdOrderByTimeDesc(long nodeId, Pageable pageable);
	
	// Devuelve el histórico de posiciones de la más reciente a la más antigua
	List<Location> findByNodeNodeIdOrderByTimeDesc(long nodeId);
	
	Location findById(long id);
	
	// Devuelve las localizaciones de los nodos con el status especificado
	List<Location> findByNodeStatus(String status);
	
	// Devuelve la última medición de localización de todos los nodos
	@Query(value=
			"SELECT DISTINCT ON (l1.node) " +
			"l1.node, l1.id, l1.latitude, l1.longitude,  l1.time " +
			"FROM LOCATIONS l1 INNER JOIN " +
			"(SELECT node, MAX(time) AS time FROM LOCATIONS GROUP BY node) l2 " +
			"ON l1.node = l2.node AND l1.time = l2.time", 
			nativeQuery=true)
	List<Location> findAllLastLocation();
	
	// Devuelve la última medición de localización para los nodos con el status indicado
	@Query(value=
			"SELECT DISTINCT ON (l1.node) " +
			"l1.node, l1.id, l1.latitude,  l1.longitude,  l1.time " +
			"FROM LOCATIONS l1 INNER JOIN " +
			"(SELECT node, MAX(time) AS time FROM LOCATIONS GROUP BY node) l2 " +
			"ON l1.node = l2.node AND l1.time = l2.time " +
			"LEFT OUTER JOIN NODES n ON l1.node = n.nodeId WHERE n.status IN :status", 
			nativeQuery=true)
	List<Location> findLastLocationByStatus(@Param("status") Set<String> status);
	
	// Devuelve la última medición de localización para los nodos con el id indicado
	@Query(value="SELECT DISTINCT ON (l1.node) " +
			"l1.node, l1.id, l1.latitude,  l1.longitude,  l1.time " +
			"FROM LOCATIONS l1 INNER JOIN " +
			"(SELECT node, MAX(time) AS time FROM LOCATIONS GROUP BY node) l2 " +
			"ON l1.node = l2.node AND l1.time = l2.time " +
			"WHERE l1.node IN :ids", nativeQuery=true)
	List<Location> findLastLocationByIds(@Param("ids") Set<Long> ids);
	
}
