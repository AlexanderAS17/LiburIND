package liburind.project.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ItineraryDestination {

	private String seqId;
	private String destinationId;
	private Destinations destination;
	private LocalDateTime seqStartTime;
	private LocalDateTime seqEndTime;

}
