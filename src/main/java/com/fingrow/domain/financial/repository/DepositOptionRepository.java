package com.fingrow.domain.financial.repository;

import com.fingrow.domain.financial.entity.DepositOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// 예금 옵션 Repository
@Repository
public interface DepositOptionRepository extends JpaRepository<DepositOption, Long> {

    // 특정 상품의 옵션들 조회
    List<DepositOption> findByDepositProductFinPrdtCd(String finPrdtCd);

    // 특정 기간의 옵션들을 금리순으로 조회
    @Query("SELECT o FROM DepositOption o " +
            "WHERE o.saveTrm = :term " +
            "ORDER BY o.intrRate2 DESC")
    List<DepositOption> findByTermOrderByRateDesc(@Param("term") Integer term);

    // 모든 저축 기간 조회 (중복 제거)
    @Query("SELECT DISTINCT o.saveTrm FROM DepositOption o " +
            "WHERE o.saveTrm IS NOT NULL " +
            "ORDER BY o.saveTrm")
    List<Integer> findAllDistinctTerms();

    // 최고 금리 TOP N 조회
    @Query("SELECT o FROM DepositOption o " +
            "WHERE o.intrRate2 IS NOT NULL " +
            "ORDER BY o.intrRate2 DESC")
    List<DepositOption> findTopByOrderByIntrRate2Desc();

    // 특정 금리 유형의 옵션들 조회
    List<DepositOption> findByIntrRateType(String intrRateType);

    // 기간별 평균 금리 조회
    @Query("SELECT o.saveTrm, AVG(o.intrRate2) as avgRate " +
            "FROM DepositOption o " +
            "WHERE o.intrRate2 IS NOT NULL " +
            "GROUP BY o.saveTrm " +
            "ORDER BY o.saveTrm")
    List<Object[]> findAverageRateByTerm();
}
