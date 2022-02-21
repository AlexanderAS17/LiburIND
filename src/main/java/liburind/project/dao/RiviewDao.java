package liburind.project.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import liburind.project.model.Riview;
import liburind.project.model.User;

public interface RiviewDao extends JpaRepository<Riview, String> {
	
	@Query(value = "SELECT * FROM riview WHERE tableId = :tableId", nativeQuery = true)
	List<User> findByTableId(@Param("tableId") String tableId);

}
