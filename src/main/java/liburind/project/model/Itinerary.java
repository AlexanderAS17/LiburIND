package liburind.project.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "Itinerary")
@JsonInclude(Include.NON_NULL)
@Data
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
	
	@Transient
	ArrayList<Riview> riview;

}
