package app.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
        Double  up_px;
    }
}
