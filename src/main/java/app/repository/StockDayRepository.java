package app.repository;

import app.entity.FundFlowPie;
import app.entity.Stock;
import app.entity.StockDay;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by terry.wu on 2016/4/29 0029.
 */
public interface StockDayRepository extends JpaRepository<StockDay, Long>, JpaSpecificationExecutor {

    @Query("select t from StockDay t where t.stock.id=?1 and t.id>?2 ")
    List<StockDay> findByStockIdAndIdGreaterThan(String code, Long id, Pageable pageable);

}
