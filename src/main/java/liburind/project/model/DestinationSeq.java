package liburind.project.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "DestinationSequence")
public class DestinationSeq {

	@Id
	private String seqId;
	private String itineraryId;
	private LocalDate seqDate;
	private BigDecimal seqPrice;
	private String destinationId;
	private String duration;
	private String distance;
	
	@Transient
	private Destination destination;

	public static void sortByDate(ArrayList<DestinationSeq> list) {
		list.sort((o1, o2) -> o1.getSeqId().compareTo(o2.getSeqId()));
		list.sort((o1, o2) -> o1.getSeqDate().compareTo(o2.getSeqDate()));
	}

	public static void sortByDate(List<DestinationSeq> list) {
		list.sort((o1, o2) -> o1.getSeqId().compareTo(o2.getSeqId()));
		list.sort((o1, o2) -> o1.getSeqDate().compareTo(o2.getSeqDate()));
	}
}
