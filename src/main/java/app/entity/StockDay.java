package app.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by terry.wu on 2016/5/24 0024.
 */
@Data
@Entity
public class StockDay {
    /*  @EmbeddedId
      private DayPK id;*/
    /**
     * date：日期
     * •open：开盘价
     * •high：最高价
     * •close：收盘价
     * •low：最低价
     * •volume：成交量
     * •price_change：价格变动
     * •p_change：涨跌幅
     * •ma5：5日均价
     * •ma10：10日均价
     * •ma20:20日均价
     * •v_ma5:5日均量
     * •v_ma10:10日均量
     * •v_ma20:20日均量
     * •turnover:换手率[注：指数无此项]
     */
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    private String code;
    private Date date;
    private double open = 0.0; // 开盘价
    private double high = 0.0; // 最高价
    private double low = 0.0; // 最低价
    private double close = 0.0; // 最后一次交易价格，相当于收盘价
    private double volume = 0.0;// 总交易手

    @Column(name = "price_change", nullable = false)
    private double priceChange;//价格变动
    @Column(name = "p_change", nullable = false)
    private double pChange;//涨跌幅

    @Column(name = "ma5", nullable = false)
    private double ma5;//ma5
    @Column(name = "ma10", nullable = false)
    private double ma10;//ma10
    @Column(name = "ma20", nullable = false)
    private double ma20;//ma20

    @Column(name = "v_ma5", nullable = false)
    private double vma5;//:5日均量
    @Column(name = "v_ma10", nullable = false)
    private double vma10;//10日均量
    @Column(name = "v_ma20", nullable = false)
    private double vma20;//20日均量


    @Column(name = "turnover", nullable = false)
    private double turnover;//换手率
}
