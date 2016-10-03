package com.geoslab.tracking.persistence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.geoslab.tracking.persistence.domain.Node;
import com.geoslab.tracking.persistence.domain.Operation;

//@RepositoryRestResource(collectionResourceRel = "locations", path = "locations")
//@Repository
public interface OperationRepository extends CrudRepository<Operation, Long>{
	
//	List<Location> findByNodeId(@Param("nodeId") long nodeId);
	
	Operation findById(long id);
	
	Operation findBySeqId(long seqId);
	
//	@Query(value="select * from OPERATIONS where operationName = :opName AND nodeId = :node", nativeQuery=true)
	Operation findByOperationNameAndNode(String operationName, Node nodeId);
	
	// Devuelve el último número de operaciones ordenadas por seqId indicadas con Pageable 
	// para un nodo en concreto
//	List<Operation> findByNodeOrderBySeqIdDesc(Node node, Pageable pageable);
	
	@Query("SELECT MAX(seqId) FROM Operation")
	long findMaxSeqId();
	
	Operation findByNodeAndSeqIdAndOperationClass(Node node, long seqId, String operationClass);

}
