package liburind.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import liburind.project.model.TransportationCategory;

public interface TransportationCategoryDao extends JpaRepository<TransportationCategory, String> {

}
