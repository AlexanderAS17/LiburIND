package liburind.project.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Destinations;

public interface DestinationRepository extends MongoRepository<Destinations, String> {
	
	@Query("{destinationName: {$regex: '?0'}}")
	List<Destinations> findByName(String destinationName);
	
	@Query("{destinationPlaceId: '?0'}")
	Optional<Destinations> findByPlaceId(String destinationPlaceId);

}
