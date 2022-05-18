package liburind.project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.DestinationSeqRepository;
import liburind.project.dao.ItineraryRepository;
import liburind.project.dao.ItineraryUserRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.dao.UserRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.DestinationSeq;
import liburind.project.model.Itinerary;
import liburind.project.model.ItineraryUser;
import liburind.project.model.ItineraryUserKey;
import liburind.project.model.TableCount;
import liburind.project.model.User;

@Service
public class ItineraryService {

	@Autowired
	ItineraryRepository itineraryDao;

	@Autowired
	ItineraryUserRepository itineraryUserDao;

	@Autowired
	TableCountRepository tableCountDao;

	@Autowired
	UserRepository userDao;

	@Autowired
	DestinationSeqRepository destinationSeqDao;

	@Autowired
	EmailService emailServ;

	public Itinerary update(String id, String name, boolean publicFlag, String startDate, String endDate,
			String detail) {
		Optional<Itinerary> itrOpt = itineraryDao.findById(id);
		if (itrOpt.isPresent()) {
			LocalDate localDate = LocalDate.now();
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				localDate = LocalDate.parse(startDate, formatter);
			} catch (Exception e) {
				localDate = LocalDate.now();
			}

			Itinerary itinerary = itrOpt.get();
			itinerary.setItineraryName(name);
			itinerary.setPublicFlag(publicFlag);
			itinerary.setDetail(detail);
			itinerary.setStartDate(localDate);
			itineraryDao.save(itinerary);

//			List<DestinationSeq> listSeq = destinationSeqDao.findByItrId(itinerary.getItineraryId());
//			LocalDate date = itinerary.getStartDate();
//			for (DestinationSeq destinationSeq : listSeq) {
//				if (destinationSeq.getSeqDate().isAfter(date)) {
//					date = destinationSeq.getSeqDate();
//				}
//			}
//			itinerary.setEndDate(date);

			return itinerary;
		} else {
			return null;
		}
	}

	public Itinerary save(String name, boolean publicFlag, String userId, String startDate, String endDate,
			String detail) {
		LocalDate localDateStart = LocalDate.now();
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			localDateStart = LocalDate.parse(startDate, formatter);
		} catch (Exception e) {
			localDateStart = LocalDate.now();
		}

		LocalDate localDateEnd = LocalDate.now();
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			localDateEnd = LocalDate.parse(endDate, formatter);
		} catch (Exception e) {
			localDateEnd = LocalDate.now();
		}

		Itinerary itinerary = new Itinerary();
		String id = "";
		Optional<TableCount> tblCount = tableCountDao.findById("Itinerary");
		if (tblCount.isPresent()) {
			id = String.format("ITR%03d", tblCount.get().getCount() + 1);
			tableCountDao.save(new TableCount("Itinerary", tblCount.get().getCount() + 1));
		} else {
			id = String.format("ITR%03d", 1);
			tableCountDao.save(new TableCount("Itinerary", 1));
		}

		itinerary.setItineraryId(id);
		itinerary.setItineraryName(name);
		itinerary.setPublicFlag(publicFlag);
		itinerary.setItineraryUserId(userId);
		itinerary.setDetail(detail);
		itinerary.setStartDate(localDateStart);
		itinerary.setEndDate(localDateEnd);

		itinerary.setItineraryRecordedTime(LocalDateTime.now());

		ItineraryUser itineraryUser = new ItineraryUser();
		ItineraryUserKey key = new ItineraryUserKey(itinerary.getItineraryId(), itinerary.getItineraryUserId());
		itineraryUser.setIteneraryUserKey(key);
		itineraryUser.setActiveFlag(true);

		itineraryDao.save(itinerary);
		itineraryUserDao.save(itineraryUser);

		long days = localDateStart.until(localDateEnd, ChronoUnit.DAYS);
		for (int i = 0; i <= days; i++) {
			DestinationSeq destinationSeq = new DestinationSeq();
			String desSeqId = itinerary.getItineraryId() + " - "
					+ DataHelper.dateToString(itinerary.getStartDate().plusDays(i)) + " - 1"; // Cek

			destinationSeq.setSeqId(desSeqId);
			destinationSeq.setItineraryId(itinerary.getItineraryId());
			destinationSeq.setSeqDate(itinerary.getStartDate().plusDays(i));
			destinationSeq.setSeqPrice(BigDecimal.ZERO);
			destinationSeq.setDestinationId("");

			destinationSeqDao.save(destinationSeq);
		}
		return itinerary;
	}

	public ArrayList<User> getUser(String itineraryId) {
		List<ItineraryUser> userList = itineraryUserDao.findAll();
		ArrayList<User> arrUser = new ArrayList<User>();
		boolean flagItr = false;

		for (ItineraryUser itineraryUser : userList) {
			if (itineraryId.equals(itineraryUser.getIteneraryUserKey().getItineraryId())) {
				flagItr = true;
				Optional<User> userOpt = userDao.findById(itineraryUser.getIteneraryUserKey().getUserId());
				if (userOpt.isPresent()) {
					arrUser.add(userOpt.get());
				}
			}
		}

		if (flagItr) {
			return arrUser;
		}
		return null;
	}

	public ArrayList<User> updateUser(JsonNode jsonNode) {
		List<ItineraryUser> userList = itineraryUserDao.findAll();
		ArrayList<String> oldUser = new ArrayList<String>();
		ArrayList<User> arrUser = new ArrayList<User>();
		boolean flagItr = false;
		String itrKey = jsonNode.get("itineraryId").asText();

		for (ItineraryUser itineraryUser : userList) {
			if (itrKey.equals(itineraryUser.getIteneraryUserKey().getItineraryId())
					&& itineraryUser.getActiveFlag() == true) {
				flagItr = true;
				oldUser.add(itineraryUser.getIteneraryUserKey().getUserId());
			}
		}

		if (flagItr) {
			Optional<Itinerary> itrOpt = itineraryDao.findById(itrKey);
			if (itrOpt.isPresent()) {
				// Get User Pembuat
				HashMap<String, ItineraryUser> map = new HashMap<String, ItineraryUser>();
				ItineraryUser mainUser = new ItineraryUser(
						new ItineraryUserKey(itrOpt.get().getItineraryId(), itrOpt.get().getItineraryUserId()));
				map.put(itrOpt.get().getItineraryUserId(), mainUser);

				// Mapping semua user tambahan
				for (int i = 1; i < jsonNode.size(); i++) {
					String userKey = jsonNode.get("itineraryUser" + i).asText();
					ItineraryUser itrUser = new ItineraryUser(new ItineraryUserKey(itrKey, userKey));

					map.put(userKey, itrUser);
				}

				// Pisahin hanya yg baru
				for (int i = 0; i < oldUser.size(); i++) {
					if (map.containsKey(oldUser.get(i))) {
						map.remove(oldUser.get(i));
						// Kalau ga diinvite di delete
						// } else {
						// itineraryUserDao.delete(new ItineraryUser(new ItineraryUserKey(itrKey,
						// oldUser.get(i))));
					}
				}

				ArrayList<ItineraryUser> arrItrUser = new ArrayList<ItineraryUser>(map.values());
				for (ItineraryUser itineraryUser : arrItrUser) {
					if (jsonNode.get("itineraryId").asText()
							.equals(itineraryUser.getIteneraryUserKey().getItineraryId())) {
						flagItr = true;
						Optional<User> userOpt = userDao.findById(itineraryUser.getIteneraryUserKey().getUserId());
						if (userOpt.isPresent()) {
							itineraryUser.setActiveFlag(false);
							itineraryUserDao.save(itineraryUser);
							// Kirim Email Aktifasi
							emailServ.invitefriend(jsonNode.get("itineraryId").asText(), userOpt.get());
						}
					}
				}

				arrUser = this.getUser(itrKey);
				return arrUser;
			}
		}
		return null;
	}

	public ArrayList<Itinerary> getUserItenerary(String userId) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		List<Itinerary> arrItr = itineraryDao.findByUserId(userId);
		List<ItineraryUser> list = itineraryUserDao.findAll();
		Integer count = 1;

		for (Itinerary itinerary : arrItr) {
			List<DestinationSeq> listSeq = destinationSeqDao.findByItineraryId(itinerary.getItineraryId());
			LocalDate date = itinerary.getStartDate();
			for (DestinationSeq destinationSeq : listSeq) {
				if (destinationSeq.getSeqDate().isAfter(date)) {
					date = destinationSeq.getSeqDate();
				}
			}
			itinerary.setEndDate(date);
			map.put(itinerary.getItineraryId(), count++);
		}

		for (ItineraryUser itineraryUser : list) {
			if (userId.equals(itineraryUser.getIteneraryUserKey().getUserId())
					&& itineraryUser.getActiveFlag() == true) {
				Optional<Itinerary> itrOpt = itineraryDao
						.findById(itineraryUser.getIteneraryUserKey().getItineraryId());
				if (itrOpt.isPresent()) {
					if (!map.containsKey(itrOpt.get().getItineraryId())) {
						List<DestinationSeq> listSeq = destinationSeqDao.findByItineraryId(itrOpt.get().getItineraryId());
						LocalDate date = itrOpt.get().getStartDate();
						for (DestinationSeq destinationSeq : listSeq) {
							if (destinationSeq.getSeqDate().isAfter(date)) {
								date = destinationSeq.getSeqDate();
							}
						}
						Itinerary itinerary = itrOpt.get();
						itinerary.setEndDate(date);
						arrItr.add(itinerary);
					}
				}
			}
		}

		if (arrItr.size() == 0) {
			return new ArrayList<Itinerary>();
		}
		return new ArrayList<Itinerary>(arrItr);
	}

	public ArrayList<Itinerary> getItrListPublic() {
		List<Itinerary> arrItr = itineraryDao.findByPublicFlag(true);
		ArrayList<Itinerary> listItr = new ArrayList<Itinerary>();
		if (arrItr.size() == 0) {
			return null;
		}

		for (Itinerary itinerary : arrItr) {
			if ("".equals(itinerary.getItineraryUserId())) {
				List<DestinationSeq> listSeq = destinationSeqDao.findByItineraryId(itinerary.getItineraryId());
				LocalDate date = itinerary.getStartDate();
				for (DestinationSeq destinationSeq : listSeq) {
					if (destinationSeq.getSeqDate().isAfter(date)) {
						date = destinationSeq.getSeqDate();
					}
				}
				itinerary.setEndDate(date);
				listItr.add(itinerary);
			}
		}

		return listItr;
	}

	public Itinerary get(String itineraryId) {
		Optional<Itinerary> itrOpt = itineraryDao.findById(itineraryId);
		List<DestinationSeq> listSeq = destinationSeqDao.findByItineraryId(itineraryId);

		if (itrOpt.isPresent()) {
			LocalDate date = itrOpt.get().getStartDate();
			for (DestinationSeq destinationSeq : listSeq) {
				if (destinationSeq.getSeqDate().isAfter(date)) {
					date = destinationSeq.getSeqDate();
				}
			}
			Itinerary itr = itrOpt.get();
			itr.setEndDate(date);
			return itr;
		}
		return null;
	}

	public void delete(String itineraryId, String userId) {
		Optional<Itinerary> itrOpt = itineraryDao.findById(itineraryId);
		if (itrOpt.isPresent()) {
			if (userId.equals(itrOpt.get().getItineraryUserId())) {
				itineraryDao.delete(itrOpt.get());
				ArrayList<User> arrUser = this.getUser(itineraryId);
				List<DestinationSeq> listSeq = destinationSeqDao.findByItineraryId(itineraryId);

				for (DestinationSeq destinationSeq : listSeq) {
					destinationSeqDao.delete(destinationSeq);
				}

				for (User user : arrUser) {
					ItineraryUser itrUser = new ItineraryUser(new ItineraryUserKey(itineraryId, user.getUserId()));
					itineraryUserDao.delete(itrUser);
				}
			} else {
				ItineraryUser itrUser = new ItineraryUser(new ItineraryUserKey(itineraryId, userId));
				itineraryUserDao.delete(itrUser);
			}
		}
	}

	public Object search(String itineraryName) {
		return itineraryDao.findByName(itineraryName);
	}

	public Object active(String key) {
		List<ItineraryUser> userList = itineraryUserDao.findAll();
		for (ItineraryUser itineraryUser : userList) {
			if (key.substring(24, 27).equals(itineraryUser.getIteneraryUserKey().getItineraryId().substring(3, 6))
					&& key.substring(27, 30).equals(itineraryUser.getIteneraryUserKey().getUserId().substring(3, 6))) {
				itineraryUser.setActiveFlag(true);
				itineraryUserDao.save(itineraryUser);
				return "OKEEEE";
			}
		}
		return ResponseEntity.badRequest().body("Invite Friend Deleted");
	}

	public Object publishItenerary(JsonNode jsonNode) {
		String itineraryId = jsonNode.get("itineraryId").asText();
		String detail = jsonNode.get("detail").asText();

		Optional<Itinerary> itrOpt = itineraryDao.findById(itineraryId);
		if (itrOpt.isPresent()) {
			Itinerary itr = itrOpt.get();
			itr.setPublicFlag(true);
			itineraryDao.save(itr);

			String id = "";
			Optional<TableCount> tblCount = tableCountDao.findById("Itinerary");
			if (tblCount.isPresent()) {
				id = String.format("ITR%03d", tblCount.get().getCount() + 1);
				tableCountDao.save(new TableCount("Itinerary", tblCount.get().getCount() + 1));
			} else {
				id = String.format("ITR%03d", 1);
				tableCountDao.save(new TableCount("Itinerary", 1));
			}

			itr.setItineraryId(id);
			itr.setPublicFlag(true);
			itr.setPublisher("");
			Optional<User> usrOpt = userDao.findById(itr.getItineraryUserId());
			if (usrOpt.isPresent()) {
				itr.setPublisher(usrOpt.get().getUserName());
			}
			itr.setItineraryUserId("");
			itr.setDetail(detail);

			itineraryDao.save(itr);

			List<DestinationSeq> listDest = destinationSeqDao.findByItineraryId(itineraryId);
			for (DestinationSeq data : listDest) {
				data.setSeqId("PUB" + data.getSeqId());
				data.setItineraryId(id);
				destinationSeqDao.save(data);
			}

			return "Data Published";
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

	public Object copyData(JsonNode jsonNode) {
		String itineraryId = jsonNode.get("itineraryId").asText();
		String userId = jsonNode.get("userId").asText();
		LocalDate date = DataHelper.toDate(jsonNode.get("date").asText());

		Optional<Itinerary> itrOpt = itineraryDao.findById(itineraryId);
		if (itrOpt.isPresent()) {
			Itinerary itr = itrOpt.get();
			String id = "";
			Optional<TableCount> tblCount = tableCountDao.findById("Itinerary");
			if (tblCount.isPresent()) {
				id = String.format("ITR%03d", tblCount.get().getCount() + 1);
				tableCountDao.save(new TableCount("Itinerary", tblCount.get().getCount() + 1));
			} else {
				id = String.format("ITR%03d", 1);
				tableCountDao.save(new TableCount("Itinerary", 1));
			}

			itr.setItineraryId(id);
			itr.setPublicFlag(false);
			itr.setItineraryUserId(userId);
			itr.setPublisher("");
			itr.setStartDate(date);
			itineraryDao.save(itr);

			List<DestinationSeq> listDest = destinationSeqDao.findByItineraryId(itineraryId);
			DestinationSeq.sortByDate(listDest);
			long diff = ChronoUnit.DAYS.between(date, listDest.get(0).getSeqDate());
			Integer number = (int) Math.abs(diff);
			for (DestinationSeq data : listDest) {
				if (diff < 0) {
					data.setSeqDate(data.getSeqDate().plusDays(number));
				} else {
					data.setSeqDate(data.getSeqDate().minusDays(number));
				}
				
				String key = data.getSeqId().replaceAll("PUB", "");
				data.setSeqId(
						id + " - " + DataHelper.dateToString(data.getSeqDate()) + key.substring(17, key.length()));
				data.setItineraryId(id);
				destinationSeqDao.save(data);
			}

			return "Data Copied";
		}
		return ResponseEntity.badRequest().body("Check Param");
	}
}
