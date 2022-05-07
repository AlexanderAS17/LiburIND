package liburind.project.helper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataHelper {
	
	public static LocalDateTime toLongDate(String str) {
		String format = "yyyyMMddHHmmss";
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
			LocalDateTime date = LocalDateTime.parse(str, formatter);
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
			LocalDate date = LocalDate.parse(str, formatter);
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

}
