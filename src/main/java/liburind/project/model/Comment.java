package liburind.project.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
@Document(collection = "Comment")
public class Comment {
	
	@Id
	@JsonIgnore
	private String commentId;
	private String userId;
	private String userName;
	private String itineraryId;
	private LocalDateTime commentTime;
	private String message;
	
	public static void sortByDate(List<Comment> list) {
		list.sort((o1, o2) -> o1.getCommentTime().compareTo(o2.getCommentTime()));
	}
	
	public static Comment mapJson(JsonNode jsonNode) {
		String userId = jsonNode.has("userId") ? jsonNode.get("userId").asText() : "";
		String itineraryId = jsonNode.has("itineraryId") ? jsonNode.get("itineraryId").asText() : "";
		String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "";

		Comment comment = new Comment();
		comment.setUserId(userId);
		comment.setItineraryId(itineraryId);
		comment.setMessage(message);
		comment.setCommentTime(LocalDateTime.now());
		return comment;
	}

}
