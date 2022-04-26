package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.Destinations;

public interface DestinationRepository extends MongoRepository<Destinations, String> {

}
