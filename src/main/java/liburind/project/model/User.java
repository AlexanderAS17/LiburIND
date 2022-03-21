package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "User")
public class User {
	
	@Id
	private String userId;
	private String userName;
	private String userEmail;
	private String userPassword;
	private String roleId;
	
	public User(String userId, String userName, String userEmail, String userPassword, String roleId) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.roleId = roleId;
	}

	public User() {
		super();
	}
	
	
}
