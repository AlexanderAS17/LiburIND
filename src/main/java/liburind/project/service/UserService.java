package liburind.project.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import liburind.project.dao.TableCountRepository;
import liburind.project.dao.UserRepository;
import liburind.project.model.TableCount;
import liburind.project.model.User;

@Service
public class UserService {
	
	@Autowired
	UserRepository userDao;
	
	@Autowired
	TableCountRepository tableCountDao;
	
	public String hashPassword(String str) {
		//Hash Password
		return str;
	}
	
	public User save(String name, String email, String password) {
		User user = new User();

		Optional<TableCount> countOpt = tableCountDao.findById("User");
		int count = countOpt.isPresent() ? countOpt.get().getCount() : 0;
		
		String id = String.format("USR%03d", count + 1);
		user.setUserId(id);
		user.setUserName(name);

		// Validasi email
		user.setUserEmail(email);
		user.setUserPassword(this.hashPassword(password));
		user.setRoleId("User");

		userDao.save(user);
		return user;
	}

}
