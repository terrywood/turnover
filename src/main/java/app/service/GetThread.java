package app.service;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2014/12/4.
 */
public class GetThread extends Thread {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    RequestConfig defaultRequestConfig = RequestConfig.custom()
            .setSocketTimeout(10000)
            .setConnectTimeout(10000)
            .setConnectionRequestTimeout(10000)
            .build();
    private CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
    private String  filePath;
    private String  ticker;
    public GetThread(String file, String ticker) {
        this.filePath= file;
        this.ticker=ticker;

    }

    @Override
    public void run() {
             File file = new File(filePath + ticker + ".json" );
             if(!file.exists()){
                 try {
                     String link ="http://server.huanshoulv.com/aimapp/stock/fundflowPie/"+ticker;
                     log.info(link);
                     HttpGet httpget = new HttpGet(link);
                     CloseableHttpResponse response = httpClient.execute( httpget);
                     try {
                         HttpEntity entity = response.getEntity();
                         String content = EntityUtils.toString(entity);
                         if(content.indexOf("200")>0){
                             FileUtils.writeStringToFile(file,content,"UTF-8",false);
                         }
                     } finally {
                         response.close();
                     }
                 } catch (ClientProtocolException ex) {
                     ex.printStackTrace();
                     // Handle protocol errors
                 } catch (IOException ex) {
                     ex.printStackTrace();
                     // Handle I/O errors
                 }
                 log.info("子线程" + Thread.currentThread() + "执行完毕");
             }else{
                 //log.info("file exists:" +str);
             }

    }
}

