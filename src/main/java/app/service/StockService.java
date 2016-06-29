package app.service;

import app.entity.Stock;
import app.repository.StockRepository;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**交易日历*/
    private static String  calAll="http://218.244.146.57/static/calAll.csv";

    @Autowired
    private StockRepository stockRepository;



    public void getInfoAnaSaveStock2DB() throws IOException {
      /*  File file = File.createTempFile("csv", "csv");
        FileUtils.copyURLToFile(new URL(ALL), file);
        Reader in = new FileReader(file);
        */
        List<Stock> ret = new ArrayList<>();
        URL url = new URL(ALL);
        Reader in = new InputStreamReader(new BOMInputStream(url.openStream()), "GBK");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
        BeanUtilsBean beanUtils = BeanUtilsBean.getInstance();
        for (CSVRecord record : records) {
             Stock stock = new Stock();
             Map<String ,String> map = record.toMap();
             for(String key : map.keySet()){
                 try {
                     beanUtils.setProperty(stock,key,map.get(key));
                 } catch (IllegalAccessException e) {
                     e.printStackTrace();
                 } catch (InvocationTargetException e) {
                     e.printStackTrace();
                 }
             }
            stock.setId(map.get("code"));
            ret.add(stock);
        }
        stockRepository.save(ret);

    }





}
