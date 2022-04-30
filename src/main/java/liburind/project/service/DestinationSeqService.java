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
import liburind.project.dao.ItineraryRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.DestinationSeq;
import liburind.project.model.DestinationSeqKey;
import liburind.project.model.Destinations;
import liburind.project.model.Itinerary;
import liburind.project.model.ItineraryDestination;
import liburind.project.model.ItineraryResponse;

@Service
public class DestinationSeqService {

	@Autowired
	DestinationSeqRepository desSeqDao;

	@Autowired
	DestinationRepository desDao;

	@Autowired
	ItineraryRepository itrDao;

	private ItineraryDestination toModel(ItineraryDestination itr, Destinations des) {
		itr.setDestinationId(des.getDestinationId());
		itr.setDestinationName(des.getDestinationName());
		itr.setDestinationRating(des.getDestinationRating());
		itr.setDestinationDetail(des.getDestinationDetail());
		itr.setDestinationAddress(des.getDestinationAddress());
		itr.setDestinationPlaceId(des.getDestinationPlaceId());
		itr.setDestinationGeometryLat(des.getDestinationGeometryLat());
		itr.setDestinationGeometryLng(des.getDestinationGeometryLng());
		itr.setDestinationPhoto(des.getDestinationPhoto());
		itr.setDestinationTimeOpen(des.getDestinationTimeOpen());
		itr.setDestinationUrl(des.getDestinationUrl());
		itr.setDestinationUsrJmlh(des.getDestinationUsrJmlh());
		itr.setDestinationWebsite(des.getDestinationWebsite());
		return itr;
	}

	private ArrayList<ItineraryResponse> splitData(List<DestinationSeq> listDestSeq) {
		ArrayList<ArrayList<DestinationSeq>> splited = new ArrayList<ArrayList<DestinationSeq>>();
		ArrayList<DestinationSeq> arrData = new ArrayList<DestinationSeq>();
		ArrayList<ItineraryResponse> response = new ArrayList<ItineraryResponse>();
		DestinationSeq prevData = new DestinationSeq();
		for (DestinationSeq destinationSeq : listDestSeq) {
			if (arrData.size() > 0
					&& !prevData.getSeqKey().getSeqDate().isEqual(destinationSeq.getSeqKey().getSeqDate())) {
				splited.add(arrData);
				arrData = new ArrayList<DestinationSeq>();
			}
			arrData.add(destinationSeq);
			prevData = destinationSeq;
		}
		splited.add(arrData);

		for (ArrayList<DestinationSeq> perDays : splited) {
			ItineraryResponse resDays = new ItineraryResponse();
			resDays.setSeqDate(perDays.get(0).getSeqKey().getSeqDate());
			resDays.setSeqPrice(perDays.get(0).getSeqPrice());
			resDays.setItineraryId(perDays.get(0).getItineraryId());
			List<ItineraryDestination> arrDestination = new ArrayList<ItineraryDestination>();
			for (DestinationSeq destinationSeq : perDays) {
				ItineraryDestination itrDes = new ItineraryDestination();
				itrDes.setSeqId(destinationSeq.getSeqKey().getSeqId());
				itrDes.setDestinationId(destinationSeq.getDestinationId());
				itrDes.setSeqStartTime(destinationSeq.getSeqStartTime());
				itrDes.setSeqEndTime(destinationSeq.getSeqEndTime());
				Optional<Destinations> desOpt = desDao.findById(itrDes.getDestinationId());
				if (desOpt.isPresent()) {
					itrDes = this.toModel(itrDes, desOpt.get());
				}
				arrDestination.add(itrDes);
			}
			resDays.setArrDestination(arrDestination);
			response.add(resDays);
		}
		return response;
	}

	public Object get(JsonNode jsonNode) {
		if (jsonNode.has("itineraryId")) {
			List<DestinationSeq> listDestSeq = desSeqDao.findByItrId(jsonNode.get("itineraryId").asText());
			for (DestinationSeq destinationSeq : listDestSeq) {
				Optional<Destinations> desOpt = desDao.findById(destinationSeq.getDestinationId());
				if (desOpt.isPresent()) {
					destinationSeq.setDestination(desOpt.get());
				} else {
					destinationSeq.setDestination(null);
				}
			}
			if (listDestSeq.size() > 0) {
				ArrayList<DestinationSeq> list = new ArrayList<DestinationSeq>(listDestSeq);
				DestinationSeq.sortByDate(list);
				return this.splitData(list);
			}
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

	@Transactional(rollbackOn = Exception.class)
	public Object save(JsonNode jsonNode) {
		List<DestinationSeq> arrDest = new ArrayList<DestinationSeq>();
		String itineraryId = jsonNode.get("itineraryId").asText();
		LocalDate seqDate = DataHelper.toDate(jsonNode.get("date").asText());
		BigDecimal seqPrice = DataHelper.toBigDecimal(jsonNode.get("price").asText());
		int count = 1;
		this.delete(itineraryId, jsonNode.get("date").asText());

		Optional<Itinerary> itrOpt = itrDao.findById(itineraryId);
		LocalDate startDateTime = null;

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

				Optional<Destinations> desOpt = desDao.findById(objNode.get("destinationId").asText());
				if (desOpt.isPresent()) {
					destinationSeq.setDestinationId(objNode.get("destinationId").asText());
					destinationSeq.setDestination(desOpt.get());
				} else {
					destinationSeq.setDestinationId("");
				}

				desSeqDao.save(destinationSeq);
				arrDest.add(destinationSeq);

				if (startDateTime == null || destinationSeq.getSeqKey().getSeqDate().isBefore(startDateTime)) {
					startDateTime = destinationSeq.getSeqKey().getSeqDate();
				}
			}

			if (itrOpt.isPresent()) {
				Itinerary itr = itrOpt.get();
				itr.setStartDate(startDateTime);
				itrDao.save(itr);
			}
			ArrayList<ItineraryResponse> arrData = this.splitData(arrDest);
			return arrData;
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

	@Transactional(rollbackOn = Exception.class)
	public Object delete(String itineraryId, String date) {
		List<DestinationSeq> listData = desSeqDao.findByItrId(itineraryId);
		LocalDate deletedDate = DataHelper.toDate(date);
		for (DestinationSeq destinationSeq : listData) {
			if (destinationSeq.getSeqKey().getSeqDate().equals(deletedDate)) {
				desSeqDao.delete(destinationSeq);
			}
		}
		return "Data Deleted";
	}

	@Transactional
	public Object saveone(JsonNode jsonNode) {
		if(jsonNode.has("itineraryId")) {
			Optional<Itinerary> itrOpt = itrDao.findById(jsonNode.get("itineraryId").asText());
			if (itrOpt.isPresent()) {
				DestinationSeq destinationSeq = new DestinationSeq();
				Destinations destination = new Destinations();
				String destinationPlaceId = jsonNode.has("destinationPlaceId") ? jsonNode.get("destinationPlaceId").asText()
						: "NoData";
				if(!"NoData".equals(destinationPlaceId)) {
					Optional<Destinations> desOpt = desDao.findByPlaceId(destinationPlaceId);
					if(desOpt.isPresent()) {
						destination = desOpt.get();
					} else {
						destination = Destinations.mapJson(jsonNode);
						desDao.save(destination);
					}
					
					Integer count = 1;
					LocalDate seqDate = DataHelper.toDate(jsonNode.get("date").asText());
					List<DestinationSeq> listDes = desSeqDao.findByItrId(jsonNode.get("itineraryId").asText());
					if(listDes.size() > 0) {
						for (DestinationSeq desSeq : listDes) {
							if(seqDate.equals(desSeq.getSeqStartTime().toLocalDate()) && !"".equals(desSeq.getDestinationId())) {
								count++;
							}
						}
					}
					
					String seqId = jsonNode.get("itineraryId").asText() + " - " + jsonNode.get("date").asText() + " - " + count++;
					DestinationSeqKey seqKey = new DestinationSeqKey(seqId, seqDate);
					destinationSeq.setSeqKey(seqKey);
					destinationSeq.setItineraryId(jsonNode.get("itineraryId").asText());
					destinationSeq.setSeqStartTime(DataHelper.toLongDate(jsonNode.get("startTime").asText()));
					destinationSeq.setSeqEndTime(DataHelper.toLongDate(jsonNode.get("endTime").asText()));
					destinationSeq.setSeqPrice(DataHelper.toBigDecimal(jsonNode.get("price").asText()));
					destinationSeq.setDestinationId(destination.getDestinationId());
					destinationSeq.setDestination(destination);
					desSeqDao.save(destinationSeq);
					
					listDes.add(destinationSeq);
					DestinationSeq.sortByDate(listDes);
					
					ArrayList<ItineraryResponse> arrData = this.splitData(listDes);
					return arrData;
				}
			}
		} 
		return ResponseEntity.badRequest().body("Check Param");
	}

}
