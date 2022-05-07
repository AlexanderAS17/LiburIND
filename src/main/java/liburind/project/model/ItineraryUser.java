package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "ItineraryUser")
public class ItineraryUser {
	
	@Id
	private ItineraryUserKey iteneraryUserKey;
	private Boolean activeFlag;

	public ItineraryUser(ItineraryUserKey iteneraryUserKey) {
		super();
		this.iteneraryUserKey = iteneraryUserKey;
		this.activeFlag = true;
	}

	public ItineraryUser() {
		super();
		this.activeFlag = true;
	}
	
	
}
