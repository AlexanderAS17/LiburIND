package liburind.project.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
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

import liburind.project.service.DestinationSeqService;

@CrossOrigin
@RestController
@RequestMapping("/destinationseq")
public class DestinationSeqController {

	@Autowired
	DestinationSeqService destSeqServ;

	@RequestMapping(value = {
			"/get" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> get(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(destSeqServ.get(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@RequestMapping(value = {
			"/save" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> save(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(destSeqServ.save(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@RequestMapping(value = {
			"/delete" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> delete(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String date = jsonNode.get("date").asText().replaceAll("-", "");
			String itineraryId = jsonNode.get("itineraryId").asText();

			return ResponseEntity.ok().body(destSeqServ.delete(itineraryId, date));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}

	@RequestMapping(value = {
			"/saveone" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> saveone(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(destSeqServ.saveone(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
		}
	}
}
