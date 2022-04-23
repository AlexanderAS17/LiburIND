package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Riview;

public interface RiviewRepository extends MongoRepository<Riview, String> {
	
	@Query("{tableId:'?0'}")
	List<Riview> findByTableId(String tableId);

}
