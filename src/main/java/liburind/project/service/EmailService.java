package liburind.project.service;

import java.util.Optional;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import liburind.project.dao.UserRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.User;

@Service
public class EmailService {

	@Autowired
	UserRepository usrDao;

	public MimeMessage generateMessage() {
		String from = "liburind@gmail.com";
		String host = "smtp.gmail.com";
		Properties properties = System.getProperties();

		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, "vdcbwhkwoshaduhf");
			}
		});

		session.setDebug(true);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			return message;
		} catch (Exception e) {
			return null;
		}
	}

	public void email(User user) {
		try {
			MimeMessage message = this.generateMessage();
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getUserEmail()));
			message.setSubject("This is the Subject Line!");

			String key = DataHelper.getAlphaNumericString(30) + user.getUserId().substring(3, 6);
			user.setKey(key);
			usrDao.save(user);

			message.setText("Click Link Below!\nhttps://liburind.herokuapp.com/user/active?key=" + key);

			System.out.println("sending...");
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void invitefriend(String itineraryId, User user) {
		try {
			MimeMessage message = this.generateMessage();
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getUserEmail()));
			message.setSubject("This is the Subject Line!");

			String key = DataHelper.getAlphaNumericString(24) + itineraryId.substring(3, 6)
					+ user.getUserId().substring(3, 6);
			user.setKey(key);
			usrDao.save(user);

			message.setText("Click Link to Join!\nhttps://liburind.herokuapp.com/itinerary/join?key=" + key);

			System.out.println("sending...");
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public Object active(String key) {
		Optional<User> usrOpt = usrDao.findById("USR" + key.substring(30, 33));
		if (usrOpt.isPresent()) {
			User user = usrOpt.get();
			if (user.getKey().equals(key)) {
				user.setFlagActive(true);
				usrDao.save(user);
				// Return PHP (?)
				return "OKEEEE";
			}
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

}
