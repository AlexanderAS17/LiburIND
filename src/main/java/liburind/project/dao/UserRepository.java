package liburind.project.dao;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import liburind.project.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	
	@Query("{userEmail:'?0'}")
	Optional<User> findByEmail(String userEmail);

}
