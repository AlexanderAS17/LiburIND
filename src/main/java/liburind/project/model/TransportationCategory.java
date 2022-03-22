package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "TransportationCategory")
public class TransportationCategory {
	
	@Id
	private String transCategoryId;
	private String transCategoryName;

}
