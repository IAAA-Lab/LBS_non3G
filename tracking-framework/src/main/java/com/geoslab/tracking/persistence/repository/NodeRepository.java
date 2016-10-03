package com.geoslab.tracking.persistence.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.geoslab.tracking.persistence.domain.Node;

//@RepositoryRestResource(collectionResourceRel = "nodes", path = "nodes")
//CrudRepository
//@Repository
//@Cacheable(false)
public interface NodeRepository extends JpaRepository<Node, Long>{
	Node findByNodeId(long nodeId);
	
	List<Node> findByStatusIn(Set<String> status);

	List<Node> findByNodeIdIn(Set<Long> ids);

	Node findByPhoneNumber(String phoneNumber);

	Node findByCloudId(String cloudId);
	
	Node findByDeviceDescription(String deviceDescription);
	
	@Query(value=
			"SELECT * FROM NODES n WHERE n.nodeId IN :ids", 
			nativeQuery=true)
	List<Node> findByNodesIds(@Param("ids") Set<Long> ids);

}
