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
public class Stock  implements java.io.Serializable {
    @Id
    private String id; // 股票编码
    private String name; // 股票名称
    private double outstanding = 0.0; //流通股本

    public Stock(String code) {
        this.id=code;
    }

}
