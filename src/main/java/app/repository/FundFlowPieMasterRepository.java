package app.repository;

import app.entity.FundFlowPieDetail;
import app.entity.FundFlowPieMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by terry.wu on 2016/4/29 0029.
 */
public interface FundFlowPieMasterRepository extends JpaRepository<FundFlowPieMaster, Long>, JpaSpecificationExecutor {

   /* @Modifying
    @Query("update DailyEntity t set t.content = ? ,t.lastUpdateTime = now() where t.id = ? ")
    public void updateContent(String content, String id);*/
}
