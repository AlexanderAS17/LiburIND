package liburind.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.RiviewRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.model.Riview;
import liburind.project.model.TableCount;

@Service
public class RiviewService {

	@Autowired
	RiviewRepository riviewDao;
	
	@Autowired
	TableCountRepository tableCountDao;

	public float getRating(String tableId) {
		List<Riview> listRvw = riviewDao.findByTableId(tableId);
		float data = 0;
		for (Riview riview : listRvw) {
			data += riview.getRiviewScore();
		}
		return data / listRvw.size();
	}

	public Object save(JsonNode jsonNode) {
		try {
			String riviewDetail = jsonNode.get("riviewDetail").asText();
			String userId = jsonNode.get("userId").asText();
			String tableId = jsonNode.get("tableId").asText();
			Double riviewScore = jsonNode.get("riviewScore").asDouble();
			LocalDateTime tanggal = LocalDateTime.now();
			
			Riview riview = new Riview();
			riview.setRiviewDetail(riviewDetail);
			riview.setUserId(userId);
			riview.setTableId(tableId);
			riview.setRiviewScore(riviewScore.floatValue());
			riview.setRiviewRecordedTime(tanggal);
			
			Optional<TableCount> countOpt = tableCountDao.findById("Riview");
			int count = countOpt.isPresent() ? countOpt.get().getCount() : 0;
			tableCountDao.save(new TableCount("Riview", count + 1));
			
			String id = String.format("RVW%03d", count + 1);
			riview.setRiviewId(id);
			
			riviewDao.save(riview);
			return "Data Saved";
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Check Param");
		}
	}

}
