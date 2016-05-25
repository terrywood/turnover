package app.entity;

import lombok.Data;

/**
 * Created by terry.wu on 2016/5/24 0024.
 */
@Data
public class StockData {
    private String code; // 股票编码
    private String name; // 股票名称
    private String date; // 交易日期
    private double open = 0.0; // 开盘价
    private double high = 0.0; // 最高价
    private double low = 0.0; // 最低价
    private double close = 0.0; // 最后一次交易价格，相当于收盘价
    private double volume = 0.0;// 总交易手
    private double adj = 0.0; // 最后一次交易价格 (今天的收盘价当做加权价格)
}
