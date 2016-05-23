package app.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by terry.wu on 2016/5/17 0017.
 */
@Data
@Entity
public class FundFlowPieMaster {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private String code;
    private Double totalBuyDang; //买入总单数
    private Double totalSellDang; //卖出总单数

    private Double totalBuyGu; // 买入总股数
    private Double sanBuyGu; //散户买入
    private Double zhongBuyGu; //中户买入
    private Double daBuyGu; //大户买入
    private Double jiBuyGu; //机构买入

    private Double totalSellGu; //卖出总股数
    private Double sanSellGu; //散户买入
    private Double zhongSellGu; // 买入总股数
    private Double daSellGu; //大户买入
    private Double jiSellGu; //机构买入

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

    private Double masterLv;
}
