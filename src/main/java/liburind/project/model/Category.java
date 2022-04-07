package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Document(collection = "Category")
public class Category {
	
	@Id
	@JsonIgnore
	private String categoryId;
	private String categoryName;
	
}
