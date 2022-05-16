package liburind.project.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import liburind.project.dao.TableCountRepository;
import liburind.project.dao.UserRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.TableCount;
import liburind.project.model.User;

@Service
public class UserService {

	@Autowired
	UserRepository userDao;

	@Autowired
	TableCountRepository tableCountDao;

	@Autowired
	EmailService emailServ;

	public String hashPassword(String str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] messageDigest = md.digest(str.getBytes());
		BigInteger no = new BigInteger(1, messageDigest);

		String hashtext = no.toString(16);
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}

		return hashtext;
	}

	public Object register(String name, String email, String password) throws NoSuchAlgorithmException {
		Optional<User> userOpt = userDao.findByEmail(email);
		if (!userOpt.isPresent()) {
			User user = new User();

			Optional<TableCount> countOpt = tableCountDao.findById("User");
			int count = countOpt.isPresent() ? countOpt.get().getCount() : 0;
			tableCountDao.save(new TableCount("User", count + 1));

			String id = String.format("USR%03d", count + 1);
			user.setUserId(id);
			user.setUserName(name);

			String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(email);
			if (!matcher.matches()) {
				return ResponseEntity.badRequest().body("Check Email");
			}
			user.setUserEmail(email);

			String response = DataHelper.validatePassword(password);
			if (!"OK".equals(response)) {
				return ResponseEntity.badRequest().body(response);
			}

			user.setUserPassword(this.hashPassword(password));
			user.setFlagActive(false);

			emailServ.sendActivationEmail(user);

			return user;
		}
		return ResponseEntity.badRequest().body("Email has Been Used");
	}

	public Object login(String userEmail, String userPassword) throws NoSuchAlgorithmException {
		Optional<User> userOpt = userDao.findByEmail(userEmail);

		return this.validateData(userOpt, userEmail, userPassword);
	}

	private Object validateData(Optional<User> userOpt, String userEmail, String userPassword) throws NoSuchAlgorithmException {
		if (userOpt.isPresent()) {
			if (!userOpt.get().getFlagActive()) {
				return ResponseEntity.badRequest().body("Not Actived");
			}
			if (!this.hashPassword(userPassword).equals(userOpt.get().getUserPassword())) {
				return ResponseEntity.badRequest().body("Wrong Password");
			}
		} else {
			return ResponseEntity.badRequest().body("User Not Found");
		}

		return userOpt.get();
	}

}
