package liburind.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.CategoryRepository;
import liburind.project.model.Category;

@Service
public class CategoryService {

	@Autowired
	CategoryRepository catgDao;

	public Category save(String categoryId, String categoryName) {
		Category category = new Category();
		Optional<Category> catgOpt = catgDao.findById(categoryId);
		if (catgOpt.isPresent()) {
			category = catgOpt.get();
			category.setCategoryName(categoryName);
			catgDao.save(category);
		} else {
			category = new Category();
			category.setCategoryId(categoryId);
			category.setCategoryName(categoryName);
			catgDao.save(category);
		}
		return category;
	}

	public Object get(JsonNode jsonNode) {
		if (jsonNode.has("categoryId")) {
			Optional<Category> catgOpt = catgDao.findById(jsonNode.get("categoryId").asText());
			if (catgOpt.isPresent()) {
				return catgOpt.get();
			} else {
				return ResponseEntity.badRequest().body("Not Found");
			}
		} else {
			return catgDao.findAll();
		}
	}

	public Object delete(String categoryId) {
		Optional<Category> catgOpt = catgDao.findById(categoryId);
		if (catgOpt.isPresent()) {
			catgDao.delete(catgOpt.get());
			return "Ok";
		} else {
			return ResponseEntity.badRequest().body("Not Found");
		}
	}

}
