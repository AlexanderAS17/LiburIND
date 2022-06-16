package liburind.project.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import liburind.project.dao.UserRepository;
import liburind.project.model.User;
import liburind.project.service.EmailService;
import liburind.project.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserRepository userDao;

	@Autowired
	UserService userServ;

	@Autowired
	EmailService emailServ;

	@RequestMapping(value = { "/get" }, method = RequestMethod.POST)
	public ResponseEntity<?> getUserDetail(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);
		
		try {
			return ResponseEntity.ok(userServ.getUserDetail(jsonNode));
		} catch (Exception e) {
			return ResponseEntity.status(404).body("Not Found");
		}
	}

	@RequestMapping(value = { "/login" }, method = RequestMethod.POST)
	public ResponseEntity<?> login(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String userEmail = jsonNode.get("email").asText();
		String userPassword = jsonNode.get("password").asText();

		try {
			return userServ.login(userEmail, userPassword);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new User());
		}
	}

	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public ResponseEntity<?> register(@RequestBody String json)
			throws JsonMappingException, JsonProcessingException, NoSuchAlgorithmException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String name = jsonNode.get("name").asText();
		String email = jsonNode.get("email").asText();
		String password = jsonNode.get("password").asText();

		return ResponseEntity.ok(userServ.register(name, email, password));
	}

	@RequestMapping(value = { "/edit" }, method = RequestMethod.POST)
	public ResponseEntity<?> editProfile(@RequestBody String json)
			throws JsonMappingException, JsonProcessingException, NoSuchAlgorithmException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(json);

		String userId = jsonNode.get("userId").asText();
		String name = jsonNode.get("name").asText();
		String password = jsonNode.get("password").asText();
		String newPassword = jsonNode.get("newPassword").asText();

		try {
			return ResponseEntity.ok(userServ.editProfile(userId, name, password, newPassword));
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Check Param");
		}
	}

	@RequestMapping(value = { "/findfriend" }, method = RequestMethod.POST)
	public ResponseEntity<?> findByEmail(@RequestBody String json) throws JsonMappingException, JsonProcessingException {
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

	// Active Email
	@RequestMapping(value = { "/active" }, method = RequestMethod.GET)
	public ResponseEntity<?> active(@RequestParam String key) throws JsonMappingException, JsonProcessingException {

		try {
			return ResponseEntity.ok().body(emailServ.active(key));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Check Param");
		}
	}

//	@PostMapping(value = "/tryphoto")
//	public ResponseEntity<?> signupUser(@RequestParam("photo") MultipartFile photo) throws IOException {
////		Path uploadPath = Paths.get("D:\\Project\\Heroku\\Foto\\");
////
////		if (!Files.exists(uploadPath)) {
////			Files.createDirectories(uploadPath);
////		}
////
////		try (InputStream inputStream = photo.getInputStream()) {
////			Path filePath = uploadPath.resolve(photo.getName());
////			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
//		try {
//			InputStream inputStream = photo.getInputStream();
//		} catch (IOException ioe) {
//			return ResponseEntity.badRequest().body("Error");
//		}
//		return ResponseEntity.ok().body("OK");
//
//	}

}
