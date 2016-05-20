package app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by terry.wu on 2016/5/17 0017.
 */
@Data
@Entity
public class FundFlowPie {

    @JsonIgnore
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @JsonFormat(pattern = "yyyyMMdd", timezone="GMT+8")
    Date date;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name=" content", columnDefinition="mediumtext", nullable=true)
    String detail;

    @JsonProperty(value = "close_px")
    Double close;
    @JsonProperty(value = "hslddx")
    Double ddx;
    @JsonProperty(value = "hslddy")
    Double ddy;

    @JsonIgnore
    String code;

    Double jiPrice;
    Double daPrice;
    Double zhongPrice;
    Double shaPrice;

}
