package liburind.project.model;

import java.io.Serializable;
import java.time.LocalDate;

public class DestinationSeqKey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String seqId;
	private LocalDate seqDate;

	public DestinationSeqKey(String seqId, LocalDate seqDate) {
		super();
		this.seqId = seqId;
		this.seqDate = seqDate;
	}

	public DestinationSeqKey() {
		super();
	}

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

}
