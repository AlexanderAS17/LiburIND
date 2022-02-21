package liburind.project.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import liburind.project.dao.RiviewDao;
import liburind.project.model.Riview;
import liburind.project.model.User;

@CrossOrigin
@RestController
@RequestMapping("/riview")
public class RiviewController {
	
	@Autowired
	RiviewDao riviewDao;
	
	@RequestMapping(value = {"/list"}, method = RequestMethod.GET)
	public ResponseEntity<?> getRiview(@RequestParam String tableId) {
		try {
			List<User> userList = riviewDao.findByTableId(tableId);
			
			return ResponseEntity.ok(userList);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
		}
	}
	
	@RequestMapping(value = {"/save"}, method = RequestMethod.POST)
	public ResponseEntity<?> saveRiview(@RequestParam Integer score, @RequestParam String detail, @RequestParam String userId, @RequestParam String tableId) {
		try {
			Riview riview = new Riview();
			riview.setRiviewId(""); //Next Sequence From DB
			riview.setRiviewScore(score);
			riview.setRiviewDetail(detail);
			riview.setRiviewRecordedTime(LocalDateTime.now());
			riview.setUserId(userId);
			riview.setTableId(tableId);
			
			riviewDao.save(riview);
			
			return ResponseEntity.ok(riview);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
		}
	}

}
