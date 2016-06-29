package app.service;

import app.bean.ApiResult;
import app.bean.PieData;
import app.entity.*;
import app.repository.*;
import app.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections.MapUtils;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by terry.wu on 2016/5/18 0018.
 */
@Transactional
@Service
public class HuanShouLvService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${save.huanshoulv.raw.path}")
    public String rawPath;
    private static String DOMAIN = "http://server.huanshoulv.com/";
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

    @Autowired
    StockRepository stockRepository;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
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
       */
    }

    public  void fetchStockExtend() throws IOException {
        String str =DOMAIN+"aimapp/stock/forecast/";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<Stock> list =this.stockRepository.findAll();
        List<Object[]> batchArgs = new ArrayList<>();
        for(Stock stock :list){
            HttpGet httpget = new HttpGet(str + stock.getId());
            httpget.setHeader("user-agent", "IM821OSmCn2wzlOW8y5FDawuhtPBrwCl");
            CloseableHttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            Map apiStockResult = objectMapper.readValue(entity.getContent(),Map.class);
            Map data = MapUtils.getMap(apiStockResult,"data");
            Map _stock = MapUtils.getMap(data,"stock");
            Map limitGene = MapUtils.getMap(_stock,"limitGene");
            if(limitGene!=null){
                Object[] values = new Object[]{limitGene.get("avgNoOneSurged"),limitGene.get("limitGene"),limitGene.get("prop"),stock.getId()};
                batchArgs.add(values);
            }
        }

        int[] result =  jdbcTemplate.batchUpdate("update stock set avg_no_one_surged =? ,limit_gene=?,prop=? where id=?", batchArgs);
        System.out.println("update result " + result.length);
    }

    /**
     * 当一只股票符合，买单均手>卖单均手，大户买入占比>大户卖出占比，机构买入占比>机构卖出占比；
     * select d from FundFlowPieDetail d where d.totalBuyShou> d.totalSellShou and  d.daBuyGu>daSellGu and d.jiBuyGu>d.jiSellGu
     * <p>
     * 再观察被动买单均手>=被动卖单均手，同时被动买单均手>=主动买单均手，说明主力看好后市，压盘吃货。
     * select s  from FundFlowPieSlave s , FundFlowPieMaster m where s.totalBuyShou>=s.totalSellShou and s.totalBuyShou>= m.totalBuyShou
     */
    public Page<FundFlowPie> findPressEat(int page, int size) {
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "id"));
        Page<FundFlowPie> list = fundFlowPieRepository.findPressEat(pageable);

      /*  Specification spec;
        fundFlowPieRepository.findAll(spec)*/;
        return list;
    }

    public Page<FundFlowPie> findNewPressEat(int page, int size) {
        Pageable pageable = new PageRequest(page, size, new Sort(Sort.Direction.ASC, "id"));
        Page<FundFlowPie> list = fundFlowPieRepository.findPressEat(pageable);
        Specification spec = new Specification() {
            @Override
            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
//select count(t) from FundFlowPie t  where t.fundFlowPieDetail.totalBuyShou>t.fundFlowPieDetail.totalSellShou and  t.fundFlowPieDetail.daBuyGu>t.fundFlowPieDetail.daSellGu and t.fundFlowPieDetail.jiBuyGu>t.fundFlowPieDetail.jiSellGu and t.fundFlowPieSlave.totalBuyShou>=t.fundFlowPieSlave.totalSellShou and  t.fundFlowPieSlave.totalBuyShou>=t.fundFlowPieMaster.totalBuyShou
                Predicate predicate = cb.notEqual(root.get("status").as(Integer.class), 1);

                query.where(predicate);
                return query.getRestriction();
            }
        };

        return list;
    }




    public List<FundFlowPie> findByCodeAndIdGreaterThan(String code, Long id, int size) {
        Pageable pageable = new PageRequest(0, size, new Sort(Sort.Direction.ASC, "id"));
        return fundFlowPieRepository.findByStockIdAndIdGreaterThan(code, id, pageable);

    }

/*    public void resetDb() {
        File dir = new File(filePath);
        File files[] = dir.listFiles();
        for (File day : files) {
            String name = day.getName();
            save2DB(name);
        }

    }*/


    public void jdbcTemplate() {
        Map map = jdbcTemplate.queryForMap("SELECT count(*) from Fund_Flow_Pie");
        System.out.println("----------------------------------------");
        System.out.println(map);

    }

    public FundFlowPie findOne(Long id) {
        return fundFlowPieRepository.findOne(id);
    }

    @Transactional
    public void save2DB(String day) throws ParseException {
        String[] fields = AppUtils.fields;
        Date date = sdf.parse(day);
        String file = rawPath +"pie/"+ day + "/";
        File dir = new File(file);
        if (dir.isDirectory()) {
            List<FundFlowPie> list = new ArrayList<>();
            for (File json : dir.listFiles()) {
                String code = FilenameUtils.getBaseName(json.getName());
                try {
                    ApiResult result = objectMapper.readValue(new FileInputStream(json), ApiResult.class);
                    if (result.getStatus().equals("200")) {
                        PieData pieData = result.getData();
                        String pie = pieData.getPie();
                        if (pie.length() > 8) {
                            FundFlowPie entity = new FundFlowPie();
                            Long id = Long.valueOf(day + code);
                            entity.setId(id);
                           // entity.setStockId(code);
                            //entity.setStock(new Stock(code));
                            entity.setDdy(pieData.getHslddy());
                            entity.setDdx(pieData.getHslddx());
                            entity.setDate(date);


                            BeanUtilsBean beanUtils = BeanUtilsBean.getInstance();

                            String data[] = pie.split(",");
                            entity.setJiPrice(Double.valueOf(data[121]));
                            entity.setDaPrice(Double.valueOf(data[122]));
                            entity.setZhongPrice(Double.valueOf(data[123]));
                            entity.setSanPrice(Double.valueOf(data[124]));
                            entity.setMasterLv(Double.valueOf(data[134]));

                            // System.out.println(entity);
                            FundFlowPieDetail detail = new FundFlowPieDetail();
                            FundFlowPieMaster master = new FundFlowPieMaster();
                            FundFlowPieSlave slave = new FundFlowPieSlave();
                            for (int i = 1; i <= 40; i++) {
                                try {
                                    beanUtils.setProperty(detail, fields[i], data[i]);
                                    beanUtils.setProperty(master, fields[i], data[i + 40]);
                                    beanUtils.setProperty(slave, fields[i], data[i + 80]);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }
                            master.setId(entity.getId());
                            entity.setFundFlowPieMaster(master);
                            detail.setId(entity.getId());
                            entity.setFundFlowPieDetail(detail);
                            slave.setId(entity.getId());
                            entity.setFundFlowPieSlave(slave);
                            list.add(entity);

                            //System.out.println(pieData.getReal());

                        }


                    }
                } catch (IOException e) {
                    log.info(json.getAbsolutePath());
                    //json.delete();
                    e.printStackTrace();
                }
            }

            fundFlowPieRepository.save(list);
        }
    }




   /* public void fetch(String day) {
        ExecutorService consumerService = Executors.newFixedThreadPool(1);
        try {
            String file = filePath + day + "/";
            Iterable<CSVRecord> records = this.tongUnionService.getEqu();
            //File dir = new File(file);
            for (CSVRecord record : records) {
                String ticker = record.get(0);
                File fileName = new File(file + ticker + ".json");
                if (!fileName.exists()) {
                    GetShouLvThread getThread = new GetShouLvThread(fileName, ticker);
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
    }*/

    public void fetchPieRaw() {
        String folder = rawPath + "pie/" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/";
        List<Stock> records  = stockRepository.findAll();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        for (Stock record : records) {
            String ticker = record.getId();
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
    }

    //涨停强度
    public void fetchSurgeRaw() {
        String folder = rawPath + "surge/" + DateFormatUtils.format(new Date(), "yyyyMMdd") + "/";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        for (int i = 1; i < 4; i++) {
            File file = new File(folder + i + ".json");
            if (file.exists()) {
                continue;
            }
            try {
                HttpGet httpget = new HttpGet(DOMAIN + "aimapp/stock/surgeLimit?sort_type=-1&sort_field_name=px_change_rate&page_count=20&page=" + i);
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
        String folder = rawPath + "boom/" + DateFormatUtils.format(new Date(), "yyyyMMdd") + ".json";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        File file = new File(folder);
        try {
            HttpGet httpget = new HttpGet(DOMAIN + "aimapp/stock/boomStock?page=1&page_count=20&sort_type=0&sort_field_name=addup_day_count");
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
