package liburind.project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.DestinationRepository;
import liburind.project.dao.DestinationSeqRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.Destination;
import liburind.project.model.DestinationSeq;
import liburind.project.model.DestinationSeqKey;

@Service
public class DestinationSeqService {

	@Autowired
	DestinationSeqRepository desSeqDao;

	@Autowired
	DestinationRepository desDao;

	private ArrayList<ArrayList<DestinationSeq>> splitData(List<DestinationSeq> listDestSeq) {
		ArrayList<ArrayList<DestinationSeq>> splited = new ArrayList<ArrayList<DestinationSeq>>();
		ArrayList<DestinationSeq> arrData = new ArrayList<DestinationSeq>();
		DestinationSeq prevData = new DestinationSeq();
		for (DestinationSeq destinationSeq : listDestSeq) {
			if (arrData.size() > 0
					&& !prevData.getSeqKey().getSeqDate().isEqual(destinationSeq.getSeqKey().getSeqDate())) {
				System.out.println(arrData.toString());
				splited.add(arrData);
				arrData = new ArrayList<DestinationSeq>();
			}
			arrData.add(destinationSeq);
			prevData = destinationSeq;
		}
		splited.add(arrData);
		return splited;
	}

	public Object get(JsonNode jsonNode) {
		if (jsonNode.has("itineraryId")) {
			List<DestinationSeq> listDestSeq = desSeqDao.findByItrId(jsonNode.get("itineraryId").asText());
			for (DestinationSeq destinationSeq : listDestSeq) {
				Optional<Destination> desOpt = desDao.findById(destinationSeq.getDestinationId());
				String desName = "";
				if(desOpt.isPresent()) {
					desName = desOpt.get().getDestinationName();
				}
				destinationSeq.setDestinationName(desName);
			}
			if (listDestSeq.size() > 0) {
				ArrayList<DestinationSeq> list = new ArrayList<DestinationSeq>(listDestSeq);
				DestinationSeq.sortByDate(list);
				return this.splitData(listDestSeq);
			}
		}
		return ResponseEntity.badRequest().body("Chech Param");
	}

	@Transactional(rollbackOn = Exception.class)
	public Object save(JsonNode jsonNode) {
		List<DestinationSeq> arrDest = new ArrayList<DestinationSeq>();
		String itineraryId = jsonNode.get("itineraryId").asText();
		LocalDate seqDate = DataHelper.toDate(jsonNode.get("date").asText());
		BigDecimal seqPrice = DataHelper.toBigDecimal(jsonNode.get("price").asText());
		int count = 1;
		this.delete(itineraryId, jsonNode.get("date").asText());

		if (jsonNode.has("data") && jsonNode.get("data").isArray()) {
			for (JsonNode objNode : jsonNode.get("data")) {
				DestinationSeq destinationSeq = new DestinationSeq();
				String seqId = jsonNode.get("itineraryId").asText() + " - " + jsonNode.get("date").asText() + " - "
						+ count++;
				DestinationSeqKey seqKey = new DestinationSeqKey(seqId, seqDate);

				destinationSeq.setSeqKey(seqKey);
				destinationSeq.setItineraryId(itineraryId);
				destinationSeq.setSeqStartTime(DataHelper.toLongDate(objNode.get("startTime").asText()));
				destinationSeq.setSeqEndTime(DataHelper.toLongDate(objNode.get("endTime").asText()));
				destinationSeq.setSeqPrice(seqPrice);

				Optional<Destination> desOpt = desDao.findById(objNode.get("destinationId").asText());
				if (desOpt.isPresent()) {
					destinationSeq.setDestinationId(objNode.get("destinationId").asText());
					destinationSeq.setDestinationName(desOpt.get().getDestinationName());
				} else {
					return ResponseEntity.badRequest().body("Check Param");
				}

				desSeqDao.save(destinationSeq);
				arrDest.add(destinationSeq);
			}
			ArrayList<ArrayList<DestinationSeq>> arrData = this.splitData(arrDest);
			return arrData;
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

	@Transactional(rollbackOn = Exception.class)
	public Object delete(String itineraryId, String date) {
		List<DestinationSeq> listData = desSeqDao.findByItrId(itineraryId);
		LocalDate deletedDate = DataHelper.toDate(date);
		for (DestinationSeq destinationSeq : listData) {
			if(destinationSeq.getSeqKey().getSeqDate().equals(deletedDate)) {
				desSeqDao.delete(destinationSeq);
			}
		}
		return "Data Deleted";
	}

}
