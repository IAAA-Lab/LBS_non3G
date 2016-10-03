package com.geoslab.tracking.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import com.geoslab.tracking.persistence.domain.Location;
import com.geoslab.tracking.persistence.domain.Node;
import com.geoslab.tracking.persistence.domain.Power;

//@RepositoryRestResource(collectionResourceRel = "powers", path = "powers")
//@Repository
public interface PowerRepository extends CrudRepository<Power, Long>{

//	List<Power> findByNodeId(@Param("nodeId") long nodeId);
	
	Power findById(long id);
	
	// Devuelve la última medición (insertada) de energía para un nodo concreto
//	@Query(value="SELECT * FROM POWERS WHERE node = :node ORDER BY id DESC LIMIT 1", nativeQuery=true)
//	Power findNodeLastPower(@Param("node") Node node);
	
	List<Power> findByNodeOrderByTimeDesc(Node node, Pageable pageable);
	List<Power> findByNodeNodeIdOrderByTimeDesc(long nodeId, Pageable pageable);
	List<Power> findByNodeNodeIdOrderByTimeDesc(long nodeId);
	
}
