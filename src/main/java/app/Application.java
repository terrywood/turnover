package app;

import app.repository.FundFlowPieRepository;
import app.repository.StockRepository;
import app.service.HuanShouLvService;
import app.service.StockDayService;
import app.service.StockService;
import com.fasterxml.jackson.databind.ObjectMapper;
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


   // @Bean
    public CommandLineRunner stock() {
        return (args) -> {
            stockService.getInfo();
        };
    }


    //@Bean
    public CommandLineRunner daily() {
        return (args) -> {
            //stockDayService.getAndSaveInfo();
            stockDayService.saveInfo2DB();
        };
    }
    @Bean
    public CommandLineRunner huanShouLv() {
        return (args) -> {


          /*  huanShouLvService.fetchPieRaw();
            huanShouLvService.fetchBoomRaw();
            huanShouLvService.fetchSurgeRaw();

            String folder ="D:\\Terry\\cloud\\OneDrive\\data\\huanshoulv_raw\\pie";
            File f = new File(folder);
            for(File file : f.listFiles()){
                String day = file.getName();
                System.out.println(day);
                huanShouLvService.save2DB(day);
            }*/

            huanShouLvService.fetchStockExtend();
        };
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //@Bean
    public CommandLineRunner day() {
        return (args) -> {
        };
    }

}
