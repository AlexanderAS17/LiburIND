package liburind.project.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import liburind.project.dao.DestinationRepository;
import liburind.project.dao.DestinationSeqRepository;
import liburind.project.dao.ItineraryRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.DestinationSeq;
import liburind.project.model.Destinations;
import liburind.project.model.Itinerary;
import liburind.project.model.ItineraryDestination;
import liburind.project.model.ItineraryResponse;
import liburind.project.model.TableCount;

@Service
public class DestinationSeqService {

	@Autowired
	DestinationSeqRepository desSeqDao;

	@Autowired
	DestinationRepository desDao;

	@Autowired
	ItineraryRepository itrDao;

	@Autowired
	TableCountRepository tblDao;

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
			if (arrData.size() > 0 && !prevData.getSeqDate().isEqual(destinationSeq.getSeqDate())) {
				splited.add(arrData);
				arrData = new ArrayList<DestinationSeq>();
			}
			arrData.add(destinationSeq);
			prevData = destinationSeq;
		}
		splited.add(arrData);

		for (ArrayList<DestinationSeq> perDays : splited) {
			ItineraryResponse resDays = new ItineraryResponse();
			resDays.setSeqDate(perDays.get(0).getSeqDate());
			resDays.setSeqPrice(perDays.get(0).getSeqPrice());
			resDays.setItineraryId(perDays.get(0).getItineraryId());
			List<ItineraryDestination> arrDestination = new ArrayList<ItineraryDestination>();
			for (DestinationSeq destinationSeq : perDays) {
				ItineraryDestination itrDes = new ItineraryDestination();
				itrDes.setSeqId(destinationSeq.getSeqId());
				itrDes.setDestinationId(destinationSeq.getDestinationId());
				itrDes.setSeqDate(destinationSeq.getSeqDate());
				String duration = destinationSeq.getDuration() != null ? destinationSeq.getDuration() : "";
				String distance = destinationSeq.getDistance() != null ? destinationSeq.getDistance() : "";
				itrDes.setDuration(duration);
				itrDes.setDistance(distance);
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

	private void optimizeDay(ArrayList<DestinationSeq> perDay) {
		if (perDay.size() > 2) {
			String googleApi = "https://maps.googleapis.com/maps/api/directions/json?origin=";
			Optional<Destinations> desOptionalStart = desDao.findById(perDay.get(0).getDestinationId());
			Optional<Destinations> desOptionalEnd = desDao.findById(perDay.get(perDay.size() - 1).getDestinationId());
			String start = "place_id:" + desOptionalStart.get().getDestinationPlaceId();
			String end = "&destination=place_id:" + desOptionalEnd.get().getDestinationPlaceId();
			String optimize = "&waypoints=optimize:false";
			String destinasi = "";

			for (DestinationSeq data : perDay) {
				Optional<Destinations> desOptional = desDao.findById(data.getDestinationId());
				if (desOptional.isPresent()) {
					if (!desOptionalStart.get().getDestinationPlaceId()
							.equals(desOptional.get().getDestinationPlaceId())
							&& !desOptionalEnd.get().getDestinationPlaceId()
									.equals(desOptional.get().getDestinationPlaceId())) {
						destinasi += "|place_id:" + desOptional.get().getDestinationPlaceId();
					}
				}
			}

			String key = "&key=AIzaSyDy_VTeY85gJui-YspiEBMQh1QkU4PBhG4";
			String finalUrl = googleApi + start + end + optimize + destinasi + key;
//				String finalUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJP7Mmxcc1t2oRQMaOYlQ2AwQ&destination=McLaren+Vale,SA"
//						+ "&waypoints=optimize:true|Barossa+Valley,SA|place_id:ChIJPTJwAtwIy2oRcO2OYlQ2AwQ&key=AIzaSyDy_VTeY85gJui-YspiEBMQh1QkU4PBhG4";
			System.out.println(finalUrl);
			ResponseEntity<String> respEntityRes = null;
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			RestTemplate restTemplate = new RestTemplate();

			try {
				respEntityRes = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, String.class);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode nodeResp = objectMapper.readTree(respEntityRes.getBody());

				ArrayList<String> desSeqArr = new ArrayList<String>();
				if (nodeResp.get("geocoded_waypoints").isArray()) {
					for (JsonNode objNode : nodeResp.get("geocoded_waypoints")) {
						desSeqArr.add(objNode.get("place_id").asText());
					}
				}

				ArrayList<String> arrKeterangan = new ArrayList<String>();
				if (nodeResp.get("routes").isArray()) {
					for (JsonNode objNode : nodeResp.get("routes")) {
						if (objNode.get("legs").isArray()) {
							for (JsonNode objNode2 : objNode.get("legs")) {
								arrKeterangan.add(objNode2.get("distance").get("text").asText());
								arrKeterangan.add(objNode2.get("duration").get("text").asText());
							}
						}
					}
				}

				ArrayList<DestinationSeq> arrDes = perDay;
				for (int i = 0; i < arrDes.size(); i++) {
					Optional<Destinations> desOpt = desDao.findByPlaceId(desSeqArr.get(i));
					if (desOpt.isPresent()) {
						DestinationSeq desSeq = arrDes.get(i);
						desSeq.setDestinationId(desOpt.get().getDestinationId());
						try {
							desSeq.setDistance(arrKeterangan.get(i * 2));
							desSeq.setDuration(arrKeterangan.get(i * 2 + 1));
						} catch (Exception e) {
							desSeq.setDistance("");
							desSeq.setDuration("");
						}
						arrDes.set(i, desSeq);
						desSeqDao.save(arrDes.get(i));
					}
				}

			} catch (HttpStatusCodeException e) {
				System.out.println(e.getCause());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (perDay.size() == 2) {
			DestinationSeq destinationSeq = perDay.get(0);
			Optional<Destinations> startDesOpt = desDao.findById(perDay.get(0).getDestinationId());
			Optional<Destinations> endDesOpt = desDao.findById(perDay.get(1).getDestinationId());
			destinationSeq = DataHelper.getDistanceandDuration(startDesOpt.get().getDestinationPlaceId(),
					endDesOpt.get().getDestinationPlaceId(), destinationSeq);
			desSeqDao.save(destinationSeq);
		}
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
		BigDecimal seqPrice = DataHelper.toBigDecimal(jsonNode.get("price").asText());
		int count = 1;
		this.delete(itineraryId, jsonNode.get("date").asText().replaceAll("-", ""));

		Optional<Itinerary> itrOpt = itrDao.findById(itineraryId);
		LocalDate startDateTime = null;

		if (jsonNode.has("data") && jsonNode.get("data").isArray()) {
			for (JsonNode objNode : jsonNode.get("data")) {
				DestinationSeq destinationSeq = new DestinationSeq();
				String seqId = jsonNode.get("itineraryId").asText() + " - "
						+ jsonNode.get("date").asText().replaceAll("-", "") + " - " + count++;

				destinationSeq.setSeqId(seqId);
				destinationSeq.setItineraryId(itineraryId);
				destinationSeq.setSeqDate(DataHelper.toDate(objNode.get("date").asText().replaceAll("-", "")));
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

				if (startDateTime == null || destinationSeq.getSeqDate().isBefore(startDateTime)) {
					startDateTime = destinationSeq.getSeqDate();
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
			if (destinationSeq.getSeqDate().equals(deletedDate)) {
				desSeqDao.delete(destinationSeq);
			}
		}

		DestinationSeq destinationSeq = new DestinationSeq();
		destinationSeq.setSeqId(itineraryId + " - " + DataHelper.dateToString(deletedDate) + " - 1");
		destinationSeq.setItineraryId(itineraryId);
		destinationSeq.setSeqDate(deletedDate);
		destinationSeq.setSeqPrice(BigDecimal.ZERO);
		destinationSeq.setDestinationId("");
		destinationSeq.setDuration("");
		destinationSeq.setDistance("");
		desSeqDao.save(destinationSeq);

		return "Data Deleted";
	}

	@Transactional
	public Object saveone(JsonNode jsonNode) {
		if (jsonNode.has("itineraryId")) {
			Optional<Itinerary> itrOpt = itrDao.findById(jsonNode.get("itineraryId").asText());
			if (itrOpt.isPresent()) {
				DestinationSeq destinationSeq = new DestinationSeq();
				Destinations destination = new Destinations();
				String destinationPlaceId = jsonNode.has("destinationPlaceId")
						? jsonNode.get("destinationPlaceId").asText()
						: "NoData";
				if (!"NoData".equals(destinationPlaceId)) {
					Optional<Destinations> desOpt = desDao.findByPlaceId(destinationPlaceId);
					if (desOpt.isPresent()) {
						destination = desOpt.get();
					} else {
						destination = Destinations.mapJson(jsonNode);
						String id = "";
						Optional<TableCount> tblCount = tblDao.findById("Destination");
						if (tblCount.isPresent()) {
							id = String.format("DES%03d", tblCount.get().getCount() + 1);
							tblDao.save(new TableCount("Destination", tblCount.get().getCount() + 1));
						} else {
							id = String.format("DES%03d", 1);
							tblDao.save(new TableCount("Destination", 1));
						}
						destination.setDestinationId(id);
						desDao.save(destination);
					}

					Integer terbesar = 1;
					LocalDate seqDate = DataHelper.toDate(jsonNode.get("date").asText().replaceAll("-", ""));
					List<DestinationSeq> listDes = desSeqDao.findByItrId(jsonNode.get("itineraryId").asText());
					if (listDes.size() > 0) {
						for (DestinationSeq desSeq : listDes) {
							if (seqDate.equals(desSeq.getSeqDate())) {
								if ("".equals(desSeq.getDestinationId())) {
									desSeqDao.delete(desSeq);
								} else {
									if (terbesar < Integer.valueOf(desSeq.getSeqId().substring(20, 21))) {
										terbesar = Integer.valueOf(desSeq.getSeqId().substring(20, 21));
									}
								}
							}
						}
					}

					terbesar += 1;
					String seqId = jsonNode.get("itineraryId").asText() + " - "
							+ jsonNode.get("date").asText().replaceAll("-", "") + " - " + terbesar;
					destinationSeq.setSeqId(seqId);
					destinationSeq.setItineraryId(jsonNode.get("itineraryId").asText());
					destinationSeq.setSeqDate(DataHelper.toDate(jsonNode.get("date").asText().replaceAll("-", "")));
					destinationSeq.setSeqPrice(DataHelper.toBigDecimal(jsonNode.get("price").asText()));
					destinationSeq.setDestinationId(destination.getDestinationId());
					destinationSeq.setDestination(destination);
					desSeqDao.save(destinationSeq);

					// Get Distance and Duration
					if (terbesar > 1) {
						String prevSeqId = jsonNode.get("itineraryId").asText() + " - "
								+ jsonNode.get("date").asText().replaceAll("-", "") + " - " + (terbesar - 1);
						Optional<DestinationSeq> desSeqOpt = desSeqDao.findById(prevSeqId);
						if (desSeqOpt.isPresent()) {
							Optional<Destinations> destStart = desDao.findById(desSeqOpt.get().getDestinationId());
							Optional<Destinations> destEnd = desDao.findById(destinationSeq.getDestinationId());
							DestinationSeq desSeq = desSeqOpt.get();
							desSeq = DataHelper.getDistanceandDuration(destStart.get().getDestinationPlaceId(),
									destEnd.get().getDestinationPlaceId(), desSeq);
							desSeqDao.save(desSeq);
						}
					}

					listDes = desSeqDao.findByItrId(jsonNode.get("itineraryId").asText());
					DestinationSeq.sortByDate(listDes);

					ArrayList<ItineraryResponse> arrData = this.splitData(listDes);
					return arrData;
				}
			}
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

	public void deleteone(String seqId) {
		Optional<DestinationSeq> desSeqOpt = desSeqDao.findById(seqId);
		if (desSeqOpt.isPresent()) {
			desSeqDao.delete(desSeqOpt.get());
		}
	}

	@Transactional
	public Object optimize(JsonNode jsonNode) throws JsonMappingException, JsonProcessingException {
		String itineraryId = jsonNode.get("itineraryId").asText();
		LocalDate date = DataHelper.toDate(jsonNode.get("date").asText().replaceAll("-", ""));
		String startDest = jsonNode.get("start").asText();
		String endDest = jsonNode.get("end").asText();
		Optional<Itinerary> itrOpt = itrDao.findById(itineraryId);
		if (itrOpt.isPresent()) {
			List<DestinationSeq> listDes = desSeqDao.findByItrId(itineraryId);
			ArrayList<DestinationSeq> arrDes = new ArrayList<DestinationSeq>();
			for (DestinationSeq destinationSeq : listDes) {
				if (date.equals(destinationSeq.getSeqDate())) {
					arrDes.add(destinationSeq);
				}
			}
			DestinationSeq.sortByDate(arrDes);

			String googleApi = "https://maps.googleapis.com/maps/api/directions/json?origin=";
			String start = "place_id:" + startDest;
			String end = "&destination=place_id:" + endDest;
			String optimize = "&waypoints=optimize:true";
			String destinasi = "";

			for (DestinationSeq data : arrDes) {
				Optional<Destinations> desOptional = desDao.findById(data.getDestinationId());
				if (desOptional.isPresent()) {
					if (!startDest.equals(desOptional.get().getDestinationPlaceId())
							&& !endDest.equals(desOptional.get().getDestinationPlaceId())) {
						destinasi += "|place_id:" + desOptional.get().getDestinationPlaceId();
					}
				}
			}

			String key = "&key=AIzaSyDy_VTeY85gJui-YspiEBMQh1QkU4PBhG4";
			String finalUrl = googleApi + start + end + optimize + destinasi + key;
//			String finalUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJP7Mmxcc1t2oRQMaOYlQ2AwQ&destination=McLaren+Vale,SA"
//					+ "&waypoints=optimize:true|Barossa+Valley,SA|place_id:ChIJPTJwAtwIy2oRcO2OYlQ2AwQ&key=AIzaSyDy_VTeY85gJui-YspiEBMQh1QkU4PBhG4";
			System.out.println(finalUrl);
			ResponseEntity<String> respEntityRes = null;
			HttpHeaders headers = new HttpHeaders();
			HttpEntity<String> entity = new HttpEntity<String>(headers);
			RestTemplate restTemplate = new RestTemplate();

			try {
				respEntityRes = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, String.class);

				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode nodeResp = objectMapper.readTree(respEntityRes.getBody());

				ArrayList<String> desSeqArr = new ArrayList<String>();
				if (nodeResp.get("geocoded_waypoints").isArray()) {
					for (JsonNode objNode : nodeResp.get("geocoded_waypoints")) {
						desSeqArr.add(objNode.get("place_id").asText());
					}
				}

				ArrayList<String> arrKeterangan = new ArrayList<String>();
				if (nodeResp.get("routes").isArray()) {
					for (JsonNode objNode : nodeResp.get("routes")) {
						if (objNode.get("legs").isArray()) {
							for (JsonNode objNode2 : objNode.get("legs")) {
								arrKeterangan.add(objNode2.get("distance").get("text").asText());
								arrKeterangan.add(objNode2.get("duration").get("text").asText());
							}
						}
					}
				}

				for (int i = 0; i < arrDes.size(); i++) {
					Optional<Destinations> desOpt = desDao.findByPlaceId(desSeqArr.get(i));
					if (desOpt.isPresent()) {
						DestinationSeq desSeq = arrDes.get(i);
						desSeq.setDestinationId(desOpt.get().getDestinationId());
						try {
							desSeq.setDistance(arrKeterangan.get(i * 2));
							desSeq.setDuration(arrKeterangan.get(i * 2 + 1));
						} catch (Exception e) {
							desSeq.setDistance("");
							desSeq.setDuration("");
						}
						desSeqDao.save(desSeq);
					}
				}

				// Get Lagi
				List<DestinationSeq> listDestSeq = desSeqDao.findByItrId(itineraryId);
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

			} catch (HttpStatusCodeException e) {
				System.out.println(e.getCause());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

	public Object deleteseq(JsonNode jsonNode) {
		String seqId = jsonNode.get("seqId").asText();
		Optional<DestinationSeq> desSeqOpt = desSeqDao.findById(seqId);
		if (desSeqOpt.isPresent()) {
			String itineraryId = desSeqOpt.get().getItineraryId();
			LocalDate tanggal = desSeqOpt.get().getSeqDate();

			ArrayList<DestinationSeq> arrDes = new ArrayList<DestinationSeq>();
			List<DestinationSeq> listData = desSeqDao.findByItrId(itineraryId);
			for (DestinationSeq destinationSeq : listData) {
				if (destinationSeq.getSeqDate().equals(tanggal)) {
					arrDes.add(destinationSeq);
				}
			}

			if (arrDes.size() == 2) {
				for (DestinationSeq destinationSeq : arrDes) {
					destinationSeq.setDuration("");
					destinationSeq.setDistance("");
					if (seqId.equals(destinationSeq.getSeqId())) {
						desSeqDao.delete(destinationSeq);
					} else {
						desSeqDao.save(destinationSeq);
					}
				}
			} else if (arrDes.size() > 2) {
				DestinationSeq.sortByDate(arrDes);
				for (DestinationSeq destinationSeq : arrDes) {
					if (seqId.equals(destinationSeq.getSeqId())) {
						desSeqDao.delete(destinationSeq);
					}
				}
			} else {
				DestinationSeq newDesSeq = arrDes.get(0);
				newDesSeq.setSeqPrice(BigDecimal.ZERO);
				newDesSeq.setDestinationId("");
				newDesSeq.setDuration("");
				newDesSeq.setDistance("");
				desSeqDao.save(newDesSeq);
			}

			// Optimize
			listData = desSeqDao.findByItrId(itineraryId);
			arrDes = new ArrayList<DestinationSeq>();
			for (DestinationSeq destinationSeq : listData) {
				if (destinationSeq.getSeqDate().equals(tanggal)) {
					arrDes.add(destinationSeq);
				}
			}
			DestinationSeq.sortByDate(arrDes);
			this.optimizeDay(arrDes);

			// Get Lagi
			List<DestinationSeq> listDestSeq = desSeqDao.findByItrId(itineraryId);
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

	public Object editdate(JsonNode jsonNode) {
		String itineraryId = jsonNode.get("itineraryId").asText();
		LocalDate seqDate = DataHelper.toDate(jsonNode.get("seqDate").asText().replaceAll("-", ""));
		Optional<Itinerary> itrOpt = itrDao.findById(itineraryId);
		if (itrOpt.isPresent()) {
			List<DestinationSeq> lisDes = desSeqDao.findByItrId(itineraryId);
			DestinationSeq.sortByDate(lisDes);
			Itinerary itr = itrOpt.get();
			long diff = ChronoUnit.DAYS.between(seqDate, lisDes.get(0).getSeqDate());
			Integer number = (int) Math.abs(diff);
			ArrayList<DestinationSeq> newArr = new ArrayList<DestinationSeq>();

			for (DestinationSeq destinationSeq : lisDes) {
				if (diff < 0) {
					destinationSeq.setSeqDate(destinationSeq.getSeqDate().plusDays(number));
				} else {
					destinationSeq.setSeqDate(destinationSeq.getSeqDate().minusDays(number));
				}
				DestinationSeq newData = destinationSeq;
				String key = itineraryId + " - " + DataHelper.dateToString(newData.getSeqDate()) + " - "
						+ newData.getSeqId().substring(20, 21);
				desSeqDao.delete(destinationSeq);
				newData.setSeqId(key);
				newArr.add(newData);
			}

			for (DestinationSeq destinationSeq : newArr) {
				desSeqDao.save(destinationSeq);
			}
			itr.setStartDate(seqDate);
			itrDao.save(itr);

			// Get Lagi
			List<DestinationSeq> listDestSeq = desSeqDao.findByItrId(itineraryId);
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

	public Object change(JsonNode jsonNode) {
		String itineraryId = jsonNode.get("itineraryId").asText();
		LocalDate seqDate = DataHelper.toDate(jsonNode.get("seqDate").asText().replaceAll("-", ""));

		ArrayList<DestinationSeq> arrDes = new ArrayList<DestinationSeq>();
		HashMap<String, DestinationSeq> mapDes = new HashMap<String, DestinationSeq>();
		List<DestinationSeq> listData = desSeqDao.findByItrId(itineraryId);
		for (DestinationSeq destinationSeq : listData) {
			if (destinationSeq.getSeqDate().equals(seqDate)) {
				arrDes.add(destinationSeq);
				DestinationSeq temp = new DestinationSeq();
				temp.setDestinationId(destinationSeq.getDestinationId());
				temp.setSeqPrice(destinationSeq.getSeqPrice());
				mapDes.put(destinationSeq.getSeqId(), temp);
			}
		}

		DestinationSeq.sortByDate(arrDes);
		ArrayList<String> arrKey = new ArrayList<String>();
		if (jsonNode.get("data").size() == arrDes.size()) {
			for (JsonNode data : jsonNode.get("data")) {
				arrKey.add(data.asText());
			}

			for (int i = 0; i < arrDes.size() && i < arrKey.size(); i++) {
				DestinationSeq destinationSeq = arrDes.get(i);
				destinationSeq.setDestinationId(mapDes.get(arrKey.get(i)).getDestinationId());
				destinationSeq.setSeqPrice(mapDes.get(arrKey.get(i)).getSeqPrice());
				desSeqDao.save(destinationSeq);
				arrDes.set(i, destinationSeq);
			}

			DestinationSeq.sortByDate(arrDes);
			this.optimizeDay(arrDes);

			// Get Lagi
			List<DestinationSeq> listDestSeq = desSeqDao.findByItrId(itineraryId);
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

	public Object adddate(JsonNode jsonNode) {
		try {
			String itineraryId = jsonNode.get("itineraryId").asText();
			List<DestinationSeq> listData = desSeqDao.findByItrId(itineraryId);
			DestinationSeq.sortByDate(listData);
			LocalDate lastDate = listData.get(listData.size() - 1).getSeqDate();
			lastDate = lastDate.plusDays(1);

			DestinationSeq newData = new DestinationSeq();
			String key = itineraryId + " - " + DataHelper.dateToString(lastDate) + " - 1";
			newData.setSeqId(key);
			newData.setItineraryId(itineraryId);
			newData.setSeqDate(lastDate);
			newData.setSeqPrice(BigDecimal.ZERO);
			newData.setDestinationId("");
			newData.setDistance("");
			newData.setDuration("");
			desSeqDao.save(newData);

			// Get Lagi
			List<DestinationSeq> listDestSeq = desSeqDao.findByItrId(itineraryId);
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
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Internal Server Error");
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

	public Object editbudget(JsonNode jsonNode) {
		String itineraryId = jsonNode.get("itineraryId").asText();
		LocalDate seqDate = DataHelper.toDate(jsonNode.get("seqDate").asText().replaceAll("-", ""));
		String budget = jsonNode.get("budget").asText();
		List<DestinationSeq> lisDes = desSeqDao.findByItrId(itineraryId);
		DestinationSeq.sortByDate(lisDes);

		for (DestinationSeq destinationSeq : lisDes) {
			if (destinationSeq.getSeqDate().equals(seqDate)) {
				destinationSeq.setSeqPrice(new BigDecimal(budget));
				desSeqDao.save(destinationSeq);
			}
		}

		// Get Lagi
		List<DestinationSeq> listDestSeq = desSeqDao.findByItrId(itineraryId);
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
		return ResponseEntity.badRequest().body("Check Param");
	}

//	public Object updatesemua(JsonNode jsonNode) {
//		List<Itinerary> listItr = itrDao.findAll();
//		for (Itinerary itinerary : listItr) {
//			List<DestinationSeq> listDes = desSeqDao.findByItrId(itinerary.getItineraryId());
//			DestinationSeq.sortByDate(listDes);
//			ArrayList<ArrayList<DestinationSeq>> splited = new ArrayList<ArrayList<DestinationSeq>>();
//			ArrayList<DestinationSeq> arrData = new ArrayList<DestinationSeq>();
//
//			DestinationSeq prevData = new DestinationSeq();
//			for (DestinationSeq destinationSeq : listDes) {
//				if (arrData.size() > 0 && !prevData.getSeqDate().isEqual(destinationSeq.getSeqDate())) {
//					splited.add(arrData);
//					arrData = new ArrayList<DestinationSeq>();
//				}
//				arrData.add(destinationSeq);
//				prevData = destinationSeq;
//			}
//			splited.add(arrData);
//
//			for (ArrayList<DestinationSeq> perDay : splited) {
//				if (perDay.size() > 1) {
//					String googleApi = "https://maps.googleapis.com/maps/api/directions/json?origin=";
//					Optional<Destinations> desOptionalStart = desDao.findById(perDay.get(0).getDestinationId());
//					Optional<Destinations> desOptionalEnd = desDao
//							.findById(perDay.get(perDay.size() - 1).getDestinationId());
//					String start = "place_id:" + desOptionalStart.get().getDestinationPlaceId();
//					String end = "&destination=place_id:" + desOptionalEnd.get().getDestinationPlaceId();
//					String optimize = "&waypoints=optimize:false";
//					String destinasi = "";
//
//					for (DestinationSeq data : perDay) {
//						Optional<Destinations> desOptional = desDao.findById(data.getDestinationId());
//						if (desOptional.isPresent()) {
//							if (!desOptionalStart.get().getDestinationPlaceId().equals(desOptional.get().getDestinationPlaceId())
//									&& !desOptionalEnd.get().getDestinationPlaceId().equals(desOptional.get().getDestinationPlaceId())) {
//								destinasi += "|place_id:" + desOptional.get().getDestinationPlaceId();
//							}
//						}
//					}
//
//					String key = "&key=AIzaSyDy_VTeY85gJui-YspiEBMQh1QkU4PBhG4";
//					String finalUrl = googleApi + start + end + optimize + destinasi + key;
////					String finalUrl = "https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJP7Mmxcc1t2oRQMaOYlQ2AwQ&destination=McLaren+Vale,SA"
////							+ "&waypoints=optimize:true|Barossa+Valley,SA|place_id:ChIJPTJwAtwIy2oRcO2OYlQ2AwQ&key=AIzaSyDy_VTeY85gJui-YspiEBMQh1QkU4PBhG4";
//					System.out.println(finalUrl);
//					ResponseEntity<String> respEntityRes = null;
//					HttpHeaders headers = new HttpHeaders();
//					HttpEntity<String> entity = new HttpEntity<String>(headers);
//					RestTemplate restTemplate = new RestTemplate();
//
//					try {
//						respEntityRes = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, String.class);
//
//						ObjectMapper objectMapper = new ObjectMapper();
//						JsonNode nodeResp = objectMapper.readTree(respEntityRes.getBody());
//
//						ArrayList<String> desSeqArr = new ArrayList<String>();
//						if (nodeResp.get("geocoded_waypoints").isArray()) {
//							for (JsonNode objNode : nodeResp.get("geocoded_waypoints")) {
//								desSeqArr.add(objNode.get("place_id").asText());
//							}
//						}
//
//						ArrayList<String> arrKeterangan = new ArrayList<String>();
//						if (nodeResp.get("routes").isArray()) {
//							for (JsonNode objNode : nodeResp.get("routes")) {
//								if (objNode.get("legs").isArray()) {
//									for (JsonNode objNode2 : objNode.get("legs")) {
//										arrKeterangan.add(objNode2.get("distance").get("text").asText());
//										arrKeterangan.add(objNode2.get("duration").get("text").asText());
//									}
//								}
//							}
//						}
//
//						ArrayList<DestinationSeq> arrDes = perDay;
//						for (int i = 0; i < arrDes.size(); i++) {
//							Optional<Destinations> desOpt = desDao.findByPlaceId(desSeqArr.get(i));
//							if (desOpt.isPresent()) {
//								DestinationSeq desSeq = arrDes.get(i);
//								desSeq.setDestinationId(desOpt.get().getDestinationId());
//								try {
//									desSeq.setDistance(arrKeterangan.get(i * 2));
//									desSeq.setDuration(arrKeterangan.get(i * 2 + 1));
//								} catch (Exception e) {
//									desSeq.setDistance("");
//									desSeq.setDuration("");
//								}
//								arrDes.set(i, desSeq);
//								desSeqDao.save(arrDes.get(i));
//							}
//						}
//
//					} catch (HttpStatusCodeException e) {
//						System.out.println(e.getCause());
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		return "Oke";
//	}

}
