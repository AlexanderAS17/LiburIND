package liburind.project.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "Riview")
public class Riview {
	
	@Id
	private String riviewId;
	private Integer riviewScore;
	private String riviewDetail;
	private LocalDateTime riviewRecordedTime;
	private String userId;
	private String tableId;

}
