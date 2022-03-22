package liburind.project.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import liburind.project.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {

}
