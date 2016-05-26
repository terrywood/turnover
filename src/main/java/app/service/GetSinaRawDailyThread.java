package app.service;

import app.history.SinaHistorySummary;
import app.util.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by terry.wu on 2016/5/25 0025.
 */
public class GetSinaRawDailyThread implements Runnable {

    private String code;
    private String folder;

    private static final String NEW_LINE_SEPARATOR = "\n";
    private SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");

    //private  String  syncFolder = "D:\\Terry\\cloud\\OneDrive\\history\\day\\raw_data\\";


    public GetSinaRawDailyThread(String code, String folder) {
        this.code = code;
        this.folder =folder;
    }

    @Override
    public void run() {
        String stockFile = String.format("%s%s.csv",folder, code);
        String stockSummary =String.format("%s%s_summary.json",folder,code);
        String fetchUrl = String.format("http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/%s.phtml",code);
        Document doc = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            SinaHistorySummary summary = mapper.readValue(new FileInputStream(stockSummary), SinaHistorySummary.class);
            Date lastDay = summary.getDate();
            System.out.println("lastDay->"+lastDay);
            if(DateUtils.truncatedEquals(new Date(),lastDay, Calendar.DATE)){
                System.out.println("It is last update version");
                return;
            }
            FileWriter fileWriter = new FileWriter(stockFile,true);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            Date days = DateUtils.addDays(lastDay, 1);
            int year =DateUtils.getYear(days);
            int season = DateUtils.getSeason(days);
            doc = Jsoup.connect(fetchUrl)
                    .data("year", String.valueOf(year))
                    .data("jidu",String.valueOf(season))
                    .userAgent("Mozilla")
                    .timeout(10000)
                    .get();
            Element body = doc.getElementById("FundHoldSharesTable").getElementsByTag("tbody").get(0);
            Elements data = body.getElementsByTag("tr");
            //date,open,high,close,low,volume,amount,factor

            //System.out.println(data.html());

            if(data.size()>1){
                for(int i =data.size()-1 ;i>0;i--){
                    Element tr =data.get(i);
                    Elements td = tr.getElementsByTag("td");
                    String date = td.get(0).text();
                    //System.out.println("date->"+date);
                    String open = td.get(1).text();
                    String high = td.get(2).text();
                    String close = td.get(3).text();
                    String low = td.get(4).text();
                    String volume = td.get(5).text();
                    String amount = td.get(6).text();
                    String factor = td.get(7).text();
                    Date _d = sdf.parse(date);
                    if(_d.after(lastDay) ){
                        csvFilePrinter.printRecord(date,open,high,close,low,volume,amount,factor);
                    }
                    if(i==1){
                        Date last = sdf.parse(date);
                        if(!last.equals(lastDay)){
                            System.out.println("append "+code+"->"+date);
                            summary.setDate(last);
                            summary.setDay(DateUtils.getDay(last));
                            summary.setYear(DateUtils.getYear(last));
                            summary.setMonth(DateUtils.getMonth(last));
                            mapper.writeValue(new File(stockSummary),summary);
                        }
                    }
                }
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
