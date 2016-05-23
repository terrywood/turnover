package app.repository;

import app.entity.FundFlowPie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Created by terry.wu on 2016/4/29 0029.
 */
public interface FundFlowPieRepository extends JpaRepository<FundFlowPie, Long>, JpaSpecificationExecutor {

   /* @Modifying
    @Query("update DailyEntity t set t.content = ? ,t.lastUpdateTime = now() where t.id = ? ")
    public void updateContent(String content, String id);*/

    public Page<FundFlowPie> findByDate(java.util.Date date, Pageable page);

    public List<FundFlowPie> findByIsSplit(boolean isSplit);

}
