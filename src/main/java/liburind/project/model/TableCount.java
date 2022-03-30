package liburind.project.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "TableCount")
public class TableCount {

	@Id
	String tableName;
	int count;

	public TableCount(String tableName, int count) {
		super();
		this.tableName = tableName;
		this.count = count;
	}

	public TableCount() {
		super();
	}

}
