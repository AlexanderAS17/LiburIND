package liburind.project.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Transportation")
public class Transportation {
	
	@Id
	private String transportationId;
	private String transportationName;
	private String transportationPhone;
	private BigDecimal transportationPrice;
	private String transCategoryId;
	private String itineraryId;
	private LocalDate startDate;
	private LocalDate endDate;
	private Boolean flagUsed;

}
