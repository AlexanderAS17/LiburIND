package liburind.project.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import liburind.project.dao.DestinationRepository;
import liburind.project.dao.DestinationSeqRepository;
import liburind.project.dao.UserRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.DestinationSeq;
import liburind.project.model.Destination;
import liburind.project.model.Itinerary;
import liburind.project.model.User;

@Service
public class EmailService {

	@Autowired
	UserRepository usrDao;
	
	@Autowired
	DestinationSeqRepository desSeqDao;
	
	@Autowired
	DestinationRepository desDao;

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
//		session.setDebug(true);

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			return message;
		} catch (Exception e) {
			return null;
		}
	}

	public void sendActivationEmail(User user) {
		try {
			MimeMessage message = this.generateMessage();
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getUserEmail()));
			message.setSubject("Aktivasi Akun LiburIND");

			String key = DataHelper.getAlphaNumericString(30) + user.getUserId().substring(3, 6);
			user.setKey(key);
			usrDao.save(user);

			message.setText("Klik Link Berikut!\nhttps://liburind.herokuapp.com/user/active?key=" + key);

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
			message.setSubject("Link Undangan Itinerary LiburIND");

			String key = DataHelper.getAlphaNumericString(24) + itineraryId.substring(3, 6)
					+ user.getUserId().substring(3, 6);
			user.setKey(key);
			usrDao.save(user);

			message.setText("Klik Link untuk Bergabung!\nhttps://liburind.herokuapp.com/itinerary/join?key=" + key);

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

	public void kirimTagihan(Itinerary itinerary, User user, String transCategoryName, Integer num, LocalDate startDate,
			Integer duration, BigDecimal sum) {
		String tempat = "";
		List<DestinationSeq> arrDest = desSeqDao.findByItineraryId(itinerary.getItineraryId());
		DestinationSeq.sortByDate(arrDest);
		for (DestinationSeq destinationSeq : arrDest) {
			if(startDate.equals(destinationSeq.getSeqDate())) {
				Optional<Destination> desOpt = desDao.findById(destinationSeq.getDestinationId());
				if(desOpt.isPresent()) {
					tempat = desOpt.get().getDestinationName();
					break;
				}
			}
		}
		try {
			MimeMessage message = this.generateMessage();
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getUserEmail()));
			message.setSubject("Tagihan Pembayaran Kendaraan LiburIND");

			StringBuilder sb = new StringBuilder();
			sb.append("Detail Pesanan: \n");
			sb.append("Nama Itinerary: " + itinerary.getItineraryName() + "\n");
			sb.append("Nama Pemesan: " + user.getUserName() + "\n");
			sb.append("Jenis Kendaraan: " + transCategoryName + "\n");
			sb.append("Jumlah Kendaraan: " + num + " Unit\n");
			sb.append("Tanggal Pemesanan: " + DataHelper.dateToPrettyString(startDate) + "\n");
			sb.append("Tempat Penjemputan: " + tempat + "\n");
			sb.append("Jam Penjemputan: 8:00 \n");
			sb.append("Durasi Pemesanan: " + duration + " Hari\n");
			sb.append("Jumlah yang Harus dibayarkan: Rp." + sum.toEngineeringString() + "\n\n");
			sb.append("Silahkan kirimkan bukti pembayaran ke email berikut\n");
			sb.append("liburind.adm1n@gmail.com");
			sb.append("\nPembayaran paling lambat dilakukan 12 jam setelah email ini dikirimkan\n");

			String key = DataHelper.getAlphaNumericString(24) + itinerary.getItineraryId().substring(3, 6)
					+ user.getUserId().substring(3, 6);
			user.setKey(key);
			usrDao.save(user);

			message.setText(sb.toString()
					+ "\n\nKlik Link Berikut untuk Membatalkan Pesanan\nhttps://liburind.herokuapp.com/transportation/endbook?key="
					+ key + "\n\nUntuk Informasi lebih lanjut silahkan hubungi 081398863986 / 081272452265");

			System.out.println("sending...");
			Transport.send(message);
			System.out.println("Sent message successfully....");
			
			this.kirimKeAdmin(itinerary, user, transCategoryName, num, startDate, duration, sum, tempat);
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}
	
	private void kirimKeAdmin(Itinerary itinerary, User user, String transCategoryName, Integer num, LocalDate startDate,
			Integer duration, BigDecimal sum, String tempat) {
		try {
			MimeMessage message = this.generateMessage();
			message.addRecipient(Message.RecipientType.TO, new InternetAddress("liburind.adm1n@gmail.com"));
			message.setSubject("Link Konfirmasi Pembayaran");

			StringBuilder sb = new StringBuilder();
			sb.append("Detail Pesanan: \n");
			sb.append("Nama Itinerary: " + itinerary.getItineraryName() + "\n");
			sb.append("Nama Pemesan: " + user.getUserName() + "\n");
			sb.append("Jenis Kendaraan: " + transCategoryName + "\n");
			sb.append("Jumlah Kendaraan: " + num + " Unit\n");
			sb.append("Tanggal Pemesanan: " + DataHelper.dateToPrettyString(startDate) + "\n");
			sb.append("Tempat Penjemputan: " + tempat + "\n");
			sb.append("Jam Penjemputan: 8:00 \n");
			sb.append("Durasi Pemesanan: " + duration + " Hari\n");
			sb.append("Jumlah yang Harus dibayarkan: Rp." + sum.toEngineeringString() + "\n\n");

			String key = DataHelper.getAlphaNumericString(24) + itinerary.getItineraryId().substring(3, 6)
					+ user.getUserId().substring(3, 6);
			user.setKey(key);
			usrDao.save(user);

			message.setText(sb.toString()
					+ "\n\nKlik Link Berikut untuk Konfirmasi Pesanan\nhttps://liburind.herokuapp.com/transportation/sendinvoice?key="
					+ key + "\nKlik Link Berikut Menyelesaikan Pesanan\nhttps://liburind.herokuapp.com/transportation/endbook?key="
					+ key);

			System.out.println("sending...");
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public void kirimInvoice(File file, User user) {
		try {
			MimeMessage message = this.generateMessage();
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getUserEmail()));
			message.setSubject("Nota Pembayaran LiburIND");

			Multipart multipart = new MimeMultipart();
            MimeBodyPart attachmentPart = new MimeBodyPart();
            MimeBodyPart textPart = new MimeBodyPart();

            try {
                attachmentPart.attachFile(file);
                textPart.setText("Berikut kami Lampirkan Nota pembayaran beserta detail Kendaraan\nTerima kasih :)\n\nUntuk Informasi lebih lanjut silahkan hubungi 081398863986 / 081272452265");
                multipart.addBodyPart(textPart);
                multipart.addBodyPart(attachmentPart);
            } catch (IOException e) {
                e.printStackTrace();
            }

            message.setContent(multipart);
			System.out.println("sending...");
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

}
