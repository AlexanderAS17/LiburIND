package liburind.project.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import liburind.project.model.User;

public interface UserDao extends JpaRepository<User, String> {
	
	@Query(value = "SELECT * FROM user WHERE userEmail = :email", nativeQuery = true)
	Optional<User> findByEmail(@Param("email") String email);

}
