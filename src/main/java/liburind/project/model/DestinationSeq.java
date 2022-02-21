package liburind.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "DestinationSeq")
@JsonInclude(Include.NON_NULL)
@Data
public class DestinationSeq {
	
	@EmbeddedId
	private DestinationSeqKey destinationSeqKey;
	private LocalDate seqDate;
	private LocalDateTime seqStartTime;
	private LocalDateTime seqEndTime;
	private String seqPrice;
	private String seqTimeEstimation;
	
	@Transient
	ArrayList<Destination> destination;

}
