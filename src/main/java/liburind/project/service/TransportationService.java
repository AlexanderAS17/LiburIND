package liburind.project.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.TransportationCategoryRepository;
import liburind.project.dao.TransportationRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.Transportation;
import liburind.project.model.TransportationCategory;

@Service
public class TransportationService {

	@Autowired
	TransportationRepository transDao;

	@Autowired
	TransportationCategoryRepository transCatgDao;

	public Object get(JsonNode jsonNode) {
		if (jsonNode.has("category")) {
			return transDao.findByCtg(jsonNode.get("category").asText());
		} else {
			return transDao.findAllAvailable();
		}
	}

	public Object list(JsonNode jsonNode) {
		List<TransportationCategory> listCtg = transCatgDao.findAll();
		List<Transportation> listTrans = transDao.findAllAvailable();
		ArrayList<TransportationCategory> listData = new ArrayList<TransportationCategory>();

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (TransportationCategory data : listCtg) {
			map.put(data.getTransCategoryId(), 0);
		}

		for (Transportation data : listTrans) {
			if (map.containsKey(data.getTransCategoryId())) {
				map.put(data.getTransCategoryId(), map.get(data.getTransCategoryId()) + 1);
			}
		}
		for (TransportationCategory data : listCtg) {
			data.setJumlah(map.get(data.getTransCategoryId()));
			listData.add(data);
		}

		return listData;
	}

	public Object book(JsonNode jsonNode) {
		try {
			ArrayList<Transportation> arrData = new ArrayList<Transportation>();
			List<Transportation> list = transDao.findByCtg(jsonNode.get("categoryId").asText());

			Integer num = jsonNode.has("jumlah") ? jsonNode.get("jumlah").asInt() : 0;
			String itineraryId = jsonNode.get("itineraryId").asText();
			LocalDate startDate = DataHelper.toDate(jsonNode.get("startDate").asText());
			Integer duration = jsonNode.get("duration").asInt();
			LocalDate endDate = startDate.plusDays(duration - 1);

			for (int i = 0; i < num; i++) {
				Transportation trans = list.get(i);
				trans.setItineraryId(itineraryId);
				trans.setStartDate(startDate);
				trans.setEndDate(endDate);
				trans.setFlagUsed(true);
				arrData.add(trans);

				transDao.save(trans);
			}
			return arrData;
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Check Param");
		}
	}

}
