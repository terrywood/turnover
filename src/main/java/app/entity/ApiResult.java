package app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * Created by terry.wu on 2016/5/17 0017.
 */

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult {
    String status;
    List<FundFlowPie> data;
}
