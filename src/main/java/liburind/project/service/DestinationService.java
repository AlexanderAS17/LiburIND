package liburind.project.service;

import java.util.ArrayList;
import java.util.HashMap;
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
import liburind.project.helper.DataHelper;
import liburind.project.model.Category;
import liburind.project.model.DestinationCategory;
import liburind.project.model.Destination;
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
			if (ctgOpt.isPresent()) {
				arrCtg.add(ctgOpt.get());
			}
		}
		destination.setDestinationCategory(arrCtg);
		return destination;
	}

	public Object getData(JsonNode jsonNode) {
		if (jsonNode.has("destinationId")) {
			String destinationId = jsonNode.get("destinationId").asText();

			Destination destination = new Destination();
			Optional<Destination> destOpt = destDao.findById(destinationId);
			if (destOpt.isPresent()) {
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

	public Object delete(String destinationId) {
		Optional<Destination> destOpt = destDao.findById(destinationId);
		if (destOpt.isPresent()) {
			destDao.deleteById(destinationId);
			return "Data Deleted";
		} else {
			return ResponseEntity.badRequest().body("Not Found");
		}
	}

	public Object save(JsonNode jsonNode) {
		Destination destination = Destination.mapJson(jsonNode);
		String id = "";

		if (jsonNode.has("destinationPlaceId")) {
			id = jsonNode.get("destinationPlaceId").asText();
			Optional<Destination> destOpt = destDao.findByPlaceId(id);
			if (destOpt.isPresent()) {
				String detail = jsonNode.has("destinationDetail") ? jsonNode.get("destinationDetail").asText()
						: destOpt.get().getDestinationDetail();
				destination.setDestinationDetail(detail);
				destination.setDestinationId(destOpt.get().getDestinationId());
			}
		}
		
		if("".equals(destination.getDestinationId())) {
			Optional<TableCount> tblCount = tblDao.findById("Destination");
			if (tblCount.isPresent()) {
				id = String.format("DES%03d", tblCount.get().getCount() + 1);
				tblDao.save(new TableCount("Destination", tblCount.get().getCount() + 1));
				destination.setDestinationId(id);
			} else {
				id = String.format("DES%03d", 1);
				tblDao.save(new TableCount("Destination", 1));
				destination.setDestinationId(id);
			}
		}
		
		destination = this.updateCategory(destination);
		destDao.save(destination);

		return destination;
	}

	private Destination updateCategory(Destination destination) {
		Optional<Destination> destOpt = destDao.findById(destination.getDestinationId());
		if (destOpt.isPresent()) {
			Destination destinations = this.getCategory(destination);
			HashMap<String, Category> mapCtg = new HashMap<String, Category>();
			List<DestinationCategory> listData = destCtgDao.findByDestination(destination.getDestinationId());

			for (String key : destinations.getDestinationType()) {
				key = DataHelper.translate(key);
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
				key = DataHelper.translate(key);
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
