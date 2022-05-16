package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Transportation;

public interface TransportationRepository extends MongoRepository<Transportation, String> {

	@Query("{transCategoryId:'?0'}")
	List<Transportation> findByCategory(String category);
	
	@Query("{flagUsed: false}")
	List<Transportation> findAllAvailable();
	
	@Query("{itineraryId:'?0'}")
	List<Transportation> findByItinerary(String itineraryId);

}
