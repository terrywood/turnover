package app.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by terry.wu on 2016/5/24 0024.
 */
@Embeddable
@Data
public class DayPK implements Serializable {
    @Column(length = 10)
    private String day;
    @Column(length = 10)
    private String code;


    public DayPK(String s, String s1) {
        this.day = s;
        this.code = s1;
    }
}
