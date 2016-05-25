package app.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Calendar;

/**
 * Created by terry.wu on 2016/5/24 0024.
 */
@Data
public class SinaHistorySummary {
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private java.util.Date date;
    private int month;
    private int year;
    private int day;
}
