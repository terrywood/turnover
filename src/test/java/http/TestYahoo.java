package http;

import app.entity.StockData;
import app.util.GetDataFromYahooUtil;

import java.util.List;

/**
 * Created by terry.wu on 2016/5/24 0024.
 */
public class TestYahoo {
    public static void main(String[] args){
        GetDataFromYahooUtil stockUtil = new GetDataFromYahooUtil();
        List<StockData> sd = stockUtil.getStockCsvData("600104.ss", "2016-01-31","2016-03-12");
        System.out.println(sd);
    }
}
