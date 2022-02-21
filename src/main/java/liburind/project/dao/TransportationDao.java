package liburind.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import liburind.project.model.Transportation;

public interface TransportationDao extends JpaRepository<Transportation, String> {

}
