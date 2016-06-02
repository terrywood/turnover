package app.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by terry.wu on 2016/6/2 0002.
 */
@Service
public class TongUnionService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private static String  URL="https://api.wmcloud.com:443/data/v1/api/";
    private static String  TOKEN="2d412c0da8a85583d4164ee7bcb57c8e96beaa38a67805bae855840803949889";

  //  private static String  equFile="D:\\Terry\\cloud\\OneDrive\\data\\stockinfo\\all.csv";


    @Value("${fetch.huanshoulv.stocks}")
    public String stockFile;

    //股票基本信息
    public Iterable<CSVRecord> getEqu(){
        Reader in = null;
        try {
            in = new FileReader(stockFile);
            return CSVFormat.RFC4180.parse(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
//getData("equity/getEqu.csv?field=ticker,secShortName,listDate,totalShares,nonrestFloatShares&equTypeCD=A", new File(stockFile));
    public HttpEntity getData(String api,File file){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            String link =URL+api;
            HttpGet httpget = new HttpGet(link);
            httpget.setHeader("Authorization","Bearer "+TOKEN);
            CloseableHttpResponse response = httpClient.execute( httpget);
            try {

                FileUtils.copyInputStreamToFile(response.getEntity().getContent(),file);
               //return  response.getEntity();

            } finally {
                response.close();
            }
        } catch (ClientProtocolException ex) {
            //ex.printStackTrace();
            // Handle protocol errors
        } catch (IOException ex) {
            // Handle I/O errors
        }
        return  null;
    }
}
