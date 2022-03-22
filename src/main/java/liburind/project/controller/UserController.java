package liburind.project.controller;

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

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserRepository userDao;

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
				// HashPassword
				if (!password.equals(userOpt.get().getUserPassword())) {
					return ResponseEntity.badRequest().body("Wrong Password");
				}
			} else {
				return ResponseEntity.status(404).body("Not Found");
			}

			return ResponseEntity.ok(userOpt.get());
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
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
			User user = new User();

			String id = String.format("USR%03d", userDao.count() + 1);
			user.setUserId(id); // Next Sequence From DB
			user.setUserName(name);

			// Validasi email
			user.setUserEmail(email);
			// HashPassword
			user.setUserPassword(password);
			user.setRoleId("User");

			userDao.save(user);

			return ResponseEntity.ok(user);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error");
		}
	}

	@RequestMapping(value = { "/edit" }, method = RequestMethod.POST)
	public ResponseEntity<?> editProfile(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
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

}
