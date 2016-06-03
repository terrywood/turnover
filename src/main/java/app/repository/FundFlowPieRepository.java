package app.repository;

import app.entity.FundFlowPie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by terry.wu on 2016/4/29 0029.
 */
public interface FundFlowPieRepository extends JpaRepository<FundFlowPie, Long>, JpaSpecificationExecutor {

    @Modifying
    @Transactional
    @Query("update FundFlowPie t set t.ddx = ? , t.ddy = ? where t.id = ?")
    public void updateContent(Double ddx,Double ddy, Long id);

    public Page<FundFlowPie> findByDate(java.util.Date date, Pageable page);

    public List<FundFlowPie> findByIsSplit(boolean isSplit);

    /**
     * 当一只股票符合，买单均手>卖单均手，大户买入占比>大户卖出占比，机构买入占比>机构卖出占比；
     * select d from FundFlowPieDetail d where d.totalBuyShou> d.totalSellShou and  d.daBuyGu>daSellGu and d.jiBuyGu>d.jiSellGu
     *
     * 再观察被动买单均手>=被动卖单均手，同时被动买单均手>=主动买单均手，说明主力看好后市，压盘吃货。
     * select s  from FundFlowPieSlave s , FundFlowPieMaster m where s.totalBuyShou>=s.totalSellShou and s.totalBuyShou>= m.totalBuyShou
     * */
 /*

   @Query("select t from FundFlowPie t  join fetch t.fundFlowPieDetail join fetch t.fundFlowPieMaster join fetch t.fundFlowPieSlave where t.fundFlowPieDetail.totalBuyShou>t.fundFlowPieDetail.totalSellShou and  t.fundFlowPieDetail.daBuyGu>t.fundFlowPieDetail.daSellGu and t.fundFlowPieDetail.jiBuyGu>t.fundFlowPieDetail.jiSellGu" +
            " and t.fundFlowPieSlave.totalBuyShou>=t.fundFlowPieSlave.totalSellShou and  t.fundFlowPieSlave.totalBuyShou>=t.fundFlowPieMaster.totalBuyShou" )
*/
    @Query(value ="select t from FundFlowPie t  join fetch t.fundFlowPieDetail join fetch t.fundFlowPieMaster join fetch t.fundFlowPieSlave where t.fundFlowPieDetail.totalBuyShou>t.fundFlowPieDetail.totalSellShou and  t.fundFlowPieDetail.daBuyGu>t.fundFlowPieDetail.daSellGu and t.fundFlowPieDetail.jiBuyGu>t.fundFlowPieDetail.jiSellGu" +
            " and t.fundFlowPieSlave.totalBuyShou>=t.fundFlowPieSlave.totalSellShou and  t.fundFlowPieSlave.totalBuyShou>=t.fundFlowPieMaster.totalBuyShou" ,
     countQuery = "select count(t) from FundFlowPie t  where t.fundFlowPieDetail.totalBuyShou>t.fundFlowPieDetail.totalSellShou and  t.fundFlowPieDetail.daBuyGu>t.fundFlowPieDetail.daSellGu and t.fundFlowPieDetail.jiBuyGu>t.fundFlowPieDetail.jiSellGu and t.fundFlowPieSlave.totalBuyShou>=t.fundFlowPieSlave.totalSellShou and  t.fundFlowPieSlave.totalBuyShou>=t.fundFlowPieMaster.totalBuyShou")
    Page<FundFlowPie> findPressEat( Pageable pageable);

    Page<FundFlowPie> findAll(Pageable pageable);



    List<FundFlowPie> findByCodeAndIdGreaterThan(String code,Long id, Pageable pageable);
}
