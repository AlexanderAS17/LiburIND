package liburind.project.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ItineraryDestination {

	private String seqId;
	private String destinationId;
	private String destinationName;
	private LocalDateTime seqStartTime;
	private LocalDateTime seqEndTime;

}
