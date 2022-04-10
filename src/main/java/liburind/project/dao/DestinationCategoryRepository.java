package liburind.project.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.DestinationCategory;

public interface DestinationCategoryRepository extends MongoRepository<DestinationCategory, String> {
	
	@Query("{destinationId:'?0', categoryId:'?1'}")
	Optional<DestinationCategory> findByKey(String destinationId, String categoryId);
	
	@Query("{destinationId:'?0'}")
	List<DestinationCategory> findByDestination(String destinationId);
	
	@Query("{categoryId:'?0'}")
	List<DestinationCategory> findByCategory(String categoryId);

}
