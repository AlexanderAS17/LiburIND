package liburind.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import liburind.project.service.RiviewService;

@CrossOrigin
@RestController
@RequestMapping("/itinerary")
public class RiviewController {
	
	@Autowired
	RiviewService riviewServ;
	
	@RequestMapping(value = { "/save" }, method = RequestMethod.POST)
	public ResponseEntity<?> getUser(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		return ResponseEntity.ok(riviewServ.save(jsonNode));
	}

}
