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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import liburind.project.model.Destinations;
import liburind.project.service.DestinationService;

@CrossOrigin
@RestController
@RequestMapping("/destination")
public class DestinationController {

	@Autowired
	DestinationService destinationServ;

	@RequestMapping(value = {
			"/get" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> get(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(destinationServ.get(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Destinations());
		}
	}

	@RequestMapping(value = {
			"/category" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getByCategory(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String categoryId = jsonNode.get("categoryId").asText();

			return ResponseEntity.ok().body(destinationServ.getCategory(categoryId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<Destinations>());
		}
	}

	@RequestMapping(value = {
			"/search" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> search(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String destinationName = jsonNode.get("destinationName").asText();

			return ResponseEntity.ok().body(destinationServ.search(destinationName));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<Destinations>());
		}
	}

	// Administrator
	@RequestMapping(value = {
			"/delete" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> delete(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String destinationId = jsonNode.get("destinationId").asText();

			return ResponseEntity.ok().body(destinationServ.delete(destinationId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
		}
	}

	@RequestMapping(value = {
			"/save" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> save(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(destinationServ.save(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error");
		}
	}

}
