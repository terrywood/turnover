package app.repository;

import app.entity.FundFlowPie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by terry.wu on 2016/4/29 0029.
 */
public interface FundFlowPieRepository extends JpaRepository<FundFlowPie, Long>, JpaSpecificationExecutor {

    @Modifying
    @Transactional
    @Query("update FundFlowPie t set t.ddx = ?0 , t.ddy = ?1 where t.id = ?2")
    public void updateContent(Double ddx,Double ddy, Long id);

    public Page<FundFlowPie> findByDate(java.util.Date date, Pageable page);

    public List<FundFlowPie> findByIsSplit(boolean isSplit);

}
