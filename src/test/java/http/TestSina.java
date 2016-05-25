package http;

import app.history.SinaHistorySummary;
import app.util.DateUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import  java.util.Date;


/**
 * Created by terry.wu on 2016/5/24 0024.
 */
public class TestSina {
    private static final String NEW_LINE_SEPARATOR = "\n";

    private SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
    public void fetchSinaData(String code){
        String stockFile = String.format("D:\\Terry\\cloud\\%s.csv", code);
        String stockSummary =String.format("D:\\Terry\\cloud\\%s_summary.json",code);
        Document doc = null;
        try {
          //  Reader in = new FileReader(stockFile);
            //  Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader("date", "open", "high", "close", "low", "volume", "amount","factor").parse(in);
   /*     Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            String ticker = record.get("date");
            String open = record.get("open");
            System.out.println(record);
        }
*/
            FileWriter fileWriter = new FileWriter(stockFile,true);
            CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
            CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
            ObjectMapper mapper = new ObjectMapper();
            SinaHistorySummary map = mapper.readValue(new FileInputStream(stockSummary), SinaHistorySummary.class);
            Date lastDay = map.getDate();
            Date days = DateUtils.addDays(lastDay, 1);
            int year =DateUtils.getYear(days);
            int season = DateUtils.getSeason(days);
            System.out.println(year);
            System.out.println(season);
            doc = Jsoup.connect("http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/000100.phtml")
                    .data("year", String.valueOf(year))
                    .data("jidu",String.valueOf(season))
                    .userAgent("Mozilla")
                    .timeout(10000)
                    .get();
            Element body = doc.getElementById("FundHoldSharesTable").getElementsByTag("tbody").get(0);
            Elements data = body.getElementsByTag("tr");
            //date,open,high,close,low,volume,amount,factor

            if(data.size()>1){
                for(int i =data.size()-1 ;i>0;i--){
                    Element tr =data.get(i);
                    Elements td = tr.getElementsByTag("td");
                    String date = td.get(0).text();
                    String open = td.get(1).text();
                    String high = td.get(2).text();
                    String close = td.get(3).text();
                    String low = td.get(4).text();
                    String volume = td.get(5).text();
                    String amount = td.get(6).text();
                    String factor = td.get(7).text();
                    Date _d = sdf.parse(date);
                    if(_d.after(lastDay)){
                         System.out.println("append ->"+date);
                        //csvFilePrinter.printRecord(date,open,high,close,low,volume,amount,factor);
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

    public static void main(String[] args) throws IOException {
        TestSina sina = new TestSina();
        sina.fetchSinaData("000100");

    }

}
