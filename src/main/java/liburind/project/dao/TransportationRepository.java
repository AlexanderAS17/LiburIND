package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Transportation;

public interface TransportationRepository extends MongoRepository<Transportation, String> {

	@Query("{transCategoryId:'?0'}")
	List<Transportation> findByCtg(String category);
	
	@Query("{flagUsed: false}")
	List<Transportation> findAllAvailable();

}
