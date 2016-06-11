package http;

import app.bean.ApiDayResult;
import app.service.GetSinaRawDailyThread;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by terry.wu on 2016/5/24 0024.
 */
public class TestIfeng {




    public void fetchSinaData(String code){




    }

    public static void main(String[] args) throws IOException {

        String  syncFolder = "D:\\Terry\\cloud\\OneDrive\\history\\day\\raw_data\\";
        String  useFolder = "D:\\Terry\\cloud\\OneDrive\\history\\day\\";


        ObjectMapper jacksonObjectMapper = new ObjectMapper();
        ApiDayResult result = jacksonObjectMapper.readValue(new URL("http://api.finance.ifeng.com/akdaily/?code=sh600258&type=last"), ApiDayResult.class);
         String[] one = result.getRecord().get(0);
        System.out.println(one[0]);
    }

}
