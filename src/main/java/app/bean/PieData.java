package app.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Created by terry.wu on 2016/6/8 0008.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PieData {
    Double hslddx;
    Double hslddy;
    String pie;

    Real real;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private class Real {
        @JsonProperty(value = "up_px" )
        Double  up;
        @JsonProperty(value = "high_px" )
        Double  high;
        @JsonProperty(value = "low_px" )
        Double  low;
        @JsonProperty(value = "down_px" )
        Double  down;
        @JsonProperty(value = "preclose_px" )
        Double  preclose;
        @JsonProperty(value = "last_px" )
        Double  close;
        @JsonProperty(value = "trade_status" )
        String status;
    }
}
