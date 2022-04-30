package liburind.project.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ItineraryDestination {

	private String seqId;
	private LocalDateTime seqStartTime;
	private LocalDateTime seqEndTime;

	//Destinasi
	private String destinationId;
	private String destinationName;
	private BigDecimal destinationRating;
	private String destinationDetail;
	private String destinationAddress;
	private String destinationPlaceId;
	private String destinationGeometryLat;
	private String destinationGeometryLng;
	private String destinationPhoto;
	private List<String> destinationTimeOpen;
	private String destinationUrl;
	private Integer destinationUsrJmlh;
	private String destinationWebsite;
	
}
