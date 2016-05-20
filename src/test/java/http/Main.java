package http;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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
        String csvData ="002477,28144,15270,48454767,16396168,15708191,11720808,4629600,48454767,9410092,12862146,15598434,10584095,17.22,6.64,54.07,228.92,907.76,31.73,7.71,54.99,244.87,1125.97,33.84,32.42,24.19,9.55,19.42,26.54,32.19,21.84,17.59,17.53,17.59,17.62,17.68,17.59,17.62,17.61,17.56,17.57,11975,8392,20766176,6991679,7286496,5013621,1474380,27688591,5104407,7139645,8754002,6690537,17.34,6.75,53.74,211.55,819.10,32.99,7.67,54.71,237.24,1133.99,14.43,15.04,10.35,3.04,10.53,14.73,18.07,13.81,17.65,17.59,17.63,17.67,17.88,17.54,17.58,17.58,17.52,17.51,16169,6878,27688591,9404489,8421695,6707187,3155220,20766176,4305685,5722501,6844432,3893558,17.12,6.57,54.37,243.90,956.13,30.19,7.77,55.34,255.39,1112.45,19.41,17.38,13.84,6.51,8.89,11.81,14.13,8.04,17.54,17.49,17.55,17.58,17.60,17.65,17.68,17.64,17.62,17.66,-104042935,-67382832,49783602,121642165,81873961,206539895,276275991,287494447,185916896,273922727,226492389,165852282,13315.66,-14.29,-9832121\",\"close_px\":16.85,\"hslddx\":-1.551,\"hslddy\":-0.17},{\"date\":\"20160517\",\"detail\":\"002477,19476,15299,38447368,11463943,12169101,11779943,3034381,38447368,9150130,11963595,12386921,4946722,19.74,6.87,53.85,238.94,919.51,25.13,7.30,54.50,240.06,916.06,29.82,31.65,30.64,7.89,23.80,31.12,32.22,12.87,17.89,17.89,17.90,17.89,17.87,17.89,17.90,17.89,17.89,17.89,8814,8660,17117623,5143613,5660260,5019766,1293984,21329745,5171500,7052442,6629578,2476225,19.42,6.86,52.75,217.31,808.74,24.63,7.33,54.80,231.80,917.12,13.38,14.72,13.06,3.37,13.45,18.34,17.24,6.44,17.91,17.91,17.91,17.91,17.91,17.88,17.88,17.88,17.87,17.88,10662,6639,21329745,6320330,6508841,6760177,1740397,17117623,3978630,4911153,5757343,2470497,20.01,6.87,54.83,258.02,1023.76,25.78,7.27,54.09,250.32,915.00,16.44,16.93,17.58,4.53,10.35,12.77,14.97,6.43,17.88,17.87,17.89,17.88,17.85,17.91,17.91,17.91,17.90,17.91,-34282220,-10810438,3763087,41329571,54234238,210746588,217790558,205071886,88516458,221557026,214027471,163742315,10795.47,-10.96,-2519319";
        CSVParser parser = CSVParser.parse(csvData, CSVFormat.EXCEL);
        {
            for (CSVRecord csvRecord : parser) {
                System.out.println(csvRecord.get(0));
            }
        }


    }


}
