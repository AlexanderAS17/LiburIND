package liburind.project.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "TransportationCategory")
public class TransportationCategory {
	
	@Id
	private String transCategoryId;
	private String transCategoryName;
	private BigDecimal transPrice;
	
	@Transient
	private int jumlah;

}
