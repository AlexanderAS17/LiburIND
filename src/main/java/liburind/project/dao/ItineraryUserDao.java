package liburind.project.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import liburind.project.model.ItineraryUser;
import liburind.project.model.ItineraryUserKey;

public interface ItineraryUserDao extends JpaRepository<ItineraryUser, ItineraryUserKey> {
	
	@Query(value = "SELECT * FROM ItineraryUser WHERE itineraryId = :itineraryId", nativeQuery = true)
	List<ItineraryUser> findByItineraryId(@Param("itineraryId") String itineraryId);

}	
