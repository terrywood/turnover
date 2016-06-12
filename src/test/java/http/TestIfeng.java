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




    public void fetchSinaData(String code){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        String _code = code.startsWith("60")?"sh":"sz" + code;
        try {
            URL url =new URL("http://api.finance.ifeng.com/akdaily/?code="+_code+"&type=last");
            String _file="D:/Terry/cloud/OneDrive/data/day/"+code+".json";
            File file = new File(_file);
            if(file.exists()){
                ApiDayResult oldFile = jacksonObjectMapper.readValue(file, ApiDayResult.class);
                List<String[]> list  = oldFile.getRecord();
                String last[] = list.get(list.size()-1);
                Date lastDate = sdf.parse(last[0]);
                boolean append = false;
                ApiDayResult result = jacksonObjectMapper.readValue(url, ApiDayResult.class);
                List<String[]> list2 = result.getRecord();
                for(String[] args :list2){
                    Date date = sdf.parse(args[0]);
                    if(date.after(lastDate)){
                        list.add(args);
                        append = true;
                        //System.out.println("write file code["+code+"] date["+date+"]");
                    }
                }
                if(append){
                    jacksonObjectMapper.writeValue(file,oldFile);
                }
            }else{
               // FileUtils.copyURLToFile(url,file);
                ApiDayResult oldFile = jacksonObjectMapper.readValue(url, ApiDayResult.class);
                List<String[]> list  = oldFile.getRecord();
                if(list!=null && !list.isEmpty()){
                    jacksonObjectMapper.writeValue(file,oldFile);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void read(){
        Reader in = null;
        String stockFile="D:/Terry/cloud/OneDrive/data/stockinfo/all.csv";
        try {
            in = new FileReader(stockFile);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            for(CSVRecord record : records){
                String code =record.get(0);
                fetchSinaData(code);
                //System.out.println(code);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {



        TestIfeng ifeng = new TestIfeng();
        ifeng.read();

   /*     ObjectMapper jacksonObjectMapper = new ObjectMapper();
        ApiDayResult result = jacksonObjectMapper.readValue(new URL("http://api.finance.ifeng.com/akdaily/?code=sh600258&type=last"), ApiDayResult.class);
        String[] one = result.getRecord().get(0);
        System.out.println(one[0]);*/
    }

}
