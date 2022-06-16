package liburind.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import liburind.project.dao.DestinationCategoryRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.model.DestinationCategory;
import liburind.project.model.TableCount;

@Service
public class DestinationCategoryService {

	@Autowired
	DestinationCategoryRepository destCatgDao;
	
	@Autowired
	TableCountRepository tblDao;

	public Object save(String destinationId, String categoryId) {
		DestinationCategory desCatg = new DestinationCategory();
		Optional<DestinationCategory> desCatgOpt = destCatgDao.findByKey(destinationId, categoryId);
		if (!desCatgOpt.isPresent()) {
			Optional<TableCount> tblCount = tblDao.findById("DestinationCategory");
			String id = "";
			if(tblCount.isPresent()) {
				id = String.format("DCT%d", tblCount.get().getCount() + 1);
				tblDao.save(new TableCount("DestinationCategory", tblCount.get().getCount() + 1));
			} else {
				id = String.format("DCT1", 1);
				tblDao.save(new TableCount("DestinationCategory", 1));
			}
			desCatg.setDestinationCategoryId(id);
			desCatg.setCategoryId(categoryId);
			desCatg.setDestinationId(destinationId);
			destCatgDao.save(desCatg);
		}
		return desCatg;
	}    

	public Object get(String destinationId) {
		List<DestinationCategory> arrCatg = destCatgDao.findByDestination(destinationId);
		if(arrCatg.size() > 0) {
			return arrCatg;
		}
		return ResponseEntity.badRequest().body(new ArrayList<DestinationCategory>());
	}

	public Boolean delete(String destinationId, String categoryId) {
		Optional<DestinationCategory> desCatgOpt = destCatgDao.findByKey(destinationId, categoryId);
		if (desCatgOpt.isPresent()) {
			destCatgDao.deleteById(desCatgOpt.get().getDestinationCategoryId());
			return true;
		}
		return false;
	}

}
