package http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by terry.wu on 2016/4/26 0026.
 */
public class Main {



    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();
        return response.toString();
    }


    public static HttpEntity getText2(String url) throws Exception {
        CloseableHttpClient httpclient = HttpClients.custom().build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        return (response.getEntity());
    }






    public static void main(String[] args) throws Exception {
        long begin = System.currentTimeMillis();
               Main main = new Main();
          //  main.fetch();
        System.out.println(36608659-7868814-25907608-2832237);
        System.out.println((System.currentTimeMillis() - begin));


        {
           // File file = new File("D:\\jianguoyun\\share\\600104.json");
            //FileUtils.copyURLToFile(new URL("http://server.huanshoulv.com/aimapp/stock/fundflowPie/600104"),file);
          //  Gson gson = new Gson();
           // ObjectMapper jacksonObjectMapper = new ObjectMapper();
           // HttpEntity str = (getText2("http://server.huanshoulv.com/aimapp/stock/fundflowPie/600104"));
          /*  Reader reader = new InputStreamReader(str.getContent());
            ApiResult obj = gson.fromJson(reader, ApiResult.class);*/
            // ApiResult obj =  jacksonObjectMapper.readValue(str.getContent(),ApiResult.class);
            //System.out.println(obj);
          /*  String str  =(getText2("http://server.huanshoulv.com/aimapp/stock/fundflowPie/000850"));
            long begin = System.currentTimeMillis();
            Gson gson = new Gson();
            ApiResult obj = gson.fromJson(str, ApiResult.class);
            System.out.println(obj);
*/
     /*      Map map = gson.fromJson(str, Map.class);
            Map data = (Map) ((List) map.get("data")).get(0);
            System.out.println(MapUtils.getDouble(data, "last_price"))*/
            ;
            // System.out.println((System.currentTimeMillis() - begin));
        }


    }


}
