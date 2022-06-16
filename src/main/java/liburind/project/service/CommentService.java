package liburind.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import liburind.project.dao.CommentRepository;
import liburind.project.dao.TableCountRepository;
import liburind.project.dao.UserRepository;
import liburind.project.model.Comment;
import liburind.project.model.TableCount;
import liburind.project.model.User;

@Service
public class CommentService {

	@Autowired
	CommentRepository commentDao;

	@Autowired
	TableCountRepository tblDao;
	
	@Autowired
	UserRepository userDao;

	public Object getData(JsonNode jsonNode) {
		if (jsonNode.has("itineraryId")) {
			String itineraryId = jsonNode.get("itineraryId").asText();

			List<Comment> listComment = commentDao.findByitineraryId(itineraryId);
			Comment.sortByDate(listComment);
			return listComment;
		} else {
			List<Comment> listComment = commentDao.findAll();
			return listComment;
		}
	}

	public Object delete(String commentId) {
		Optional<Comment> commentOpt = commentDao.findById(commentId);
		if (commentOpt.isPresent()) {
			commentDao.deleteById(commentId);
			return "Data Deleted";
		} else {
			return ResponseEntity.badRequest().body("Not Found");
		}
	}

	public Object save(JsonNode jsonNode) {
		Comment comment = Comment.mapJson(jsonNode);
		String id = "";
		
		Optional<User> userOpt = userDao.findById(comment.getUserId());
		if(userOpt.isPresent()) {
			comment.setUserName(userOpt.get().getUserName());
		} else {
			comment.setUserName("");
		}

		Optional<TableCount> tblCount = tblDao.findById("Comment");
		if (tblCount.isPresent()) {
			id = String.format("DES%06d", tblCount.get().getCount() + 1);
			tblDao.save(new TableCount("Comment", tblCount.get().getCount() + 1));
			comment.setCommentId(id);
		} else {
			id = String.format("DES%06d", 1);
			tblDao.save(new TableCount("Comment", 1));
			comment.setCommentId(id);
		}

		commentDao.save(comment);
		List<Comment> listComment = commentDao.findByitineraryId(comment.getItineraryId());
		Comment.sortByDate(listComment);
		return listComment;
	}

}
