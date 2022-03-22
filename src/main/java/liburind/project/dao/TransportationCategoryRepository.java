package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.TransportationCategory;

public interface TransportationCategoryRepository extends MongoRepository<TransportationCategory, String> {

}
