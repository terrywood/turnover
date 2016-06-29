package app.service;

import app.bean.ApiDayResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by terry.wu on 2016/5/25 0025.
 */
public class GetFengRawDailyThread implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(GetFengRawDailyThread.class);
    private String code;
    private String folder;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private ObjectMapper jacksonObjectMapper = new ObjectMapper();

    public GetFengRawDailyThread(String code, String folder) {
        this.code = code;
        this.folder = folder;
    }

    public GetFengRawDailyThread(String code, String folder, JdbcTemplate jdbcTemplate) {
        this.code = code;
        this.folder = folder;
        this.jdbcTemplate = jdbcTemplate;
    }


    private JdbcTemplate jdbcTemplate;

    @Override
    public void run() {
        String stockFile = String.format("%s%s.json", folder, code);
        //String stockCSVFile = String.format("%s%s.csv",folder, code);
        String market = code.startsWith("6") ? "sh" : "sz";
        try {
            List<Object[]> batchArgs = new ArrayList<>();
            Date csvStartDate = sdf.parse("2016-06-01");
            URL url = new URL("http://api.finance.ifeng.com/akdaily/?code=" + market + code + "&type=last");
            //String stockFile="D:/Terry/cloud/OneDrive/data/day/"+code+".json";
            //log.info(url.toString());

            File file = new File(stockFile);
            if (file.exists()) {
                ApiDayResult oldFile = jacksonObjectMapper.readValue(file, ApiDayResult.class);
                List<String[]> list = oldFile.getRecord();
                String last[] = list.get(list.size() - 1);
                Date lastDate = sdf.parse(last[0]);
                boolean append = false;
                ApiDayResult result = jacksonObjectMapper.readValue(url, ApiDayResult.class);
                List<String[]> list2 = result.getRecord();
                for (String[] args : list2) {

                    Date date = sdf.parse(args[0]);
                    if (date.after(csvStartDate)) {
                        Object[] values = new Object[17];
                        values[0] = (args[0]);
                        for (int i = 1; i < args.length; i++) {
                            values[i] = Double.valueOf(StringUtils.remove(args[i], ","));
                        }
                        String id = StringUtils.remove(args[0], "-") + code;
                        values[15] = code;
                        values[16] = Long.valueOf(id);
                        //System.out.println(ArrayUtils.toString(values));
                        batchArgs.add(values);
                    }


                    if (date.after(lastDate)) {
                        list.add(args);
                        append = true;
                    }
                }
                if (append) {
                    jacksonObjectMapper.writeValue(file, oldFile);
                }
            } else {
                ApiDayResult oldFile = jacksonObjectMapper.readValue(url, ApiDayResult.class);
                List<String[]> list = oldFile.getRecord();
                if (list != null && !list.isEmpty()) {
                    jacksonObjectMapper.writeValue(file, oldFile);
                }
            }

            jdbcTemplate.update("delete from stock_day where stock_id=?", code);
            jdbcTemplate.batchUpdate("insert into stock_day (date,open,high,close,low,volume,price_change,p_change,ma5,ma10,ma20,v_ma5,v_ma10,v_ma20,turnover,stock_id,id)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", batchArgs);

        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("can not get code[" + code + "] message:" + e.getMessage());
        }

    }
}
