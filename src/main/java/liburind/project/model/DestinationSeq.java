package liburind.project.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "DestinationSeq")
public class DestinationSeq {

	@Id
	private DestinationSeqKey destinationSeqKey;
	private String itineraryId;
	private LocalDateTime seqEndTime;
	private String seqPrice;
	private String detail;

}
