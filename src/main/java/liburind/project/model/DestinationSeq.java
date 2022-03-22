package liburind.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "DestinationSeq")
public class DestinationSeq {

	@Id
	private DestinationSeqKey destinationSeqKey;
	private LocalDate seqDate;
	private LocalDateTime seqStartTime;
	private LocalDateTime seqEndTime;
	private String seqPrice;
	private String seqTimeEstimation;

	@Transient
	ArrayList<Destination> destination;

}
