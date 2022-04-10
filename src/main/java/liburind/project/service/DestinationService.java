package liburind.project.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.CategoryRepository;
import liburind.project.dao.DestinationCategoryRepository;
import liburind.project.dao.DestinationRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.model.Category;
import liburind.project.model.Destination;
import liburind.project.model.DestinationCategory;
import liburind.project.model.TableCount;

@Service
public class DestinationService {
	
	@Autowired
	DestinationRepository destDao;
	
	@Autowired
	DestinationCategoryRepository destCtgDao;
	
	@Autowired
	CategoryRepository ctgDao;
	
	@Autowired
	TableCountRepository tblDao;
	
	private Destination getCategory(Destination destination) {
		ArrayList<Category> arrCtg = new ArrayList<Category>();
		List<DestinationCategory> ctgList = destCtgDao.findByDestination(destination.getDestinationId());
		for (DestinationCategory data : ctgList) {
			Optional<Category> ctgOpt = ctgDao.findById(data.getCategoryId());
			if(ctgOpt.isPresent()) {
				arrCtg.add(ctgOpt.get());
			}
		}
		destination.setCategory(arrCtg);
		return destination;
	}
	
	public Object get(JsonNode jsonNode) {
		if(jsonNode.has("destinationId")) {
			String destinationId = jsonNode.get("destinationId").asText();
			
			Destination destination = new Destination();
			Optional<Destination> destOpt = destDao.findById(destinationId);
			if(destOpt.isPresent()) {
				destination = destOpt.get();
				destination = this.getCategory(destination);
			}
			return destination;
		} else {
			ArrayList<Destination> arrDest = new ArrayList<Destination>();
			List<Destination> listDest = destDao.findAll();
			for (Destination destination : listDest) {
				destination = this.getCategory(destination);
				arrDest.add(destination);
			}
			return arrDest;
		}
	}

	public ArrayList<Destination> getCategory(String categoryId) {
		ArrayList<Destination> arrDest = new ArrayList<Destination>();
		List<DestinationCategory> ctgList = destCtgDao.findByCategory(categoryId);
		for (DestinationCategory data : ctgList) {
			Optional<Destination> destOpt = destDao.findById(data.getDestinationId());
			if(destOpt.isPresent()) {
				arrDest.add(destOpt.get());
			}
		}
		return arrDest;
	}

	public Object delete(String destinationId) {
		Optional<Destination> destOpt = destDao.findById(destinationId);
		if(destOpt.isPresent()) {
			destDao.deleteById(destinationId);
			return "Data Deleted";
		} else {
			return ResponseEntity.badRequest().body("Not Found");
		}
	}

	public Object save(JsonNode jsonNode) {
		Destination destination = new Destination();
		String name = jsonNode.get("destinationName").asText();
		String city = jsonNode.get("destinationCity").asText();
		String score = jsonNode.get("destinationScore").asText();
		
		if(jsonNode.has("destinationId")) {
			String id = jsonNode.get("destinationId").asText();
			Optional<Destination> destOpt = destDao.findById(id);
			if(destOpt.isPresent()) {
				destination = destOpt.get();
				destination.setDestinationCity(city);
				destination.setDestinationName(name);
				destination.setDestinationScore(new BigDecimal(score));
				
				destDao.save(destination);
			} else {
				return ResponseEntity.badRequest().body("Not Found");
			}
		} else {
			Optional<TableCount> tblCount = tblDao.findById("Destination");
			String id = "";
			if(tblCount.isPresent()) {
				id = String.format("DES%03d", tblCount.get().getCount() + 1);
				tblDao.save(new TableCount("Destination", tblCount.get().getCount() + 1));
			} else {
				id = String.format("DES%03d", 1);
				tblDao.save(new TableCount("Destination", 1));
			}
			
			destination = new Destination();
			destination.setDestinationId(id);
			destination.setDestinationName(name);
			destination.setDestinationCity(city);
			destination.setDestinationScore(new BigDecimal(score));
			
			destDao.save(destination);
		}
		return destination;
	}

}
