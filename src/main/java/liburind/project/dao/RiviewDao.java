package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Riview;
import liburind.project.model.User;

public interface RiviewDao extends MongoRepository<Riview, String> {
	
	@Query("{tableId:'?0'}")
	List<User> findByTableId(String tableId);

}
