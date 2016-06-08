package app;

import app.service.HuanShouLvService;
import app.service.TongUnionService;
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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
            huanShouLvService.save2DB("20160602");
      /*      huanShouLvService.save2DB("20160603");
            huanShouLvService.save2DB("20160606");
            huanShouLvService.save2DB("20160607");
            huanShouLvService.save2DB("20160608");*/
        };
    }

}
