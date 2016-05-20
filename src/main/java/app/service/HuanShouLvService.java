package app.service;

import app.entity.*;
import app.repository.FundFlowPieDetailRepository;
import app.repository.FundFlowPieMasterRepository;
import app.repository.FundFlowPieRepository;
import app.repository.FundFlowPieSlaveRepository;
import app.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.BeanUtilsBean2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
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
    FundFlowPieDetailRepository fundFlowPieDetailRepository;
    @Autowired
    FundFlowPieSlaveRepository fundFlowPieSlaveRepository;
    @Autowired
    FundFlowPieMasterRepository fundFlowPieMasterRepository;
    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void postConstruct() {
       /* fetch();
        save2DB();*/
        //splitDate("");
    }


    public FundFlowPie findOne(Long id){
        return  fundFlowPieRepository.findOne(id);
    }
   /**
    * 当一只股票符合，买单均手>卖单均手，
    大户买入占比>大户卖出占比，
    机构买入占比>机构卖出占比；
    再观察被动买单均手>=被动卖单均手，
    同时被动买单均手>=主动买单均手，
    说明主力看好后市，
    压盘吃货。

    * */
    public void press(){

    }

    public void splitDate(String detail){
        String[] fields = AppUtils.fields;
        Pageable page = new PageRequest(0,20, Sort.Direction.DESC,"id");
        Page<FundFlowPie>  pageList  = fundFlowPieRepository.findAll(page);
        List<FundFlowPieDetail>  detailList  = new ArrayList<>();
        List<FundFlowPieMaster>  masterList  = new ArrayList<>();
        List<FundFlowPieSlave>  slaveList  = new ArrayList<>();

        BeanUtilsBean beanUtils =BeanUtilsBean.getInstance();
         for(FundFlowPie entity : pageList.getContent()){
             String data[]  =entity.getDetail().split(",");


             try {
                 beanUtils.setProperty(entity,"jiPrice",data[121]);
                 beanUtils.setProperty(entity,"daPrice",data[122]);
                 beanUtils.setProperty(entity,"zhongPrice",data[123]);
                 beanUtils.setProperty(entity,"shaPrice",data[124]);
                 this.fundFlowPieRepository.save(entity);
             } catch (IllegalAccessException e) {
                 e.printStackTrace();
             } catch (InvocationTargetException e) {
                 e.printStackTrace();
             }

             FundFlowPieDetail  obj =  new  FundFlowPieDetail() ;
             FundFlowPieMaster master =  new  FundFlowPieMaster() ;
             FundFlowPieSlave slave =  new  FundFlowPieSlave() ;


             for(int i =1;i<=40;i++){
                 try {
                     beanUtils.setProperty(obj, fields[i],data[i]);
                     beanUtils.setProperty(master, fields[i],data[i+40]);
                     beanUtils.setProperty(slave, fields[i],data[i+80]);
                 } catch (IllegalAccessException e) {
                     e.printStackTrace();
                 } catch (InvocationTargetException e) {
                     e.printStackTrace();
                 }
             }


             master.setId(entity.getId());
             master.setCode(entity.getCode());
             masterList.add(master);

             obj.setCode(entity.getCode());
             obj.setId(entity.getId());
             detailList.add(obj);

             slave.setCode(entity.getCode());
             slave.setId(entity.getId());

             slaveList.add(slave);

         }
        fundFlowPieDetailRepository.save(detailList);
        fundFlowPieMasterRepository.save(masterList);
        fundFlowPieSlaveRepository.save(slaveList);

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
