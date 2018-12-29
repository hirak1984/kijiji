package pvt.hrk.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public final class ConvertUtils {
	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final  DecimalFormat NUMBER_FORMAT = new DecimalFormat("###,###.###");
	public static Integer convert(String str){
		
		try {
			return  NUMBER_FORMAT.parse(Utils.removeBraces(str).trim()).intValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}
	

	
	
}
