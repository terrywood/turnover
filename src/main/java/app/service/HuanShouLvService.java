package app.service;

import app.entity.*;
import app.repository.FundFlowPieDetailRepository;
import app.repository.FundFlowPieMasterRepository;
import app.repository.FundFlowPieRepository;
import app.repository.FundFlowPieSlaveRepository;
import app.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by terry.wu on 2016/5/18 0018.
 */
@Transactional
@Service
public class HuanShouLvService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    //BlockingQueue<String> consumerQueue = new LinkedBlockingQueue();

    @Value("${save.huanshoulv.data.path}")
    public String filePath;
    @Value("${save.huanshoulv.raw.path}")
    public String rawPath;

    @Value("${fetch.huanshoulv.stocks}")
    public String stockFile;
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
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TongUnionService tongUnionService;
   /* @Scheduled(cron = "0 0/15 9-20 * * MON-FRI")
    public void fetchData() {
       String today = DateFormatUtils.format(new Date(), "yyyyMMdd");
        fetch(today);
    }*/

    @PostConstruct
    public void postConstruct() {
   /*      String today = DateFormatUtils.format(new Date(), "yyyyMMdd");
         fetch(today);
         save2DB(today);
         splitDate();*/


    }
    /**
     * 当一只股票符合，买单均手>卖单均手，大户买入占比>大户卖出占比，机构买入占比>机构卖出占比；
     * select d from FundFlowPieDetail d where d.totalBuyShou> d.totalSellShou and  d.daBuyGu>daSellGu and d.jiBuyGu>d.jiSellGu
     *
     * 再观察被动买单均手>=被动卖单均手，同时被动买单均手>=主动买单均手，说明主力看好后市，压盘吃货。
     * select s  from FundFlowPieSlave s , FundFlowPieMaster m where s.totalBuyShou>=s.totalSellShou and s.totalBuyShou>= m.totalBuyShou
     * */
    public Page<FundFlowPie> findPressEat(int page, int size){
        Pageable pageable =  new PageRequest( page, size, new Sort(Sort.Direction.ASC,"id"));
        Page<FundFlowPie> list = fundFlowPieRepository.findPressEat(pageable);
        return  list;
    }

    public List<FundFlowPie> findByCodeAndIdGreaterThan(String code, Long id, int size){
        Pageable pageable =  new PageRequest( 0, size, new Sort(Sort.Direction.ASC,"id"));
        return fundFlowPieRepository.findByCodeAndIdGreaterThan(code,id,pageable);

    }

    public void resetDb() {
        File dir = new File(filePath);
        File files[] = dir.listFiles();
        for (File day : files) {
            String name = day.getName();
            save2DB(name);
        }

    }


    public void jdbcTemplate() {
        Map map = jdbcTemplate.queryForMap("SELECT count(*) from Fund_Flow_Pie");
        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");
        System.out.println("----------------------------------------");
        System.out.println(map);

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

    public void splitDate() {
        String[] fields = AppUtils.fields;
        List<FundFlowPie> entityList = fundFlowPieRepository.findByIsSplit(false);
        BeanUtilsBean beanUtils = BeanUtilsBean.getInstance();
        for (FundFlowPie entity : entityList) {
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


    @Transactional
    public void save2DB(String day) {

        String file = filePath + day + "/";
        File dir = new File(file);
        if (dir.isDirectory()) {
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
                            entity.setId(id);
                            entity.setCode(code);
                            entity.setSplit(false);
                            FundFlowPie obj = fundFlowPieRepository.findOne(id);
                            if (obj == null) {
                                fundFlowPieRepository.save(entity);
                            } else {
                                /*if (entity.getDdx()!=null){
                                    obj.setDdx(entity.getDdx());
                                }*/
                                if (entity.getDdy() != null) {
                                    // obj.setDdy(entity.getDdy());
                                    fundFlowPieRepository.updateContent(entity.getDdx(), entity.getDdy(), entity.getId());
                                }
                                //fundFlowPieRepository.save(obj);
                            }

                        /*    if(entity.getDdx()!=null && entity.getDdx()>0d){
                                System.out.println(entity.getId() +" " + entity.getDdx() +" " + entity.getDdy());
                            }*/

                            // list.add(entity);
                        }
                    }
                } catch (IOException e) {
                    log.info(json.getAbsolutePath());
                    //json.delete();
                    e.printStackTrace();
                }
            }

            // fundFlowPieRepository.save(list);
        }
    }

    public void fetch(String day) {
        ExecutorService consumerService = Executors.newFixedThreadPool(1);

        try {
            String file = filePath + day + "/";
            Iterable<CSVRecord> records = this.tongUnionService.getEqu();
            //File dir = new File(file);
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


        } catch (InterruptedException e) {

        }
    }
    private static String DOMAIN = "http://server.huanshoulv.com/";
    public void fetchPieRaw() {
        String folder = rawPath + "pie/" +DateFormatUtils.format(new Date(), "yyyyMMdd") + "/";
        Iterable<CSVRecord> records = this.tongUnionService.getEqu();
        int i = 1;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        for (CSVRecord record : records) {
            i++;
            String ticker = record.get(0);
            File file = new File(folder + ticker + ".json");
            if (file.exists()) {
                //file.lastModified();
                continue;
            }
            try {
                HttpGet httpget = new HttpGet(DOMAIN + "aimapp/stock/fundflowStock/" + ticker + "?min_time=1500&fundflow_min_time=1500");
                httpget.setHeader("user-agent", "IM821OSmCn2wzlOW8y5FDawuhtPBrwCl");
                CloseableHttpResponse response = httpClient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                if (content.indexOf("200") > 0) {
                    FileUtils.writeStringToFile(file, content, "UTF-8", false);
                }

            } catch (Exception e) {
            }
        }
        File dir = new File(folder);
        if (dir.listFiles().length == i) {
            log.info("----------------fetchPieRaw done");
        } else {
            log.info("----------------oops fetchPieRaw not done");
        }
    }

    //涨停强度
    public void fetchSurgeRaw() {
        String folder = rawPath + "surge/" +DateFormatUtils.format(new Date(), "yyyyMMdd") + "/";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        for (int i=1;i<4;i++) {
            File file = new File(folder + i + ".json");
            if (file.exists()) {
                continue;
            }
            try {
                HttpGet httpget = new HttpGet(DOMAIN + "aimapp/stock/surgeLimit?sort_type=-1&sort_field_name=px_change_rate&page_count=20&page=" + i );
                httpget.setHeader("user-agent", "IM821OSmCn2wzlOW8y5FDawuhtPBrwCl");
                CloseableHttpResponse response = httpClient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                if (content.indexOf("200") > 0) {
                    FileUtils.writeStringToFile(file, content, "UTF-8", false);
                }
            } catch (Exception e) {
            }
        }

    }
    //暴涨强度
    public void fetchBoomRaw() {
        String folder = rawPath + "boom/" +DateFormatUtils.format(new Date(), "yyyyMMdd") + ".json";
            CloseableHttpClient httpClient = HttpClients.createDefault();
            File file = new File(folder);
            try {
                HttpGet httpget = new HttpGet(DOMAIN + "aimapp/stock/boomStock?page=1&page_count=20&sort_type=0&sort_field_name=addup_day_count"  );
                httpget.setHeader("user-agent", "IM821OSmCn2wzlOW8y5FDawuhtPBrwCl");
                CloseableHttpResponse response = httpClient.execute(httpget);
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                if (content.indexOf("200") > 0) {
                    FileUtils.writeStringToFile(file, content, "UTF-8", false);
                }
            } catch (Exception e) {

            }
    }






}
