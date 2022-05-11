package liburind.project.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Document(collection = "Transportation")
public class Transportation {
	
	@Id
	@JsonIgnore
	private String transportationId;
	private String transportationName;
	private String transportationPhone;
	@JsonIgnore
	private String transCategoryId;
	@JsonIgnore
	private String itineraryId;
	@JsonIgnore
	private String userId;
	@JsonIgnore
	private LocalDate startDate;
	@JsonIgnore
	private LocalDate endDate;
	@JsonIgnore
	private Boolean flagUsed;

}
