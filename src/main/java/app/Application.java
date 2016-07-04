package app;

import app.repository.FundFlowPieRepository;
import app.repository.StockRepository;
import app.service.HuanShouLvService;
import app.service.StockDayService;
import app.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateUtils;
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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    StockDayService stockDayService;
    @Value("${fetch.huanshoulv.stocks}")
    public String stockFile;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    StockService stockService;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    FundFlowPieRepository fundFlowPieRepository;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
        // return super.configure(builder);+

    }

    @Bean(destroyMethod = "shutdown")
    public Executor taskScheduler() {
        return Executors.newScheduledThreadPool(5);
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



    //@Bean
    public CommandLineRunner huanShouLv() {
        return (args) -> {
            String folder ="D:\\Terry\\cloud\\OneDrive\\data\\huanshoulv_raw\\pie";
            File f = new File(folder);
            for(File file : f.listFiles()){
                String day = file.getName();
                System.out.println(day);
                huanShouLvService.save2DB(day);
            }

        };
    }

   @Bean
    public CommandLineRunner everydayAfter1500() {
        return (args) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            long start = System.currentTimeMillis();
            Date today = DateUtils.truncate(new java.util.Date(), Calendar.DATE);
            stockService.getInfoAnaSaveStock2DB();
            System.out.println("use time fetch data ok step 1");
            huanShouLvService.fetchPieRaw();
            System.out.println("use time fetch data ok step 2");
            huanShouLvService.fetchBoomRaw();
            System.out.println("use time fetch data ok step 3");
            huanShouLvService.fetchSurgeRaw();
            System.out.println("use time fetch data ok step 4");
            huanShouLvService.save2DB(sdf.format(today));
            System.out.println("use time fetch data ok step 5");
            huanShouLvService.fetchStockExtend();
            System.out.println("use time fetch data ok step 6");
            stockDayService.getAndSaveInfo();
            System.out.println("use time fetch data ok step 8");
            long end = (System.currentTimeMillis() - start )/1000;
            System.out.println("use time fetch data ["+end+"]");

        };
    }

}
