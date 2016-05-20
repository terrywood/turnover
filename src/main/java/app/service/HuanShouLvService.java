package app.service;

import app.entity.ApiResult;
import app.entity.FundFlowPie;
import app.repository.FundFlowPieRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by terry.wu on 2016/5/18 0018.
 */
@Service
public class HuanShouLvService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    //BlockingQueue<String> consumerQueue = new LinkedBlockingQueue();

    @Value("${save.huanshoulv.data.path}")
    private String filePath;

    @Value("${fetch.huanshoulv.stocks}")
    private String stockFile;
    @Autowired
    FundFlowPieRepository fundFlowPieRepository;
    @Autowired
    ObjectMapper objectMapper;

    //@PostConstruct
    public void postConstruct() {
        fetch();
        save2DB();
    }

    @Autowired
    public EntityManager em;


   /* public void batchUpdate(List list) {
        for (int i = 0; i < list.size(); i++) {
            em.persist(list.get(i));
            if (i % 30 == 0) {
                em.flush();
                em.clear();
            }
        }
    }*/
    @Transactional
    public void save2DB() {
        String today = DateUtils.formatDate(new java.util.Date(), "yyyyMMdd");
        String file = filePath + today + "/";
        File dir = new File(file);
        List<FundFlowPie> list = new ArrayList<>();
        for (File json : dir.listFiles()) {
            String code = FilenameUtils.getBaseName(json.getName());
            try {
                ApiResult result = objectMapper.readValue(new FileInputStream(json), ApiResult.class);
                if (result.getStatus().equals("200")) {
                    List<FundFlowPie> list2 = result.getData();
                        for(FundFlowPie entity : list2){
                             String date = DateUtils.formatDate(entity.getDate(), "yyyyMMdd");
                             Long id = Long.valueOf(date+code);
                             //if(!fundFlowPieRepository.exists(id)){
                                 entity.setId(id);
                                 entity.setCode(code);
                                  list.add(entity);
                               //  fundFlowPieRepository.save(entity);
                             //}
                        }
                    }
            } catch (IOException e) {
                log.info(json.getName());
                e.printStackTrace();
            }
        }

        fundFlowPieRepository.save(list);
    }

    public void fetch()  {
        ExecutorService consumerService = Executors.newFixedThreadPool(20);
        try {
            Reader in = new FileReader(stockFile);
            String file = filePath + DateUtils.formatDate(new java.util.Date(), "yyyyMMdd") + "/";
            Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
            for (CSVRecord record : records) {
                String columnOne = record.get(0);
                GetThread getThread = new GetThread(file, columnOne);
                consumerService.execute(getThread);
            }
            consumerService.shutdown();
            while (!consumerService.awaitTermination(5, TimeUnit.SECONDS)) {
                log.info("线程池没有关闭");
            }
            log.info("线程池已经关闭");
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }


    }

}
