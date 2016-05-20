package app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by terry.wu on 2016/5/17 0017.
 */
@Data
@Entity
public class FundFlowPieDetail {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private String code;
    private Integer totalBuyDang; //买入总单数
    private Integer totalSellDang; //卖出总单数

    private Integer totalBuyGu; // 买入总股数
    private Integer sanBuyGu; //散户买入
    private Integer zhongBuyGu; //中户买入
    private Integer daBuyGu; //大户买入
    private Integer jiBuyGu; //机构买入

    private Integer totalSellGu; //卖出总股数
    private Integer sanSellGu; //散户买入
    private Integer zhongSellGu; // 买入总股数
    private Integer daSellGu; //大户买入
    private Integer jiSellGu; //机构买入

    private Double totalBuyShou;//买入总均手
    private Double sanBuyShou; //
    private Double zhongBuyShou;
    private Double daBuyShou;
    private Double jiBuyShou;

    private Double totalSellShou;//卖出总均手
    private Double sanSellShou;
    private Double zhongSellShou;
    private Double daSellShou;
    private Double jiSellShou;

    private Double sanBuyDang ;  //散户买入笔数百分比
    private Double zhongBuyDang ;
    private Double daBuyDang ;
    private Double jiBuyDang ;

    private Double sanSellDang ;  //散户卖出笔数百分比
    private Double zhongSellDang ;
    private Double daSellDang ;
    private Double jiSellDang ;

    private Double totalBuyAvg; //总买入均价
    private Double sanBuyAvg;
    private Double zhongBuyAvg;
    private Double daBuyAvg;
    private Double jiBuyAvg;

    private Double totalSellAvg; //总卖出均价
    private Double sanSellAvg;
    private Double zhongSellAvg;
    private Double daSellAvg;
    private Double jiSellAvg;


}
