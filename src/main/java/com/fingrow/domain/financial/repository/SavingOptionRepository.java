package com.fingrow.domain.financial.repository;

import com.fingrow.domain.financial.entity.SavingOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// 적금 옵션 Repository
@Repository
public interface SavingOptionRepository extends JpaRepository<SavingOption, Long> {

    // 특정 상품의 옵션들 조회
    List<SavingOption> findBySavingProductFinPrdtCd(String finPrdtCd);

    // 특정 기간의 옵션들을 금리순으로 조회
    @Query("SELECT o FROM SavingOption o " +
            "WHERE o.saveTrm = :term " +
            "ORDER BY o.intrRate2 DESC")
    List<SavingOption> findByTermOrderByRateDesc(@Param("term") Integer term);

    // 모든 저축 기간 조회 (중복 제거)
    @Query("SELECT DISTINCT o.saveTrm FROM SavingOption o " +
            "WHERE o.saveTrm IS NOT NULL " +
            "ORDER BY o.saveTrm")
    List<Integer> findAllDistinctTerms();

    // 적립 유형별 조회
    List<SavingOption> findByRsrvType(String rsrvType);

    // 적립 유형명으로 검색
    List<SavingOption> findByRsrvTypeNmContaining(String rsrvTypeName);

    // 최고 금리 TOP N 조회
    @Query("SELECT o FROM SavingOption o " +
            "WHERE o.intrRate2 IS NOT NULL " +
            "ORDER BY o.intrRate2 DESC")
    List<SavingOption> findTopByOrderByIntrRate2Desc();

    // 정액적립식만 조회
    @Query("SELECT o FROM SavingOption o WHERE o.rsrvType = 'S'")
    List<SavingOption> findFixedAmountOptions();

    // 자유적립식만 조회
    @Query("SELECT o FROM SavingOption o WHERE o.rsrvType = 'F'")
    List<SavingOption> findFreeAmountOptions();

    // 기간별 평균 금리 조회
    @Query("SELECT o.saveTrm, AVG(o.intrRate2) as avgRate " +
            "FROM SavingOption o " +
            "WHERE o.intrRate2 IS NOT NULL " +
            "GROUP BY o.saveTrm " +
            "ORDER BY o.saveTrm")
    List<Object[]> findAverageRateByTerm();

    // 적립유형별 평균 금리 조회
    @Query("SELECT o.rsrvType, AVG(o.intrRate2) as avgRate " +
            "FROM SavingOption o " +
            "WHERE o.intrRate2 IS NOT NULL " +
            "GROUP BY o.rsrvType")
    List<Object[]> findAverageRateByReserveType();
}
