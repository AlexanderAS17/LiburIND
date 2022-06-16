package liburind.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.CategoryRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.Category;
import liburind.project.model.TableCount;

@Service
public class CategoryService {

	@Autowired
	CategoryRepository catgDao;

	@Autowired
	TableCountRepository tblDao;

	public Object save(JsonNode jsonNode) {
		String categoryName = jsonNode.get("categoryName").asText();

		if (jsonNode.has("categoryId")) {
			String categoryId = jsonNode.get("categoryId").asText();
			Category category = new Category();
			Optional<Category> catgOpt = catgDao.findById(categoryId);
			if (catgOpt.isPresent()) {
				category = catgOpt.get();
				category.setCategoryName(categoryName);
				catgDao.save(category);
			} else {
				return ResponseEntity.badRequest().body(new Category());
			}
			return category;
		}

		Category category = new Category();
		category.setCategoryName(categoryName);

		Optional<TableCount> countOpt = tblDao.findById("Category");
		int count = countOpt.isPresent() ? countOpt.get().getCount() : 0;
		tblDao.save(new TableCount("Category", count + 1));

		String id = String.format("CTG%03d", count + 1);
		category.setCategoryId(id);

		catgDao.save(category);
		return category;
	}

	public Object get(JsonNode jsonNode) {
		if (jsonNode.has("categoryId")) {
			Optional<Category> catgOpt = catgDao.findById(jsonNode.get("categoryId").asText());
			if (catgOpt.isPresent()) {
				return catgOpt.get();
			} else {
				return ResponseEntity.badRequest().body(new Category());
			}
		} else {
			return catgDao.findAll();
		}
	}

	public Object delete(String categoryId) {
		Optional<Category> catgOpt = catgDao.findById(categoryId);
		if (catgOpt.isPresent()) {
			catgDao.delete(catgOpt.get());
			return "Data Deleted";
		} else {
			return ResponseEntity.badRequest().body("Not Found");
		}
	}

	public Object updateindo(JsonNode jsonNode) {
		List<Category> listCtg = catgDao.findAll();
		for (Category category : listCtg) {
			category.setCategoryName(DataHelper.translate(category.getCategoryName()));
			catgDao.save(category);
		}
		return null;
	}

}
