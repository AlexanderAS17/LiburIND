package liburind.project.model;

import java.io.Serializable;

public class DestinationSeqKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String seqId;
	private String seqDay;
	private String destinationId;

	public String getSeqDay() {
		return seqDay;
	}

	public void setSeqDay(String seqDay) {
		this.seqDay = seqDay;
	}

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public DestinationSeqKey() {
		super();
	}

	public DestinationSeqKey(String seqId, String destinationId) {
		super();
		this.seqId = seqId;
		this.destinationId = destinationId;
	}

}
