package app.entity;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

/**
 * Created by terry.wu on 2016/5/24 0024.
 */
@Data
@Entity
public class DayStock {
    @EmbeddedId
    private DayPK id;

    private double open = 0.0; // 开盘价
    private double high = 0.0; // 最高价
    private double low = 0.0; // 最低价
    private double close = 0.0; // 最后一次交易价格，相当于收盘价
    private double volume = 0.0;// 总交易手
}
