package http;

import app.service.HuanShouLvService;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.IOException;
import java.util.Date;

public class TestHuangShou {

    public static void main(String[] args) throws IOException {
        TestHuangShou shou = new TestHuangShou();
        shou.fetchPie();
    }


    public void fetchPie(){
        String today = DateFormatUtils.format(new Date(), "yyyyMMdd");
        HuanShouLvService service = new HuanShouLvService();
       // service.filePath="D:/Terry/cloud/OneDrive/data/huanshoulv/";
        service.stockFile="D:/Terry/cloud/OneDrive/data/stockinfo/all.csv";
       // service.fetch(today) ;
    }

    public void fetchTodayPie(){
        String today = DateFormatUtils.format(new Date(), "yyyyMMdd");
        HuanShouLvService service = new HuanShouLvService();
       // service.filePath="D:/Terry/cloud/OneDrive/data/huanshoulv_raw/";
       // service.fetch(today) ;
    }
}