package liburind.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import liburind.project.dao.UserRepository;
import liburind.project.model.User;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	UserRepository userDao;
	
	@RequestMapping(value = {"/run"}, method = RequestMethod.GET)
	public void createGroceryItems() {
        System.out.println("Data creation started...");
        userDao.save(new User("USR001", "Alexander", "alex@gmail.com", "alex123", "role_user"));
        userDao.save(new User("USR002", "Kevin", "kevin@gmail.com", "kevin123", "role_user"));
        System.out.println("Data creation complete...");
    }
	
	@RequestMapping(value = {"/view"}, method = RequestMethod.GET)
//	public ResponseEntity<?> getUser(@RequestParam String userId) {
	public void getUser() {
		List<User> listUser = userDao.findAll();
		for (int i = 0; i < listUser.size(); i++) {
			System.out.println(listUser.get(i).toString());
		}
	}

//	@RequestMapping(value = {"/login"}, method = RequestMethod.POST)
//	public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
//		try {
//			Optional<User> userOpt = userDao.findByEmail(email);
//			
//			if(userOpt.isPresent()) {
//				//HashPassword
//				if(!password.equals(password)) {
//					return ResponseEntity.badRequest().body("Wrong Password");
//				}
//			} else {
//				return ResponseEntity.status(404).body("Not Found");
//			}
//			
//			return ResponseEntity.ok(userOpt.get());
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body("Error");
//		}
//	}
	
//	@RequestMapping(value = {"/register"}, method = RequestMethod.POST)
//	public ResponseEntity<?> register(@RequestParam String name, @RequestParam String email, @RequestParam String password) {
//		try {
//			User user = new User();
//			user.setUserId(""); //Next Sequence From DB
//			user.setUserName(name);
//			
//			//Validasi email
//			user.setUserEmail(email);
//			//HashPassword
//			user.setUserPassword(password);
//			user.setRoleId("User");
//			
//			userDao.save(user);
//			
//			return ResponseEntity.ok(user);
//		} catch (Exception e) {
//			return ResponseEntity.badRequest().body("Error");
//		}
//	}
//
//	@RequestMapping(value = {"/edit"}, method = RequestMethod.POST)
//	public ResponseEntity<?> editProfile(@RequestParam String userId, @RequestParam String name, @RequestParam String password) {
//		Optional<User> userOpt = userDao.findById(userId);
//		
//		if(userOpt.isPresent()) {
//			User user = userOpt.get();
//			user.setUserName(name);
//			user.setUserPassword(password);
//			
//			userDao.save(user);
//			
//			return ResponseEntity.ok(user);
//		} else {
//			return ResponseEntity.status(404).body("Not Found");
//		}
//	}

}
