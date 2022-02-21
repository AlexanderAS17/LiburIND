package liburind.project.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "TransportationCategory")
@JsonInclude(Include.NON_NULL)
@Data
public class TransportationCategory {
	
	@Id
	private String transCategoryId;
	private String transCategoryName;

}
