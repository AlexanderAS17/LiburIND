package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.DestinationCategory;
import liburind.project.model.DestinationCategoryKey;

public interface DestinationCategoryRepository extends MongoRepository<DestinationCategory, DestinationCategoryKey> {

}
