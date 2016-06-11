package app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by terry.wu on 2016/5/17 0017.
 */
@Data
@Entity
public class FundFlowPie implements java.io.Serializable {

    public FundFlowPie() {

    }

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
    Date date;


    @Column(name = "ddx", unique = false, nullable = true)
    Double ddx;

    @Column(name = "ddy", unique = false, nullable = true)
    Double ddy;


    Double jiPrice;
    Double daPrice;
    Double zhongPrice;
    Double sanPrice;
    Double masterLv;


/*
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="content", columnDefinition="mediumtext", nullable=true)
    String detail;
*/


    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id",referencedColumnName = "id")
    private Stock stock;

    // private String stockId;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @PrimaryKeyJoinColumn
    private FundFlowPieDetail fundFlowPieDetail;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @PrimaryKeyJoinColumn
    private FundFlowPieMaster fundFlowPieMaster;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @PrimaryKeyJoinColumn
    private FundFlowPieSlave fundFlowPieSlave;


}
