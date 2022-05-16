package liburind.project.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Destination;

public interface DestinationRepository extends MongoRepository<Destination, String> {
	
	@Query("{destinationName: {$regex: '?0', '$options' : 'i'}}")
	List<Destination> findByName(String destinationName);
	
	@Query("{destinationPlaceId: '?0'}")
	Optional<Destination> findByPlaceId(String destinationPlaceId);

}
