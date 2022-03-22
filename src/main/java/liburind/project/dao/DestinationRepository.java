package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.Destination;

public interface DestinationRepository extends MongoRepository<Destination, String> {

}
