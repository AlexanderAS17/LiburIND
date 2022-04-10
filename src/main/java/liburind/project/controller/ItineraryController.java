package liburind.project.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import liburind.project.dao.TableCountRepository;
import liburind.project.model.Itinerary;
import liburind.project.model.User;
import liburind.project.service.ItineraryService;

@CrossOrigin
@RestController
@RequestMapping("/itinerary")
public class ItineraryController {

	@Autowired
	ItineraryService itineraryServ;

	@Autowired
	TableCountRepository TableCountDao;

	@RequestMapping(value = {
			"/save" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> save(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String name = jsonNode.get("name").asText();
			boolean publicFlag = jsonNode.get("publicFlag").asBoolean();
			String userId = jsonNode.get("userId").asText();
			String startDate = jsonNode.get("startDate").asText();
			String detail = jsonNode.get("detail").asText();

			return ResponseEntity.ok().body(itineraryServ.save(name, publicFlag, userId, startDate, detail));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body(new Itinerary());
		}
	}

	@RequestMapping(value = {
			"/update" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> update(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String id = jsonNode.get("id").asText();
			String name = jsonNode.get("name").asText();
			boolean publicFlag = jsonNode.get("publicFlag").asBoolean();
			String startDate = jsonNode.get("startDate").asText();
			String detail = jsonNode.get("detail").asText();
			
			Itinerary data = itineraryServ.update(id, name, publicFlag, startDate, detail);
			
			if(data == null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Itinerary());
			}
			return ResponseEntity.ok().body(data);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Itinerary());
		}
	}

	@RequestMapping(value = { "/user" }, method = RequestMethod.POST)
	public ResponseEntity<?> getUser(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String itineraryId = jsonNode.get("itineraryId").asText();

		ArrayList<User> arrUser = itineraryServ.getUser(itineraryId);
		if (arrUser != null) {
			return ResponseEntity.ok(arrUser);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Found");
		}
	}

	@RequestMapping(value = { "/updateuser" }, method = RequestMethod.POST)
	public ResponseEntity<?> updateUser(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		ArrayList<User> arrUser = itineraryServ.updateUser(jsonNode);
		if (arrUser == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Found");
		} else if (arrUser.size() != 0) {
			return ResponseEntity.ok(arrUser);
		} else {
			return ResponseEntity.ok("Data Has Been Updated");
		}
	}

	@RequestMapping(value = { "/get" }, method = RequestMethod.POST)
	public ResponseEntity<?> get(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String itineraryId = jsonNode.get("itineraryId").asText();

		Itinerary itinerary = itineraryServ.get(itineraryId);
		if (itinerary != null) {
			return ResponseEntity.ok(itinerary);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Itinerary());
		}
	}

	@RequestMapping(value = { "/list" }, method = RequestMethod.POST)
	public ResponseEntity<?> getList(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String userId = jsonNode.get("userId").asText();

		ArrayList<Itinerary> arrItr = itineraryServ.getItrList(userId);
		if (arrItr != null) {
			return ResponseEntity.ok(arrItr);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<Itinerary>());
		}
	}
	
	@RequestMapping(value = { "/public" }, method = RequestMethod.POST)
	public ResponseEntity<?> getListPublic(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ArrayList<Itinerary> arrItr = itineraryServ.getItrListPublic();
		if (arrItr != null) {
			return ResponseEntity.ok(arrItr);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ArrayList<Itinerary>());
		}
	}

	@RequestMapping(value = { "/delete" }, method = RequestMethod.POST)
	public ResponseEntity<?> delete(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String itineraryId = jsonNode.get("itineraryId").asText();
		String userId = jsonNode.get("userId").asText();

		try {
			itineraryServ.delete(itineraryId, userId);
			return ResponseEntity.ok("Success");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not Found");
		}
	}

}
