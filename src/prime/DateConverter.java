package prime;

import java.sql.Date;
import java.time.LocalDate;

public class DateConverter {

    public static Date toDate(LocalDate localDate){
        if(null == localDate){
            return null;
        } else {
            return Date.valueOf(localDate);
        }
    }

    public static LocalDate toLocalDate(Date date){
        if(null == date) {
            return null;
        } else {
            return date.toLocalDate();
        }
    }
}
