package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.ItineraryUser;
import liburind.project.model.ItineraryUserKey;

public interface ItineraryUserDao extends MongoRepository<ItineraryUser, ItineraryUserKey> {
	
	@Query("{itineraryId:'?0'}")
	List<ItineraryUser> findByItineraryId(String itineraryId);

}	
