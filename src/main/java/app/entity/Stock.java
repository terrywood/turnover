package app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by terry.wu on 2016/5/24 0024.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "stock")
public class Stock  implements java.io.Serializable {

    /*
    *  * code,代码
     name,名称
     industry,所属行业
     area,地区
     pe,市盈率
     outstanding,流通股本
     totals,总股本(万)
     totalAssets,总资产(万)
     liquidAssets,流动资产
     fixedAssets,固定资产
     reserved,公积金
     reservedPerShare,每股公积金
     esp,每股收益
     bvps,每股净资
     pb,市净率
     timeToMarket,上市日期
    * **/

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private String id; // 股票编码
    private String name; // 股票名称
    private String timeToMarket
            ;
    private String industry;
    private String area;
    private Double pe;
    private Double outstanding;

    //private Double outstandingAssets;

    private Double outstandingAssets;

    private Double totals;
    private Double totalAssets;
    private Double reserved;
    private Double reservedPerShare;
    private Double esp;
    private Double bvps;
    private Double pb;

    //涨停基因
    private Integer limitGene;
    //跑赢股票
    private Double prop;

    //平均涨停交易日
    private Integer avgNoOneSurged;

    public Stock(String code) {
        this.id=code;
    }

}
