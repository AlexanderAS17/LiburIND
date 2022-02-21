package liburind.project.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "Role")
@JsonInclude(Include.NON_NULL)
@Data
public class Role {
	
	@Id
	private String roleId;
	private String roleName;
	
}
