package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Document(collection = "DestinationCategory")
public class DestinationCategory {

	@Id
	@JsonIgnore
	private String destinationCategoryId;
	@JsonIgnore
	private String destinationId;
	private String categoryId;

	public DestinationCategory(String destinationCategoryId, String destinationId, String categoryId) {
		super();
		this.destinationCategoryId = destinationCategoryId;
		this.destinationId = destinationId;
		this.categoryId = categoryId;
	}

	public DestinationCategory() {
		super();
	}

}
