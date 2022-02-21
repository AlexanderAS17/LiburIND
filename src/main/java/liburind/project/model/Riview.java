package liburind.project.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Entity
@Table(name = "Riview")
@JsonInclude(Include.NON_NULL)
@Data
public class Riview {
	
	@Id
	private String riviewId;
	private Integer riviewScore;
	private String riviewDetail;
	private LocalDateTime riviewRecordedTime;
	private String userId;
	private String tableId;

}
