package http;

import app.bean.ApiDayResult;
import app.service.GetSinaRawDailyThread;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by terry.wu on 2016/5/24 0024.
 */
public class TestIfeng {


    public void fetchSinaData(String code) {


    }


    public void read() {
        ExecutorService service = Executors.newFixedThreadPool(25);
        Reader in = null;
        String stockFile = "D:/Terry/cloud/OneDrive/data/stockinfo/all.csv";
        String path = "D:/Terry/cloud/OneDrive/data/day/";
        try {
            in = new FileReader(stockFile);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                String code = record.get(0);
               // service.execute(new GetFengRawDailyThread(code, path));
                //System.out.println(code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }

    public static void main(String[] args) throws IOException {
        TestIfeng ifeng = new TestIfeng();
        ifeng.read();
    }

}
