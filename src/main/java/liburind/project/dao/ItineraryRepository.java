package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Itinerary;

public interface ItineraryRepository extends MongoRepository<Itinerary, String> {
	
	@Query("{itineraryUserId:'?0'}")
	List<Itinerary> findByUserId(String itineraryUserId);
	
	@Query("{publicFlag: ?0}")
	List<Itinerary> findByPublicFlag(Boolean publicFlag);
	
	@Query("{itineraryCategory: {$regex: '?0', '$options' : 'i'}, publicFlag: true}")
	List<Itinerary> findByCategory(String itineraryName);

}
