package liburind.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import liburind.project.model.DestinationCategory;
import liburind.project.model.DestinationCategoryKey;

public interface DestinationCategoryDao extends JpaRepository<DestinationCategory, DestinationCategoryKey> {

}
