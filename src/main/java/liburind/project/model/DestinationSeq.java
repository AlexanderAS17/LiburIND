package liburind.project.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "DestinationSeq")
public class DestinationSeq {

	@Id
	private DestinationSeqKey seqKey;
	private String itineraryId;
	private LocalDateTime seqStartTime;
	private LocalDateTime seqEndTime;
	private BigDecimal seqPrice;
	private String destinationId;
	
	@Transient
	private Destinations destination;

	public static void sortByDate(ArrayList<DestinationSeq> list) {
		list.sort((o1, o2) -> o1.getSeqStartTime().compareTo(o2.getSeqStartTime()));
	}

	public static void sortByDate(List<DestinationSeq> list) {
		list.sort((o1, o2) -> o1.getSeqStartTime().compareTo(o2.getSeqStartTime()));
	}
}
