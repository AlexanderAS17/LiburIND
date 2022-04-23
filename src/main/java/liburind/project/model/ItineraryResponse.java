package liburind.project.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ItineraryResponse {

	private LocalDate seqDate;
	private BigDecimal seqPrice;
	private String itineraryId;
	private List<ItineraryDestination> arrDestination;
}
