package http;

import app.bean.ApiDayResult;
import app.service.GetSinaRawDailyThread;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 var hq_str_sz159915="创业板,2.155,2.153,2.144,2.164,2.142,2.144,2.145,171869165,370276489.984,3200,2.144,294600,2.143,2391300,2.142,2306000,2.141,2437300,2.140,239700,2.145,231000,2.146,255100,2.147,1400,2.148,173900,2.149,2016-07-05,10:58:45,00";
 var hq_str_sh518880="黄金ETF,2.900,2.899,2.883,2.902,2.871,2.882,2.883,35914100,103661438.000,509800,2.882,201900,2.881,198700,2.880,243200,2.879,302300,2.878,2000,2.883,69200,2.884,541300,2.885,435500,2.886,224400,2.887,2016-07-05,10:58:42,00";

 0：name，股票名字
 1：open，今日开盘价
 2：pre_close，昨日收盘价
 3：price，当前价格
 4：high，今日最高价
 5：low，今日最低价
 6：bid，竞买价，即“买一”报价
 7：ask，竞卖价，即“卖一”报价
 8：volume，成交量 maybe you need do volume/100
 9：amount，成交金额（元 CNY）
 10：b1_v，委买一（笔数 bid volume）
 11：b1_p，委买一（价格 bid price）
 12：b2_v，“买二”
 13：b2_p，“买二”
 14：b3_v，“买三”
 15：b3_p，“买三”
 16：b4_v，“买四”
 17：b4_p，“买四”
 18：b5_v，“买五”
 19：b5_p，“买五”
 20：a1_v，委卖一（笔数 ask volume）
 21：a1_p，委卖一（价格 ask price）
 ...
 30：date，日期；
 31：time，时间；
 * */
public class Test28 {


    ObjectMapper objectMapper = new ObjectMapper();

    public void fetchData() throws IOException {
        URL url = new URL("http://api.finance.ifeng.com/akdaily/?code=sz159915&type=last");
        URL url2 = new URL("http://api.finance.ifeng.com/akdaily/?code=sh518880&type=last");
        ApiDayResult  cy= objectMapper.readValue(url, ApiDayResult.class);
        ApiDayResult  gold= objectMapper.readValue(url2, ApiDayResult.class);
        List<String[]> list = cy.getRecord();
        List<String[]> list2 = gold.getRecord();
        Double cyb = (Double.valueOf(list.get(list.size()-22)[3]) +  Double.valueOf(list.get(list.size()-20)[3])+ Double.valueOf(list.get(list.size()-21)[3]))/3;
        Double hj = (Double.valueOf(list2.get(list2.size()-22)[3]) +  Double.valueOf(list2.get(list2.size()-20)[3])+ Double.valueOf(list2.get(list2.size()-21)[3]))/3;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String link ="http://hq.sinajs.cn/list=sz159915,sh518880";
        HttpGet httpget = new HttpGet(link);
        CloseableHttpResponse response = httpClient.execute(httpget);
        String data  = EntityUtils.toString(response.getEntity());

        String[] cybA = StringUtils.substringBetween(data,"hq_str_sz159915=\"","\";").split(",");
        String[] hjA = StringUtils.substringBetween(data,"hq_str_sh518880=\"","\";").split(",");

        Double cybPrice =  Double.valueOf(cybA[3]);
        Double hjPrice =  Double.valueOf(hjA[3]);

        double x2 = (cybPrice-cyb)/cyb;
        double x8 = (hjPrice-hj)/hj;


        System.out.println("创业板当前价["+cybPrice+"] 黄金当前价["+hjPrice+"] ");

        System.out.println("创业板涨幅["+x2+"] 黄金涨幅["+x8+"] ");
        System.out.println("------------------------------------");
        if(x2>0 && x8>0){
            if(x2>x8){
                System.out.println("持有创业板159915");
            }else{
                System.out.println("持有黄金518880");
            }

        }else{
            System.out.println("卖出回购204001!");
        }
       // System.out.println("创业板昨天收盘价["+list.get(list.size()-1)[3]+"]["+cybA[2]+"] 黄金昨天收盘价["+list2.get(list2.size()-1)[3]+"]["+hjA[2]+"] ");

        // 20天平均价(三天平均过滤)






    }

    public static void main(String[] args) throws IOException {
        Test28  test28 = new Test28();
        test28.fetchData();

    }

}
