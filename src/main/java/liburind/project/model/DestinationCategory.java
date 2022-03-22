package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "DestinationCategory")
public class DestinationCategory {
	
	@Id
	private DestinationCategoryKey DestinationCategoryKey;

}
