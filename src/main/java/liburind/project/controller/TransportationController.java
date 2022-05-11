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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import liburind.project.model.Transportation;
import liburind.project.model.TransportationCategory;
import liburind.project.service.TransportationService;

@CrossOrigin
@RestController
@RequestMapping("/transportation")
public class TransportationController {

	@Autowired
	TransportationService transServ;

	@RequestMapping(value = {
			"/get" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> get(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(transServ.get(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<Transportation>());
		}
	}

	@RequestMapping(value = {
			"/book" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> book(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(transServ.book(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<Transportation>());
		}
	}

	@RequestMapping(value = { "/endbook" }, method = RequestMethod.GET)
	public ResponseEntity<?> endbook(@RequestParam String key) throws JsonMappingException, JsonProcessingException {
		try {
			return ResponseEntity.ok().body(transServ.endbook(key));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Check Param");
		}
	}

	@RequestMapping(value = {
			"/list" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> getList(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(transServ.list(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ArrayList<TransportationCategory>());
		}
	}

	@RequestMapping(value = { "/sendinvoice" }, method = RequestMethod.GET)
	public ResponseEntity<?> sendinvoice(@RequestParam String key) throws IOException {
		try {
			return ResponseEntity.ok().body(transServ.sendinvoice(key));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ArrayList<TransportationCategory>());
		}
	}

	@RequestMapping(value = {
			"/cekpesanan" }, method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<?> cekpesanan(@RequestBody String json) throws IOException {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode jsonNode = objectMapper.readTree(json);

			return ResponseEntity.ok().body(transServ.cekpesanan(jsonNode));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ArrayList<TransportationCategory>());
		}
	}

}
