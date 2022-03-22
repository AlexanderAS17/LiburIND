package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.Transportation;

public interface TransportationRepository extends MongoRepository<Transportation, String> {

}
