package liburind.project.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import liburind.project.dao.ItineraryDao;
import liburind.project.dao.ItineraryUserDao;
import liburind.project.model.Itinerary;
import liburind.project.model.ItineraryUser;
import liburind.project.model.ItineraryUserKey;

@CrossOrigin
@RestController
@RequestMapping("/itinerary")
public class ItineraryController {
	
	@Autowired
	ItineraryDao itineraryDao;
	
	@Autowired
	ItineraryUserDao itineraryUserDao;

	@GetMapping("/welcome")
	public String welcome() {
		return "Haloo-haloo";
	}
	
	@RequestMapping(value = {"/save"}, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveItinerary(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);
			
			Itinerary itinerary = new Itinerary();
			itinerary.setItineraryId(""); //Next Sequence From DB
			itinerary.setItineraryName(jsonNode.get("name").asText());
			itinerary.setItineraryRiviewCount(0);
			itinerary.setPublicFlag(jsonNode.get("publicFlag").asBoolean());
			itinerary.setSeqId(""); //Create
			itinerary.setItineraryUserId(jsonNode.get("userId").asText());
			itinerary.setItineraryRecordedTime(LocalDateTime.now());
			
			ItineraryUser itineraryUser = new ItineraryUser();
			ItineraryUserKey key = new ItineraryUserKey(itinerary.getItineraryId(), itinerary.getItineraryUserId());
			itineraryUser.setIteneraryUserKey(key);
			
//			System.out.println(itinerary.toString());
			
//			itineraryDao.save(itinerary);
//			itineraryUserDao.save(itineraryUser);
			
			return ResponseEntity.ok().body(itinerary);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
		}
	}
	
	@RequestMapping(value = {"/getuser"}, method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@RequestParam String itineraryId) {
		try {
			List<ItineraryUser> list = itineraryUserDao.findByItineraryId(itineraryId);
			
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
		}
	}
	
	@RequestMapping(value = {"/setuser"}, method = RequestMethod.POST)
	public ResponseEntity<?> setUser(@RequestParam String json, @RequestParam String itineraryId) {
		try {
			String[] data = json.split(",");
			List<ItineraryUser> list = new ArrayList<ItineraryUser>();
			
			for (String string : data) {
				ItineraryUser itineraryUser = new ItineraryUser();
				ItineraryUserKey key = new ItineraryUserKey(itineraryId, string);
				itineraryUser.setIteneraryUserKey(key);
				list.add(itineraryUser);
			}
			
			itineraryUserDao.saveAll(list);
			
			return ResponseEntity.ok(list);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
		}
	}
	
	@RequestMapping(value = {"/getdest"}, method = RequestMethod.GET)
	public ResponseEntity<?> getDestination(@RequestParam String json, @RequestParam String city) {
		try {
			
			
			return ResponseEntity.ok("hae");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
		}
	}

}
