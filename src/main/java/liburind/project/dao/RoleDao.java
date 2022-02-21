package liburind.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import liburind.project.model.Role;

public interface RoleDao extends JpaRepository<Role, String> {

}
