package liburind.project.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;

import liburind.project.dao.DestinationRepository;
import liburind.project.dao.DestinationSeqRepository;
import liburind.project.dao.ItineraryRepository;
import liburind.project.dao.TransportationCategoryRepository;
import liburind.project.dao.TransportationRepository;
import liburind.project.dao.UserRepository;
import liburind.project.helper.DataHelper;
import liburind.project.model.DestinationSeq;
import liburind.project.model.Destinations;
import liburind.project.model.InvoiceResponse;
import liburind.project.model.Itinerary;
import liburind.project.model.Transportation;
import liburind.project.model.TransportationCategory;
import liburind.project.model.User;

@Service
public class TransportationService {

	@Autowired
	TransportationRepository transDao;

	@Autowired
	TransportationCategoryRepository transCatgDao;

	@Autowired
	UserRepository usrDao;

	@Autowired
	ItineraryRepository itrDao;

	@Autowired
	EmailService emailServ;

	@Autowired
	DestinationSeqRepository desSeqDao;

	@Autowired
	DestinationRepository desDao;

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
			String userId = jsonNode.get("userId").asText();
			LocalDate startDate = DataHelper.toDate(jsonNode.get("startDate").asText());
			Integer duration = jsonNode.get("duration").asInt();
			LocalDate endDate = startDate.plusDays(duration);

			int count = 0;
			BigDecimal sum = BigDecimal.ZERO;
			for (int i = 0; i < list.size() && count < num; i++) {
				if (list.get(i).getFlagUsed() == false) {
					count++;
					Transportation trans = list.get(i);
					trans.setItineraryId(itineraryId);
					trans.setStartDate(startDate);
					trans.setEndDate(endDate);
					trans.setFlagUsed(true);
					trans.setUserId(userId);
					arrData.add(trans);

					Optional<TransportationCategory> trnCtgOpt = transCatgDao.findById(trans.getTransCategoryId());
					if (trnCtgOpt.isPresent() && trnCtgOpt.get().getTransPrice() != null) {
						sum = sum.add(trnCtgOpt.get().getTransPrice());
					}

					transDao.save(trans);
				}
			}

			sum = sum.multiply(BigDecimal.valueOf(duration));
			InvoiceResponse response = new InvoiceResponse();
			response.setTransArr(arrData);
			response.setPriceSum(sum);
			
			Optional<User> userOpt = usrDao.findById(userId);
			if(userOpt.isPresent()) {
				response.setNamaUser(userOpt.get().getUserName());
			} else {
				response.setNamaUser("");
			}

			Optional<TransportationCategory> tranCtgOpt = transCatgDao.findById(jsonNode.get("categoryId").asText());
			Optional<Itinerary> itrOpt = itrDao.findById(itineraryId);
			Optional<User> usrOpt = usrDao.findById(userId);
			if (tranCtgOpt.isPresent()) {
				emailServ.kirimTagihan(itrOpt.get(), usrOpt.get(), tranCtgOpt.get().getTransCategoryName(), num,
						startDate, duration, sum);
			}

			return response;
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Check Param");
		}
	}

	public Object endbook(String key) {
		List<Transportation> listTrans = transDao.findByItr("ITR" + key.substring(24, 27));
		boolean flag = false;
		for (Transportation transportation : listTrans) {
			if (("USR" + key.substring(27, 30)).equals(transportation.getUserId())) {
				flag = true;
				transportation.setItineraryId("");
				transportation.setStartDate(null);
				transportation.setEndDate(null);
				transportation.setFlagUsed(false);
				transportation.setUserId("");

				transDao.save(transportation);
			}
		}
		if (flag) {
			return "Data Deleted";
		}
		return ResponseEntity.badRequest().body("Check Param");
	}

	public Object sendinvoice(String key) throws IOException, DocumentException {
		String itineraryId = "ITR" + key.substring(24, 27);
		String userId = "USR" + key.substring(27, 30);
		List<Transportation> listTrans = transDao.findByItr(itineraryId);
		ArrayList<Transportation> arrTrans = new ArrayList<Transportation>();
		BigDecimal sum = BigDecimal.ZERO;
		for (Transportation transportation : listTrans) {
			if (userId.equals(transportation.getUserId())) {
				arrTrans.add(transportation);

				Optional<TransportationCategory> trnCtgOpt = transCatgDao.findById(transportation.getTransCategoryId());
				if (trnCtgOpt.isPresent() && trnCtgOpt.get().getTransPrice() != null) {
					sum = sum.add(trnCtgOpt.get().getTransPrice());
				}
			}
		}

		String tempat = "";
		List<DestinationSeq> arrDest = desSeqDao.findByItrId(itineraryId);
		DestinationSeq.sortByDate(arrDest);
		for (DestinationSeq destinationSeq : arrDest) {
			if (arrTrans.get(0).getStartDate().equals(destinationSeq.getSeqDate())) {
				Optional<Destinations> desOpt = desDao.findById(destinationSeq.getDestinationId());
				if (desOpt.isPresent()) {
					tempat = desOpt.get().getDestinationName();
					break;
				}
			}
		}

		Optional<Itinerary> itrOpt = itrDao.findById(itineraryId);
		Optional<User> usrOpt = usrDao.findById(userId);
		Optional<TransportationCategory> tranCtg = transCatgDao.findById(arrTrans.get(0).getTransCategoryId());
		StringBuilder sb = new StringBuilder();
		sb.append("Detail Pesanan: \n");
		sb.append("Nama Itinerary: " + itrOpt.get().getItineraryName() + "\n");
		sb.append("Nama Pemesan: " + usrOpt.get().getUserName() + "\n");
		sb.append("Jenis Kendaraan: " + tranCtg.get().getTransCategoryName() + "\n");
		sb.append("Jumlah Kendaraan: " + arrTrans.size() + " Unit\n");
		sb.append("Tanggal Pemesanan: " + DataHelper.dateToPrettyString(arrTrans.get(0).getStartDate()) + "\n");
		sb.append("Tempat Penjemputan: " + tempat + "\n");
		sb.append("Jam Penjemputan: 8:00 \n");
		sb.append("Durasi Pemesanan: "
				+ ChronoUnit.DAYS.between(arrTrans.get(0).getStartDate(), arrTrans.get(0).getEndDate()) + " Hari\n");
		sum = sum.multiply(BigDecimal
				.valueOf(ChronoUnit.DAYS.between(arrTrans.get(0).getStartDate(), arrTrans.get(0).getEndDate())));
		sb.append("Jumlah yang Telah dibayarkan: Rp." + sum.toEngineeringString() + "\n\n");
		sb.append("Detail Kendaraan: \n");
		for (int i = 1; i <= arrTrans.size(); i++) {
			Transportation data = arrTrans.get(i - 1);
			sb.append(i + ". Nama Kendaraan: " + data.getTransportationName());
			sb.append("\n    Nomor Telepon Pengemudi: " + data.getTransportationPhone() + "\n");
		}

		// Generate PDF
		File file = File.createTempFile("Invoice", ".pdf");
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(file));

		document.open();
		Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, BaseColor.BLACK);

		Chunk chunk = new Chunk("Nota Pembayaran!", font);
		Phrase phrase = new Phrase();
		phrase.add(chunk);
		Paragraph para = new Paragraph();
		para.add(phrase);
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para);

		chunk = new Chunk("\n", font);
		document.add(chunk);
		document.add(chunk);
		document.add(chunk);

		chunk = new Chunk(sb.toString(), font);
		document.add(chunk);

		chunk = new Chunk("\n\nLunas!", font);
		phrase = new Phrase();
		phrase.add(chunk);
		para = new Paragraph();
		para.add(phrase);
		para.setAlignment(Element.ALIGN_CENTER);
		document.add(para);
		document.close();

		// Send File
		emailServ.kirimInvoice(file, usrOpt.get());
		return null;
	}

	public Object cekpesanan(JsonNode jsonNode) {
		String itineraryId = jsonNode.get("itineraryId").asText();
		List<Transportation> listTrans = transDao.findByItr(itineraryId);

		BigDecimal sum = BigDecimal.ZERO;
		String userId = "";
		for (Transportation transportation : listTrans) {
			Optional<TransportationCategory> trnCtgOpt = transCatgDao.findById(transportation.getTransCategoryId());
			if (trnCtgOpt.isPresent() && trnCtgOpt.get().getTransPrice() != null) {
				sum = sum.add(trnCtgOpt.get().getTransPrice());
			}
			userId = transportation.getUserId();
		}
		
		InvoiceResponse response = new InvoiceResponse();
		response.setTransArr(new ArrayList<Transportation>(listTrans));
		response.setPriceSum(sum);
		Optional<User> userOpt = usrDao.findById(userId);
		if(userOpt.isPresent()) {
			response.setNamaUser(userOpt.get().getUserName());
		} else {
			response.setNamaUser("");
		}

		return response;
	}

}
