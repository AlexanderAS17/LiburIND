package liburind.project.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Document(collection = "Itinerary")
public class Itinerary {
	
	@Id
	private String itineraryId;
	private String itineraryName;
	private boolean publicFlag;
	private String itineraryUserId;
	private LocalDate startDate;
	private String detail;
	private LocalDateTime itineraryRecordedTime;
	private String publisher;
	private String itineraryCategory;
	
	@Transient
	@JsonIgnore
	ArrayList<ItineraryUser> user;
	
	@Transient
	private BigDecimal rating;
	
	@Transient
	private LocalDate endDate;

}
