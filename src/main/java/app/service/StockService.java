package app.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;

/**
 * Created by terry.wu on 2016/6/2 0002.
 */
@Service
public class StockService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * code,代码
     name,名称
     industry,所属行业
     area,地区
     pe,市盈率
     outstanding,流通股本
     totals,总股本(万)
     totalAssets,总资产(万)
     liquidAssets,流动资产
     fixedAssets,固定资产
     reserved,公积金
     reservedPerShare,每股公积金
     eps,每股收益
     bvps,每股净资
     pb,市净率
     timeToMarket,上市日期
     * */
    private static String  ALL="http://218.244.146.57/static/all.csv";


    //股票基本信息
    public void getEqu() throws IOException {
        File file = File.createTempFile("csv", "csv");
        FileUtils.copyURLToFile(new URL(ALL), file);
        Reader in = new FileReader(file);
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
                System.out.println(record.get("code"));
        }
    }


}
