package liburind.project.helper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import liburind.project.model.DestinationSeq;

public class DataHelper {

	public static LocalDateTime toLongDate(String str) {
		String format = "yyyyMMddHHmmss";
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			LocalDateTime date = LocalDateTime.parse(str.replaceAll("-", ""), formatter);
			return date;
		} catch (Exception e) {
			LocalDateTime date = LocalDateTime.now();
			return date;
		}
	}

	public static LocalDate toDate(String str) {
		String format = "yyyyMMdd";
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			LocalDate date = LocalDate.parse(str.replaceAll("-", ""), formatter);
			return date;
		} catch (Exception e) {
			LocalDate date = LocalDate.now();
			return date;
		}
	}

	public static String longDateToString(LocalDateTime inp) {
		try {
			String str = inp.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			return str;
		} catch (Exception e) {
			return " ";
		}
	}

	public static String dateToString(LocalDate inp) {
		try {
			String str = inp.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			return str;
		} catch (Exception e) {
			return " ";
		}
	}
	
	public static String dateToPrettyString(LocalDate inp) {
		try {
			String str = inp.format(DateTimeFormatter.ofPattern("dd / MM / yyyy"));
			return str;
		} catch (Exception e) {
			return " ";
		}
	}

	public static BigDecimal toBigDecimal(String str) {
		try {
			BigDecimal inp = new BigDecimal(str);
			return inp;
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	public static String getAlphaNumericString(int n) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(n);

		for (int i = 0; i < n; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}

	public static String translate(String str) {
		ResponseEntity<String> respEntityRes = null;
		HttpHeaders headers = new HttpHeaders();

		ObjectMapper mapper = new ObjectMapper();
		ObjectNode param = mapper.createObjectNode();
		param.put("q", str);
		param.put("source", "en");
		param.put("target", "id");

		HttpEntity<String> entity = new HttpEntity<String>(param.toString(), headers);
		RestTemplate restTemplate = new RestTemplate();
		String url = "https://translation.googleapis.com/language/translate/v2?key=AIzaSyDy_VTeY85gJui-YspiEBMQh1QkU4PBhG4";
		try {
			respEntityRes = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode nodeResp = objectMapper.readTree(respEntityRes.getBody());

			if (nodeResp.get("data").get("translations").isArray()) {
				for (JsonNode jsonNode : nodeResp.get("data").get("translations")) {
					return jsonNode.get("translatedText").asText();
				}
			}
		} catch (HttpStatusCodeException e) {
			System.out.println(e.getCause());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static DestinationSeq getDistanceandDuration(String startDest, String endDest, DestinationSeq destinationSeq) {	
		String googleApi = "https://maps.googleapis.com/maps/api/directions/json?origin=";
		String start = "place_id:" + startDest;
		String end = "&destination=place_id:" + endDest;
		String key = "&key=AIzaSyDy_VTeY85gJui-YspiEBMQh1QkU4PBhG4";
		String finalUrl = googleApi + start + end + key;
		System.out.println(finalUrl);
		ResponseEntity<String> respEntityRes = null;
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		RestTemplate restTemplate = new RestTemplate();
		
		try {
			respEntityRes = restTemplate.exchange(finalUrl, HttpMethod.GET, entity, String.class);

			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode nodeResp = objectMapper.readTree(respEntityRes.getBody());

			if (nodeResp.get("routes").isArray()) {
				for (JsonNode objNode : nodeResp.get("routes")) {
					if (objNode.get("legs").isArray()) {
						for (JsonNode objNode2 : objNode.get("legs")) {
							destinationSeq.setDistance(objNode2.get("distance").get("text").asText());
							destinationSeq.setDuration(objNode2.get("duration").get("text").asText());
						}
					}
				}
			}
			
			return destinationSeq;
		} catch (HttpStatusCodeException e) {
			System.out.println(e.getCause());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
