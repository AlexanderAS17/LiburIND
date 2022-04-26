package liburind.project.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
	
	@Query("{categoryName: ?0}")
	Optional<Category> findByName(String categoryName);

}
