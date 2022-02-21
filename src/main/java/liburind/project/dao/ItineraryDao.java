package liburind.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import liburind.project.model.Itinerary;

public interface ItineraryDao extends JpaRepository<Itinerary, String> {

}
