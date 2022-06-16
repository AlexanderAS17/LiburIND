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

import liburind.project.model.DestinationCategory;
import liburind.project.service.DestinationCategoryService;

@CrossOrigin
@RestController
@RequestMapping("/descatg")
public class DestinationCategoryController {

	@Autowired
	DestinationCategoryService destCatgServ;

	@RequestMapping(value = {
			"/get" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> get(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String destinationId = jsonNode.get("destinationId").asText();

			return ResponseEntity.ok().body(destCatgServ.get(destinationId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<DestinationCategory>());
		}
	}

	//Administrator
	@RequestMapping(value = {
			"/save" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> save(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String categoryId = jsonNode.get("categoryId").asText();
			String destinationId = jsonNode.get("destinationId").asText();

			return ResponseEntity.ok().body(destCatgServ.save(destinationId, categoryId));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new DestinationCategory());
		}
	}

	@RequestMapping(value = {
			"/delete" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> delete(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			String categoryId = jsonNode.get("categoryId").asText();
			String destinationId = jsonNode.get("destinationId").asText();

			if(destCatgServ.delete(destinationId, categoryId)) {
				return ResponseEntity.ok().body("OK");
			} else {
				return ResponseEntity.badRequest().body("Check Parameter");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
		}
	}

}
