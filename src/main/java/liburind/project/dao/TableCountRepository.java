package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.TableCount;

public interface TableCountRepository extends MongoRepository<TableCount, String> {

}
