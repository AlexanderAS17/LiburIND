package liburind.project.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "Transportation")
@JsonInclude(Include.NON_NULL)
@Data
public class Transportation {
	
	@Id
	private String transportationId;
	private String transportationName;
	private String transportationPhone;
	private String transCategoryId;

}
