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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by terry.wu on 2016/6/2 0002.
 */
@Service
public class StockDayService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${fetch.ifeng.stock}")
    private  String path;
    @Autowired
    private StockRepository stockRepository;
    ExecutorService service = Executors.newFixedThreadPool(5);

    public void getAndSaveInfo() throws IOException {
        List<Stock> list = stockRepository.findAll();
        for(Stock stock :list){
            service.execute(new GetFengRawDailyThread(stock.getId(), path));
        }
    }
}
