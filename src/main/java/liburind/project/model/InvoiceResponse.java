package liburind.project.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import lombok.Data;

@Data
public class InvoiceResponse {

	private String namaUser;
	private ArrayList<Transportation> transArr;
	private BigDecimal priceSum;
	private String pickUpPlace;
	private LocalDate startDate;
	private Integer duration;
	
}
