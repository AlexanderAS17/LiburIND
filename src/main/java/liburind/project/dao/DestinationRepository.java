package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Destinations;

public interface DestinationRepository extends MongoRepository<Destinations, String> {
	
	@Query("{destinationName: {$regex: '?0'}}")
	List<Destinations> findByName(String destinationName);

}
