package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	
	@Query("{userEmail:'?0'}")
	User findByEmail(String userEmail);

}
