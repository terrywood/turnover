package app.repository;

import app.entity.Stock;
import app.entity.StockDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by terry.wu on 2016/4/29 0029.
 */
public interface StockDayRepository extends JpaRepository<StockDay, Long>, JpaSpecificationExecutor {


}
