package liburind.project.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DestinationSeqKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String seqId;
	private LocalDate seqDate;
	private LocalDateTime seqStartTime;
	private String destinationId;

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}

	public LocalDate getSeqDate() {
		return seqDate;
	}

	public void setSeqDate(LocalDate seqDate) {
		this.seqDate = seqDate;
	}

	public LocalDateTime getSeqStartTime() {
		return seqStartTime;
	}

	public void setSeqStartTime(LocalDateTime seqStartTime) {
		this.seqStartTime = seqStartTime;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public DestinationSeqKey(String seqId, LocalDate seqDate, LocalDateTime seqStartTime, String destinationId) {
		super();
		this.seqId = seqId;
		this.seqDate = seqDate;
		this.seqStartTime = seqStartTime;
		this.destinationId = destinationId;
	}

	public DestinationSeqKey() {
		super();
	}

}
