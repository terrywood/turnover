package http;

import app.history.SinaHistorySummary;
import app.service.GetSinaDailyThread;
import app.service.GetSinaRawDailyThread;
import app.util.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import  java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by terry.wu on 2016/5/24 0024.
 */
public class TestSina {




    public void fetchSinaData(String code){




    }

    public static void main(String[] args) throws IOException {
        TestSina sina = new TestSina();
        ExecutorService service =  Executors.newFixedThreadPool(25);

        String  syncFolder = "D:\\Terry\\cloud\\OneDrive\\history\\day\\raw_data\\";
        String  useFolder = "D:\\Terry\\cloud\\OneDrive\\history\\day\\";

        File folder = new File(syncFolder);
        File[] files = folder.listFiles();
        for(File file:files){
            String baseName = (FilenameUtils.getBaseName(file.getName()));
            if(baseName.length()==6){
                /***
                 * GetSinaRawDailyThread and  GetSinaDailyThread 不能同时使用。应该先行完 get sina
                 * data , 再行生写非复权数据
                 * */
                //service.execute(new GetSinaRawDailyThread(baseName,syncFolder));
                service.execute(new GetSinaDailyThread(baseName,useFolder));
            }
        }
        service.shutdown();

    }

}
