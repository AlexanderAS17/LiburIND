package liburind.project.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import liburind.project.model.Category;

public interface CategoryDao extends JpaRepository<Category, String> {

}
