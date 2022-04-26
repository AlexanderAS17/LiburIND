package liburind.project.model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Document(collection = "Destination")
public class Destinations {

	@Id
	private String destinationId;
	private String destinationName;
	private BigDecimal destinationRating;
	private String destinationDetail;

	// Google
	private String destinationAddress;
	private String destinationGeometryLat;
	private String destinationGeometryLng;
	private String destinationPhoto;
	private List<String> destinationTimeOpen;
	private String destinationUrl;
	private Integer destinationUsrJmlh;
	private String destinationWebsite;

	@Transient
	private List<Category> destinationCategory;
	
	@Transient
	@JsonIgnore
	private List<String> destinationType;

	public Destinations() {
		super();
	}

	public Destinations(String destinationId, String destinationName, BigDecimal destinationRating,
			String destinationDetail, String destinationAddress, String destinationGeometryLat,
			String destinationGeometryLng, String destinationPhoto, List<String> destinationTimeOpen,
			String destinationUrl, Integer destinationUsrJmlh, String destinationWebsite,
			List<String> destinationType) {
		super();
		this.destinationId = destinationId;
		this.destinationName = destinationName;
		this.destinationRating = destinationRating;
		this.destinationDetail = destinationDetail;
		this.destinationAddress = destinationAddress;
		this.destinationGeometryLat = destinationGeometryLat;
		this.destinationGeometryLng = destinationGeometryLng;
		this.destinationPhoto = destinationPhoto;
		this.destinationTimeOpen = destinationTimeOpen;
		this.destinationUrl = destinationUrl;
		this.destinationUsrJmlh = destinationUsrJmlh;
		this.destinationWebsite = destinationWebsite;
		this.destinationType = destinationType;
	}

}
