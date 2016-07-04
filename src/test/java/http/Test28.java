package http;

import app.service.GetSinaRawDailyThread;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by terry.wu on 2016/5/24 0024.
 */
public class Test28 {


    ObjectMapper objectMapper = new ObjectMapper();

    public void fetchData(String code) throws MalformedURLException {
        URL url = new URL("http://api.finance.ifeng.com/akdaily/?code=sz15991&type=last");
        URL url2 = new URL("http://api.finance.ifeng.com/akdaily/?code=sh518880&type=last");



    }

    public static void main(String[] args) throws IOException {


    }

}
