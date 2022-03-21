package liburind.project.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import liburind.project.model.Itinerary;
import liburind.project.model.ItineraryUser;
import liburind.project.model.ItineraryUserKey;

@CrossOrigin
@RestController
@RequestMapping("/itinerary")
public class ItineraryController {
	
	@RequestMapping(value = {"/object"}, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> object(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);
			
			Itinerary itinerary = new Itinerary();
			itinerary.setItineraryId("IT001"); //Next Sequence From DB
			itinerary.setItineraryName(jsonNode.get("name").asText());
			itinerary.setItineraryRiviewCount(0);
			itinerary.setPublicFlag(jsonNode.get("publicFlag").asBoolean());
			itinerary.setSeqId(""); //Create
			itinerary.setItineraryUserId(jsonNode.get("userId").asText());
			itinerary.setItineraryRecordedTime(LocalDateTime.now());
			
			ItineraryUser itineraryUser = new ItineraryUser();
			ItineraryUserKey key = new ItineraryUserKey(itinerary.getItineraryId(), itinerary.getItineraryUserId());
			itineraryUser.setIteneraryUserKey(key);
			
			ArrayList<ItineraryUser> user = new ArrayList<ItineraryUser>();
			user.add(new ItineraryUser(new ItineraryUserKey(itinerary.getItineraryId(), itinerary.getItineraryUserId())));
			user.add(new ItineraryUser(new ItineraryUserKey(itinerary.getItineraryId(), "USR02")));
			user.add(new ItineraryUser(new ItineraryUserKey(itinerary.getItineraryId(), "USR03")));
			itinerary.setUser(user);
			
//			System.out.println(itinerary.toString());
			
//			itineraryDao.save(itinerary);
//			itineraryUserDao.save(itineraryUser);
			
			return ResponseEntity.ok().body(itinerary);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
		}
	}

}
