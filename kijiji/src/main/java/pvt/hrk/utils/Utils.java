package pvt.hrk.utils;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

public class Utils {

	public static String removeBraces(String str) {
		return str.replace("(", "").replace(")", "").trim();
	}

	public static String format(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		} else {
			final StringBuilder retVal = new StringBuilder();
			str.chars().filter(c -> c > 32 && c < 127).mapToObj(c -> (char) c).forEach(c -> {
				retVal.append(c);
			});
			return retVal.toString();
		}
	}

	public static String format_preserve_space(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		} else {
			final StringBuilder retVal = new StringBuilder();
			str.chars().filter(c -> c >= 32 && c < 127).mapToObj(c -> (char) c).forEach(c -> {
				retVal.append(c);
			});
			return retVal.toString();
		}
	}
	
	public static long GET_EPOCH_DATE(){
		return LocalDate.now().getLong(ChronoField.EPOCH_DAY);
	}
}
