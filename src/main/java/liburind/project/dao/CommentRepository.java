package liburind.project.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
	
	@Query("{itineraryId: ?0}")
	List<Comment> findByitineraryId(String itineraryId);

}
