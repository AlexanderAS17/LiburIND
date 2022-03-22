package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Role")
public class Role {
	
	@Id
	private String roleId;
	private String roleName;
	
}
