package liburind.project.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Itinerary")
public class Itinerary {
	
	@Id
	private String itineraryId;
	private String itineraryName;
	private int itineraryRiviewCount;
	private boolean publicFlag;
	private String seqId;
	private String itineraryUserId;
	private LocalDateTime itineraryRecordedTime;
	
	@Transient
	ArrayList<ItineraryUser> user;
	
//	@Transient
//	ArrayList<Riview> riview;

}
