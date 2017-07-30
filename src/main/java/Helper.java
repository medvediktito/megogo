import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


class Helper {

    static final String NEW_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static Date convertToDate(String stringDate) throws ParseException {
        DateFormat format = new SimpleDateFormat(NEW_FORMAT);
        Date date = format.parse(stringDate);
        return date;
    }

    static String getCurrentDate() {
        SimpleDateFormat sdfDate = new SimpleDateFormat(NEW_FORMAT);
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }

    static String convetrToNewFormat(String dateInOldFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh,mm,ss");
        Date date = sdf.parse(dateInOldFormat);
        sdf.applyPattern(NEW_FORMAT);
        String dateInNewFormat = sdf.format(date);
        return dateInNewFormat;
    }


}
