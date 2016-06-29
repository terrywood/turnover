package app.service;

import app.bean.ApiDayResult;
import app.entity.Stock;
import app.repository.StockRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    ObjectMapper jacksonObjectMapper;
    @Autowired
    JdbcTemplate jdbcTemplate;
    SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
    public void saveInfo2DB() throws IOException, ParseException {
        Date when  = sdf.parse("2016-06-01");
        File dir = new File(path);
        jdbcTemplate.update("delete from stock_day");
        for (File file : dir.listFiles()) {
            String code = FilenameUtils.getBaseName(file.getName());
            log.info("saveInfo2DB code["+code+"]");;
              /*  if (!code.equals("000001")) {
                    continue;
                }*/
            List<Object[]> batchArgs = new ArrayList<>();
            ApiDayResult oldFile = jacksonObjectMapper.readValue(file, ApiDayResult.class);
            List<String[]> list = oldFile.getRecord();
            for (String[] arg : list) {
                Date date = sdf.parse(arg[0]);
                if(date.before(when)){
                    continue;
                }
                Object[] values = new Object[17];
                values[0] = (arg[0]);
                for (int i = 1; i < arg.length; i++) {
                    values[i] = Double.valueOf(StringUtils.remove(arg[i], ","));
                }
                String id = StringUtils.remove(arg[0], "-") + code;
                values[15] = code;
                values[16] = Long.valueOf(id);
                //System.out.println(ArrayUtils.toString(values));
                batchArgs.add(values);
            }

            //jdbcTemplate.update("delete from stock_day where code=?", code);
            jdbcTemplate.batchUpdate("insert into stock_day (date,open,high,close,low,volume,price_change,p_change,ma5,ma10,ma20,v_ma5,v_ma10,v_ma20,turnover,stock_id,id)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", batchArgs);
        }


    }

    public void updateStockOutstanding(Date date) throws IOException {
        String sql="UPDATE stock_day a, stock b SET b.outstanding_assets = b.outstanding * a.close WHERE a.date=? AND a.stock_id =b.id";
        jdbcTemplate.update(sql,date);
    }

    public void getAndSaveInfo() throws IOException, InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(20);
        List<Stock> list = stockRepository.findAll();
        for(Stock stock :list){
            service.submit(new GetFengRawDailyThread(stock.getId(), path,this.jdbcTemplate));
        }
        service.shutdown();
        while (!service.awaitTermination(20, TimeUnit.SECONDS)) {
           log.info("线程池没有关闭");
        }
        log.info("线程池已经关闭");
        updateStockOutstanding(DateUtils.truncate(new Date(), Calendar.DATE));
    }
}
