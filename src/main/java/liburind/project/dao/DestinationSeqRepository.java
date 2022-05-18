package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.DestinationSeq;

public interface DestinationSeqRepository extends MongoRepository<DestinationSeq, String> {
	
	@Query("{itineraryId: ?0}")
	List<DestinationSeq> findByItineraryId(String itineraryId);

}
