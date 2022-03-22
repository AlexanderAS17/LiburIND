package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Transportation")
public class Transportation {
	
	@Id
	private String transportationId;
	private String transportationName;
	private String transportationPhone;
	private String transCategoryId;

}
