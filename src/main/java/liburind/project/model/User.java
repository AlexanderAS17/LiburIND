package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Document(collection = "User")
public class User {
	
	@Id
	private String userId;
	private String userName;
	private String userEmail;
	@JsonIgnore
	private String userPassword;
	@JsonIgnore
	private Boolean flagActive;
	@JsonIgnore
	private String key;
	
	public User(String userId, String userName, String userEmail, String userPassword) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
	}

	public User() {
		super();
	}
	
}
