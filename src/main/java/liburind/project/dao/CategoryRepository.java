package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {

}
