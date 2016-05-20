package app.repository;

import app.entity.FundFlowPie;
import app.entity.FundFlowPieDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by terry.wu on 2016/4/29 0029.
 */
public interface FundFlowPieDetailRepository extends JpaRepository<FundFlowPieDetail, Long>, JpaSpecificationExecutor {

   /* @Modifying
    @Query("update DailyEntity t set t.content = ? ,t.lastUpdateTime = now() where t.id = ? ")
    public void updateContent(String content, String id);*/
}
