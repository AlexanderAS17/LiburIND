package liburind.project.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "User")
@JsonInclude(Include.NON_NULL)
@Data
public class User {
	
	@Id
	private String userId;
	private String userName;
	private String userEmail;
	private String userPassword;
	private String roleId;

}
