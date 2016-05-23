package app.service;

import app.entity.*;
import app.repository.FundFlowPieDetailRepository;
import app.repository.FundFlowPieMasterRepository;
import app.repository.FundFlowPieRepository;
import app.repository.FundFlowPieSlaveRepository;
import app.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    FundFlowPieDetailRepository fundFlowPieDetailRepository;
    @Autowired
    FundFlowPieSlaveRepository fundFlowPieSlaveRepository;
    @Autowired
    FundFlowPieMasterRepository fundFlowPieMasterRepository;
    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void postConstruct() {
        String today = DateFormatUtils.format(new Date(), "yyyMMdd");

        fetch(today);

        //save2DB(today);

/*        Date yesterday = DateUtils.addDays(new Date(), -1);
        yesterday = DateUtils.truncate(yesterday, Calendar.DATE);*/


        splitDate();
    }


    public FundFlowPie findOne(Long id) {
        return fundFlowPieRepository.findOne(id);
    }

    /**
     * 当一只股票符合，买单均手>卖单均手，
     * 大户买入占比>大户卖出占比，
     * 机构买入占比>机构卖出占比；
     * 再观察被动买单均手>=被动卖单均手，
     * 同时被动买单均手>=主动买单均手，
     * 说明主力看好后市，
     * 压盘吃货。
     */
    public void press() {

    }

    public void splitDate() {
        String[] fields = AppUtils.fields;

        Pageable page = new PageRequest(0, 10000, Sort.Direction.ASC, "id");
        //Page<FundFlowPie>  pageList  = fundFlowPieRepository.findAll(page);
        //Page<FundFlowPie> pageList = fundFlowPieRepository.findByDate(date, page);

/*

        List<FundFlowPieDetail> detailList = new ArrayList<>();
        List<FundFlowPieMaster> masterList = new ArrayList<>();
        List<FundFlowPieSlave> slaveList = new ArrayList<>();
*/

        List<FundFlowPie> entityList = fundFlowPieRepository.findByIsSplit(false,page);
        BeanUtilsBean beanUtils = BeanUtilsBean.getInstance();
        for (FundFlowPie entity :entityList) {
            String data[] = entity.getDetail().split(",");

            entity.setJiPrice(Double.valueOf(data[121]));
            entity.setDaPrice(Double.valueOf(data[122]));
            entity.setZhongPrice(Double.valueOf(data[123]));
            entity.setSanPrice(Double.valueOf(data[124]));

            entity.setMasterLv(Double.valueOf(data[134]));

            entity.setSplit(true);

           // System.out.println(entity);

            FundFlowPieDetail obj = new FundFlowPieDetail();
            FundFlowPieMaster master = new FundFlowPieMaster();
            FundFlowPieSlave slave = new FundFlowPieSlave();


            for (int i = 1; i <= 40; i++) {
                try {
                    beanUtils.setProperty(obj, fields[i], data[i]);
                    beanUtils.setProperty(master, fields[i], data[i + 40]);
                    beanUtils.setProperty(slave, fields[i], data[i + 80]);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }


            master.setId(entity.getId());
            master.setCode(entity.getCode());
            entity.setFundFlowPieMaster(master);

          //  masterList.add(master);

            obj.setCode(entity.getCode());
            obj.setId(entity.getId());
           // detailList.add(obj);
            entity.setFundFlowPieDetail(obj);

            slave.setCode(entity.getCode());
            slave.setId(entity.getId());

            entity.setFundFlowPieSlave(slave);
         //   slaveList.add(slave);

        }


        fundFlowPieRepository.save(entityList);
/*        fundFlowPieDetailRepository.save(detailList);
        fundFlowPieMasterRepository.save(masterList);
        fundFlowPieSlaveRepository.save(slaveList);*/

    }


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
    public void save2DB(String day) {

        String file = filePath + day + "/";
        File dir = new File(file);
        List<FundFlowPie> list = new ArrayList<>();
        for (File json : dir.listFiles()) {
            String code = FilenameUtils.getBaseName(json.getName());
            try {
                ApiResult result = objectMapper.readValue(new FileInputStream(json), ApiResult.class);
                if (result.getStatus().equals("200")) {
                    List<FundFlowPie> list2 = result.getData();
                    for (FundFlowPie entity : list2) {
                        String date = DateFormatUtils.format(entity.getDate(), "yyyyMMdd");
                        Long id = Long.valueOf(date + code);
                        //if(!fundFlowPieRepository.exists(id)){
                        entity.setId(id);
                        entity.setCode(code);
                        entity.setSplit(false);
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

    public void fetch(String day) {
        ExecutorService consumerService = Executors.newFixedThreadPool(20);
        try {
            Reader in = new FileReader(stockFile);
            String file = filePath + day + "/";
            Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
            for (CSVRecord record : records) {
                String ticker = record.get(0);
                File fileName = new File(file + ticker + ".json");
                if (!fileName.exists()) {
                    GetThread getThread = new GetThread(fileName, ticker);
                    consumerService.execute(getThread);
                }

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
