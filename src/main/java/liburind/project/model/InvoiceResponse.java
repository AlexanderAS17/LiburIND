package liburind.project.model;

import java.math.BigDecimal;
import java.util.ArrayList;

import lombok.Data;

@Data
public class InvoiceResponse {

	private ArrayList<Transportation> transArr;
	private BigDecimal priceSum;
	
}
