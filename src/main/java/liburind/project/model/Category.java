package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Category")
public class Category {
	
	@Id
	private String categoryId;
	private String categoryName;
	
}
