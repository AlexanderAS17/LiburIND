package liburind.project.controller;

import java.util.List;
import java.util.Optional;

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

import liburind.project.dao.UserRepository;
import liburind.project.model.User;
import liburind.project.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserRepository userDao;

	@Autowired
	UserService userServ;

	@RequestMapping(value = { "/get" }, method = RequestMethod.POST)
	public ResponseEntity<?> getUser(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		Optional<User> userOpt = userDao.findById(jsonNode.get("userId").asText());

		if (userOpt.isPresent()) {
			return ResponseEntity.ok(userOpt.get());
		} else {
			return ResponseEntity.status(404).body("Not Found");
		}
	}

	@RequestMapping(value = { "/login" }, method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String email = jsonNode.get("email").asText();
		String password = jsonNode.get("password").asText();

		try {
			Optional<User> userOpt = userDao.findByEmail(email);

			if (userOpt.isPresent()) {
				if (!userServ.hashPassword(password).equals(userOpt.get().getUserPassword())) {
					return ResponseEntity.badRequest().body(new User());
				}
			} else {
				return ResponseEntity.badRequest().body(new User());
			}

			return ResponseEntity.ok(userOpt.get());
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new User());
		}
	}

	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String name = jsonNode.get("name").asText();
		String email = jsonNode.get("email").asText();
		String password = jsonNode.get("password").asText();

		try {
			return ResponseEntity.ok(userServ.save(name, email, password));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
		}
	}

	@RequestMapping(value = { "/edit" }, method = RequestMethod.POST)
	public ResponseEntity<?> editProfile(@RequestBody String json)
			throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String userId = jsonNode.get("userId").asText();
		String name = jsonNode.get("name").asText();
		String password = jsonNode.get("password").asText();

		Optional<User> userOpt = userDao.findById(userId);

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.setUserName(name);
			user.setUserPassword(password);

			userDao.save(user);

			return ResponseEntity.ok(user);
		} else {
			return ResponseEntity.status(404).body("Not Found");
		}
	}

	@RequestMapping(value = { "/findfriend" }, method = RequestMethod.POST)
	public ResponseEntity<?> findFriend(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String email = jsonNode.get("email").asText();

		try {
			List<User> userOpt = userDao.findByEmailRegex(email);

			if (userOpt.size() != 0) {
				return ResponseEntity.ok(userOpt);
			} else {
				return ResponseEntity.badRequest().body("Not Found");
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
		}
	}

}
