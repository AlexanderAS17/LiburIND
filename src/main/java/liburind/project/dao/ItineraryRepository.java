package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.Itinerary;

public interface ItineraryRepository extends MongoRepository<Itinerary, String> {

}
