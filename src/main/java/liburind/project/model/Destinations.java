package liburind.project.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.CaseUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

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
	private String destinationPlaceId;
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
			List<String> destinationType, String destinationPlaceId) {
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
		this.destinationPlaceId = destinationPlaceId;
	}

	public static Destinations mapJson(JsonNode jsonNode) {
		String destinationId = jsonNode.has("destinationId") ? jsonNode.get("destinationId").asText() : "";
		String destinationName = jsonNode.has("destinationName") ? jsonNode.get("destinationName").asText() : "";
		BigDecimal destinationRating = jsonNode.has("destinationRating")
				? new BigDecimal(jsonNode.get("destinationRating").asText())
				: BigDecimal.ZERO;
		String destinationDetail = jsonNode.has("destinationDetail") ? jsonNode.get("destinationDetail").asText() : "";

		// Google
		String destinationAddress = jsonNode.has("destinationAddress") ? jsonNode.get("destinationAddress").asText()
				: "";
		String destinationGeometryLat = jsonNode.has("destinationGeometryLoc")
				? jsonNode.get("destinationGeometryLoc").get("lat").asText()
				: "";
		String destinationGeometryLng = jsonNode.has("destinationGeometryLoc")
				? jsonNode.get("destinationGeometryLoc").get("lng").asText()
				: "";
		String destinationPhoto = jsonNode.has("destinationPhoto") ? jsonNode.get("destinationPhoto").asText() : "";
		List<String> destinationTimeOpen = new ArrayList<String>();
		JsonNode arrNode = jsonNode.get("destinationTimeOpen");
		if (arrNode.isArray()) {
			for (final JsonNode objNode : arrNode) {
				destinationTimeOpen.add(objNode.asText());
			}
		}
		String destinationUrl = jsonNode.has("destinationUrl") ? jsonNode.get("destinationUrl").asText() : "";
		Integer destinationUsrJmlh = jsonNode.has("destinationUsrRating") ? jsonNode.get("destinationUsrRating").asInt()
				: 0;
		String destinationWebsite = jsonNode.has("destinationWebsite") ? jsonNode.get("destinationWebsite").asText()
				: "";
		List<String> destinationType = new ArrayList<String>();
		arrNode = jsonNode.get("destinationType");
		if (arrNode.isArray()) {
			for (final JsonNode objNode : arrNode) {
				destinationType.add(objNode.asText());
			}
		}
		String destinationPlaceId = jsonNode.has("destinationPlaceId") ? jsonNode.get("destinationPlaceId").asText()
				: "";

		for (int i = 0; i < destinationType.size(); i++) {
			destinationType.set(i, CaseUtils.toCamelCase(destinationType.get(i), true, ' ').replaceAll("_", " "));
		}

		return new Destinations(destinationId, destinationName, destinationRating, destinationDetail,
				destinationAddress, destinationGeometryLat, destinationGeometryLng, destinationPhoto,
				destinationTimeOpen, destinationUrl, destinationUsrJmlh, destinationWebsite, destinationType, destinationPlaceId);
	}
}
