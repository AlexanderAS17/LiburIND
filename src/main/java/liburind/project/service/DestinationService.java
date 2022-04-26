package liburind.project.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.commons.text.CaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.CategoryRepository;
import liburind.project.dao.DestinationCategoryRepository;
import liburind.project.dao.DestinationRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.model.Category;
import liburind.project.model.DestinationCategory;
import liburind.project.model.Destinations;
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

	private Destinations mapJson(JsonNode jsonNode) {
		String destinationId = jsonNode.has("destinationId") ? jsonNode.get("destinationId").asText() : "";
		String destinationName = jsonNode.has("destinationName") ? jsonNode.get("destinationName").asText() : "";
		BigDecimal destinationRating = jsonNode.has("destinationRating")
				? new BigDecimal(jsonNode.get("destinationRating").asText())
				: BigDecimal.ZERO;
		String destinationDetail = jsonNode.has("destinationDetail") ? jsonNode.get("destinationDetail").asText() : "";

		// Google
		String destinationAddress = jsonNode.has("destinationAddress") ? jsonNode.get("destinationAddress").asText()
				: "";
		String destinationGeometryLat = jsonNode.has("destinationGeometryLoc")
				? jsonNode.get("destinationGeometryLoc").get("lat").asText()
				: "";
		String destinationGeometryLng = jsonNode.has("destinationGeometryLoc")
				? jsonNode.get("destinationGeometryLoc").get("lng").asText()
				: "";
		String destinationPhoto = jsonNode.has("destinationPhoto") ? jsonNode.get("destinationPhoto").asText() : "";
		List<String> destinationTimeOpen = new ArrayList<String>();
		JsonNode arrNode = jsonNode.get("destinationTimeOpen");
		if (arrNode.isArray()) {
			for (final JsonNode objNode : arrNode) {
				destinationTimeOpen.add(objNode.asText());
			}
		}
		String destinationUrl = jsonNode.has("destinationUrl") ? jsonNode.get("destinationUrl").asText() : "";
		Integer destinationUsrJmlh = jsonNode.has("destinationUsrRating") ? jsonNode.get("destinationUsrRating").asInt()
				: 0;
		String destinationWebsite = jsonNode.has("destinationWebsite") ? jsonNode.get("destinationWebsite").asText()
				: "";
		List<String> destinationType = new ArrayList<String>();
		arrNode = jsonNode.get("destinationType");
		if (arrNode.isArray()) {
			for (final JsonNode objNode : arrNode) {
				destinationType.add(objNode.asText());
			}
		}

		for (int i = 0; i < destinationType.size(); i++) {
			destinationType.set(i, CaseUtils.toCamelCase(destinationType.get(i), true, ' ').replaceAll("_", " "));
		}

		return new Destinations(destinationId, destinationName, destinationRating, destinationDetail,
				destinationAddress, destinationGeometryLat, destinationGeometryLng, destinationPhoto,
				destinationTimeOpen, destinationUrl, destinationUsrJmlh, destinationWebsite, destinationType);
	}

	private Destinations getCategory(Destinations destination) {
		ArrayList<Category> arrCtg = new ArrayList<Category>();
		List<DestinationCategory> ctgList = destCtgDao.findByDestination(destination.getDestinationId());
		for (DestinationCategory data : ctgList) {
			Optional<Category> ctgOpt = ctgDao.findById(data.getCategoryId());
			if (ctgOpt.isPresent()) {
				arrCtg.add(ctgOpt.get());
			}
		}
		destination.setDestinationCategory(arrCtg);
		return destination;
	}

	public Object get(JsonNode jsonNode) {
		if (jsonNode.has("destinationId")) {
			String destinationId = jsonNode.get("destinationId").asText();

			Destinations destination = new Destinations();
			Optional<Destinations> destOpt = destDao.findById(destinationId);
			if (destOpt.isPresent()) {
				destination = destOpt.get();
				destination = this.getCategory(destination);
			}
			return destination;
		} else {
			ArrayList<Destinations> arrDest = new ArrayList<Destinations>();
			List<Destinations> listDest = destDao.findAll();
			for (Destinations destination : listDest) {
				destination = this.getCategory(destination);
				arrDest.add(destination);
			}
			return arrDest;
		}
	}

	public ArrayList<Destinations> getCategory(String categoryId) {
		ArrayList<Destinations> arrDest = new ArrayList<Destinations>();
		List<DestinationCategory> ctgList = destCtgDao.findByCategory(categoryId);
		for (DestinationCategory data : ctgList) {
			Optional<Destinations> destOpt = destDao.findById(data.getDestinationId());
			if (destOpt.isPresent()) {
				arrDest.add(destOpt.get());
			}
		}
		return arrDest;
	}

	public Object delete(String destinationId) {
		Optional<Destinations> destOpt = destDao.findById(destinationId);
		if (destOpt.isPresent()) {
			destDao.deleteById(destinationId);
			return "Data Deleted";
		} else {
			return ResponseEntity.badRequest().body("Not Found");
		}
	}

	public Object save(JsonNode jsonNode) {
		Destinations destination = this.mapJson(jsonNode);
		String id = "";

		if (jsonNode.has("destinationId")) {
			id = jsonNode.get("destinationId").asText();
			Optional<Destinations> destOpt = destDao.findById(id);
			if (destOpt.isPresent()) {
				String detail = jsonNode.has("destinationDetail") ? jsonNode.get("destinationDetail").asText()
						: destOpt.get().getDestinationDetail();
				destination.setDestinationDetail(detail);
			} else {
				return ResponseEntity.badRequest().body("Not Found");
			}
		} else {
			Optional<TableCount> tblCount = tblDao.findById("Destination");
			if (tblCount.isPresent()) {
				id = String.format("DES%03d", tblCount.get().getCount() + 1);
				tblDao.save(new TableCount("Destination", tblCount.get().getCount() + 1));
			} else {
				id = String.format("DES%03d", 1);
				tblDao.save(new TableCount("Destination", 1));
			}
		}
		destination.setDestinationId(id);
		destination = this.updateCategory(destination);
		destDao.save(destination);

		return destination;
	}

	public Destinations updateCategory(Destinations destination) {
		Optional<Destinations> destOpt = destDao.findById(destination.getDestinationId());
		if (destOpt.isPresent()) {
			Destinations destinations = this.getCategory(destination);
			HashMap<String, Category> mapCtg = new HashMap<String, Category>();
			List<DestinationCategory> listData = destCtgDao.findByDestination(destination.getDestinationId());

			for (String key : destinations.getDestinationType()) {
				Optional<Category> ctgOpt = ctgDao.findByName(key);
				if (ctgOpt.isPresent()) {
					mapCtg.put(ctgOpt.get().getCategoryId(), ctgOpt.get());
				} else {
					Category category = new Category();
					Optional<TableCount> tblCount = tblDao.findById("Category");
					String id = "";
					if (tblCount.isPresent()) {
						id = String.format("CTG%03d", tblCount.get().getCount() + 1);
						tblDao.save(new TableCount("Category", tblCount.get().getCount() + 1));
					} else {
						id = String.format("CTG001", 1);
						tblDao.save(new TableCount("Category", 1));
					}
					category.setCategoryId(id);
					category.setCategoryName(key);
					ctgDao.save(category);
					
					mapCtg.put(key, category);
				}
			}

			List<Category> arrCtg = new ArrayList<Category>(mapCtg.values());
			destination.setDestinationCategory(arrCtg);

			for (DestinationCategory data : listData) {
				if (!mapCtg.containsKey(data.getCategoryId())) {
					destCtgDao.delete(data);
				} else {
					mapCtg.remove(data.getCategoryId());
				}
			}

			arrCtg = new ArrayList<Category>(mapCtg.values());
			for (Category category : arrCtg) {
				DestinationCategory desCatg = new DestinationCategory();
				Optional<TableCount> tblCount = tblDao.findById("DestinationCategory");
				String id = "";
				if (tblCount.isPresent()) {
					id = String.format("DCT%d", tblCount.get().getCount() + 1);
					tblDao.save(new TableCount("DestinationCategory", tblCount.get().getCount() + 1));
				} else {
					id = String.format("DCT1", 1);
					tblDao.save(new TableCount("DestinationCategory", 1));
				}
				desCatg.setDestinationCategoryId(id);
				desCatg.setCategoryId(category.getCategoryId());
				desCatg.setDestinationId(destination.getDestinationId());
				destCtgDao.save(desCatg);
			}
		} else {
			for (String key : destination.getDestinationType()) {
				Optional<Category> ctgOpt = ctgDao.findByName(key);
				String catgString = "";
				if (ctgOpt.isPresent()) {
					catgString = ctgOpt.get().getCategoryId();
				} else {
					Category category = new Category();
					Optional<TableCount> tblCount = tblDao.findById("Category");
					String id = "";
					if (tblCount.isPresent()) {
						id = String.format("CTG%03d", tblCount.get().getCount() + 1);
						tblDao.save(new TableCount("Category", tblCount.get().getCount() + 1));
					} else {
						id = String.format("CTG001", 1);
						tblDao.save(new TableCount("Category", 1));
					}
					category.setCategoryId(id);
					category.setCategoryName(key);
					ctgDao.save(category);

					catgString = category.getCategoryId();
				}
				DestinationCategory desCatg = new DestinationCategory();
				Optional<TableCount> tblCount = tblDao.findById("DestinationCategory");
				String id = "";
				if (tblCount.isPresent()) {
					id = String.format("DCT%d", tblCount.get().getCount() + 1);
					tblDao.save(new TableCount("DestinationCategory", tblCount.get().getCount() + 1));
				} else {
					id = String.format("DCT1", 1);
					tblDao.save(new TableCount("DestinationCategory", 1));
				}
				desCatg.setDestinationCategoryId(id);
				desCatg.setCategoryId(catgString);
				desCatg.setDestinationId(destination.getDestinationId());
				destCtgDao.save(desCatg);
			}
		}
		return destination;
	}

}
