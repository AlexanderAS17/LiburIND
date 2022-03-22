package liburind.project.model;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;


@Data
@Document(collection = "Destination")
public class Destination {
	
	@Id
	private String destinationId;
	private String destinationName;
	private String destinationCity;
	private String destinationScore;
	
	@Transient
	private ArrayList<Riview> riview;
	
	@Transient
	private ArrayList<DestinationCategory> category;

}
