package liburind.project.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "DestinationCategory")
@JsonInclude(Include.NON_NULL)
@Data
public class DestinationCategory {
	
	@EmbeddedId
	private DestinationCategoryKey DestinationCategoryKey;

}
