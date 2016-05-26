package app.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;

/**
 * Created by terry.wu on 2016/5/25 0025.
 */
public class GetSinaDailyThread implements Runnable {

    private String code;
    private String folder;

    public GetSinaDailyThread(String code, String folder) {
        this.code = code;
        this.folder =folder;
    }

    @Override
    public void run() {
        String target = folder + "data/" + code + ".csv";
        String source = folder + "raw_data/" + code + ".csv";
        File f1 = new File(target);
        File sourceFile = new File(source);
        if ( sourceFile.exists()) {
            try {
                Reader in2 = new FileReader(sourceFile);
                Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in2);

                FileWriter fileWriter = new FileWriter(target);
                CSVFormat csvFileFormat = CSVFormat.DEFAULT;
                CSVPrinter csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
                //string   str = string.Format(%.2f ,4545.48);
                //date,open,high,close,low,volume,amount,factor
                csvFilePrinter.printRecord("date","open","high","close","low","volume","amount","factor");
                for (CSVRecord record : records) {
                    String date = record.get("date");
                    Double open = Double.valueOf(record.get("open"));
                    Double high =Double.valueOf( record.get("high"));
                    Double close =Double.valueOf( record.get("close"));
                    Double low = Double.valueOf(record.get("low"));
                    String volume = record.get("volume");
                    String amount = record.get("amount");
                    String temp = record.get("factor");
                    Double factor = Double.valueOf(temp);

                    csvFilePrinter.printRecord(date,String.format("%.2f",open/factor),
                            String.format("%.2f",high/factor),
                            String.format("%.2f",close/factor),
                            String.format("%.2f",low/factor),volume,amount,temp
                    );
                }
                fileWriter.flush();
                fileWriter.close();
                csvFilePrinter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
