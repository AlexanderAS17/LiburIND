package liburind.project.model;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "Destination")
@JsonInclude(Include.NON_NULL)
@Data
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
