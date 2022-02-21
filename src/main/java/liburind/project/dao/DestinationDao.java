package liburind.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import liburind.project.model.Destination;

public interface DestinationDao extends JpaRepository<Destination, String> {

}
