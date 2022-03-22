package liburind.project.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ItineraryUserKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private String itineraryId;
	private String userId;

	public String getItineraryId() {
		return itineraryId;
	}

	public void setItineraryId(String itineraryId) {
		this.itineraryId = itineraryId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public ItineraryUserKey() {
		super();
	}

	public ItineraryUserKey(String itineraryId, String userId) {
		super();
		this.itineraryId = itineraryId;
		this.userId = userId;
	}

}
