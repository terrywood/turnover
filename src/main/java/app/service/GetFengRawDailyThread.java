package app.service;

import app.bean.ApiDayResult;
import app.history.SinaHistorySummary;
import app.util.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by terry.wu on 2016/5/25 0025.
 */
public class GetFengRawDailyThread implements Runnable {
    private String code;
    private String folder;
    private SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
    private ObjectMapper jacksonObjectMapper = new ObjectMapper();
    public GetFengRawDailyThread(String code, String folder) {
        this.code = code;
        this.folder =folder;
    }
    @Override
    public void run() {
         String stockFile = String.format("%s%s.json",folder, code);
         /*
        String stockSummary =String.format("%s%s_summary.json",folder,code);
        String fetchUrl = String.format("http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/%s.phtml",code);
*/
        String market = code.startsWith("6")?"sh":"sz" ;
        try {
            URL url =new URL("http://api.finance.ifeng.com/akdaily/?code="+market+code+"&type=last");
          //  String stockFile="D:/Terry/cloud/OneDrive/data/day/"+code+".json";
            File file = new File(stockFile);
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
            // e.printStackTrace();
            System.out.println("can not get code["+code+"] message:"+e.getMessage());
        }

    }
}
