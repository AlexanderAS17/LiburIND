package liburind.project.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DestinationCategoryKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private String destinationId;
	private String categoryId;

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationCatgId) {
		this.destinationId = destinationCatgId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DestinationCategoryKey() {
		super();
	}

	public DestinationCategoryKey(String destinationId, String categoryId) {
		super();
		this.destinationId = destinationId;
		this.categoryId = categoryId;
	}

}
