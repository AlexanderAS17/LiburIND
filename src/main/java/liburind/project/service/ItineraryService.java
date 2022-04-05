package liburind.project.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.ItineraryRepository;
import liburind.project.dao.ItineraryUserRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.dao.UserRepository;
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
	
	public Itinerary update(String id, String name, boolean publicFlag, String startDate, String detail) {
		Optional<Itinerary> itrOpt = itineraryDao.findById(id);
		if(itrOpt.isPresent()) {
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
	        return itinerary;
		} else {
			return null;
		}
	}

	public Itinerary save(String name, boolean publicFlag, String userId, String startDate, String detail) {
		LocalDate localDate = LocalDate.now();
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	        localDate = LocalDate.parse(startDate, formatter);
		} catch (Exception e) {
			localDate = LocalDate.now();
		}
		
		Itinerary itinerary = new Itinerary();
		Optional<TableCount> countOpt = tableCountDao.findById("Itinerary");
		int count = countOpt.isPresent() ? countOpt.get().getCount() : 0;
		String id = String.format("ITR%03d", count + 1);
		itinerary.setItineraryId(id);
		itinerary.setItineraryName(name);
		itinerary.setItineraryRiviewCount(0);
		itinerary.setPublicFlag(publicFlag);
		Optional<TableCount> seqOpt = tableCountDao.findById("DestinationSeq");
		int seq = seqOpt.isPresent() ? seqOpt.get().getCount() : 0;
		String seqId = String.format("SEQ%03d", seq + 1);
		itinerary.setSeqId(seqId); // Create
		itinerary.setItineraryUserId(userId);
		itinerary.setDetail(detail);
        itinerary.setStartDate(localDate);
		
		itinerary.setItineraryRecordedTime(LocalDateTime.now());

		ItineraryUser itineraryUser = new ItineraryUser();
		ItineraryUserKey key = new ItineraryUserKey(itinerary.getItineraryId(), itinerary.getItineraryUserId());
		itineraryUser.setIteneraryUserKey(key);

		ArrayList<ItineraryUser> user = new ArrayList<ItineraryUser>();
		user.add(new ItineraryUser(new ItineraryUserKey(itinerary.getItineraryId(), itinerary.getItineraryUserId())));
		itinerary.setUser(user);

		itineraryDao.save(itinerary);
		for (ItineraryUser itineraryUserData : user) {
			itineraryUserDao.save(itineraryUserData);
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
			if (itrKey.equals(itineraryUser.getIteneraryUserKey().getItineraryId())) {
				flagItr = true;
				oldUser.add(itineraryUser.getIteneraryUserKey().getUserId());
			}
		}

		if (flagItr) {
			Optional<Itinerary> itrOpt = itineraryDao.findById(itrKey);
			if (itrOpt.isPresent()) {
				HashMap<String, ItineraryUser> map = new HashMap<String, ItineraryUser>();
				ItineraryUser mainUser = new ItineraryUser(
						new ItineraryUserKey(itrOpt.get().getItineraryId(), itrOpt.get().getItineraryUserId()));
				map.put(itrOpt.get().getItineraryUserId(), mainUser);

				for (int i = 1; i < jsonNode.size(); i++) {
					String userKey = jsonNode.get("itineraryUser" + i).asText();
					ItineraryUser itrUser = new ItineraryUser(new ItineraryUserKey(itrKey, userKey));

					map.put(userKey, itrUser);
				}

				for (int i = 0; i < oldUser.size(); i++) {
					if (map.containsKey(oldUser.get(i))) {
						map.remove(oldUser.get(i));
					} else {
						itineraryUserDao.delete(new ItineraryUser(new ItineraryUserKey(itrKey, oldUser.get(i))));
					}
				}

				ArrayList<ItineraryUser> arrItrUser = new ArrayList<ItineraryUser>(map.values());
				for (ItineraryUser itineraryUser : arrItrUser) {
					if (jsonNode.get("itineraryId").asText()
							.equals(itineraryUser.getIteneraryUserKey().getItineraryId())) {
						flagItr = true;
						Optional<User> userOpt = userDao.findById(itineraryUser.getIteneraryUserKey().getUserId());
						if (userOpt.isPresent()) {
							itineraryUserDao.save(itineraryUser);
						}
					}
				}

				arrUser = this.getUser(itrKey);
				return arrUser;
			}
		}
		return null;
	}

	public ArrayList<Itinerary> getItrList(String userId) {
		List<Itinerary> arrItr = itineraryDao.findByUser(userId);
		List<ItineraryUser> list = itineraryUserDao.findAll();

		for (ItineraryUser itineraryUser : list) {
			if (userId.equals(itineraryUser.getIteneraryUserKey().getUserId())) {
				Optional<Itinerary> itrOpt = itineraryDao
						.findById(itineraryUser.getIteneraryUserKey().getItineraryId());
				if (itrOpt.isPresent()) {
					arrItr.add(itrOpt.get());
				}
			}
		}

		if (arrItr.size() == 0) {
			return null;
		}
		return new ArrayList<Itinerary>(arrItr);
	}
	
	public ArrayList<Itinerary> getItrListPublic() {
		List<Itinerary> arrItr = itineraryDao.findByFlag(true);
		
		if (arrItr.size() == 0) {
			return null;
		}
		return new ArrayList<Itinerary>(arrItr);
	}

	public Itinerary get(String itineraryId) {
		Optional<Itinerary> itrOpt = itineraryDao.findById(itineraryId);
		if (itrOpt.isPresent()) {
			return itrOpt.get();
		}
		return null;
	}

	public void delete(String itineraryId, String userId) {
		Optional<Itinerary> itrOpt = itineraryDao.findById(itineraryId);
		if (itrOpt.isPresent()) {
			if (userId.equals(itrOpt.get().getItineraryUserId())) {
				itineraryDao.delete(itrOpt.get());
				ArrayList<User> arrUser = this.getUser(itineraryId);

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

	

}
