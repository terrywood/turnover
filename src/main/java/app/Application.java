package app;

import app.bean.ApiDayResult;
import app.repository.FundFlowPieRepository;
import app.repository.StockRepository;
import app.service.HuanShouLvService;
import app.service.TongUnionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@EnableTransactionManagement
@EnableCaching
@SpringBootApplication
@EnableScheduling
@Configuration
public class Application extends SpringBootServletInitializer {
    private static final Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    ObjectMapper jacksonObjectMapper;

    @Autowired
    HuanShouLvService huanShouLvService;
    @Autowired
    TongUnionService tongUnionService;
    @Value("${fetch.huanshoulv.stocks}")
    public String stockFile;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    StockRepository stockRepository;
    @Autowired
    FundFlowPieRepository fundFlowPieRepository;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
        // return super.configure(builder);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);
        /*ApplicationContext ctx = SpringApplication.run(Application.class, args);
       System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }*/


    }


    @Bean
    public CommandLineRunner demo() {
        return (args) -> {
            //huanShouLvService.fetchPieRaw();
            //huanShouLvService.fetchBoomRaw();
            //huanShouLvService.fetchSurgeRaw();
            //  huanShouLvService.save2DB("20160602");
      /*      huanShouLvService.save2DB("20160603");
            huanShouLvService.save2DB("20160606");
            huanShouLvService.save2DB("20160607");
            huanShouLvService.save2DB("20160608");*/
        };
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Bean
    public CommandLineRunner day() {
        return (args) -> {
            long t1 = System.currentTimeMillis();
            String path = "D:\\Terry\\cloud\\OneDrive\\data\\day";
            File dir = new File(path);
            for (File file : dir.listFiles()) {
                String code = FilenameUtils.getBaseName(file.getName());
              /*  if (!code.equals("000001")) {
                    continue;
                }*/
                List<Object[]> batchArgs = new ArrayList<>();
                ApiDayResult oldFile = jacksonObjectMapper.readValue(file, ApiDayResult.class);
                List<String[]> list = oldFile.getRecord();
                for (String[] arg : list) {
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
                jdbcTemplate.update("delete from stock_day where code=?", code);
                jdbcTemplate.batchUpdate("" +
                        "insert into stock_day (date,open,high,close,low,volume,price_change,p_change,ma5,ma10,ma20,v_ma5,v_ma10,v_ma20,turnover,code,id)" +
                        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", batchArgs);
            }
        };
    }

}
