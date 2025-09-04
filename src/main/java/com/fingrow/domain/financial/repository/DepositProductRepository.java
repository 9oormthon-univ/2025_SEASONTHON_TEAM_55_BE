package com.fingrow.domain.financial.repository;

import com.fingrow.domain.financial.entity.DepositProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// 예금 상품 Repository
@Repository
public interface DepositProductRepository extends JpaRepository<DepositProduct, Long> {

    // 상품코드로 조회
    Optional<DepositProduct> findByFinPrdtCd(String finPrdtCd);

    // 은행명으로 검색
    List<DepositProduct> findByKorCoNmContaining(String bankName);

    // 상품명으로 검색
    List<DepositProduct> findByFinPrdtNmContaining(String productName);

    // 특정 기간의 상품들을 최고금리 순으로 조회
    @Query("SELECT DISTINCT d FROM DepositProduct d " +
            "JOIN d.options o " +
            "WHERE o.saveTrm = :term " +
            "ORDER BY o.intrRate2 DESC")
    List<DepositProduct> findByTermOrderByBestRateDesc(@Param("term") Integer term);

    // 최소 금리 이상인 상품들 조회
    @Query("SELECT DISTINCT d FROM DepositProduct d " +
            "JOIN d.options o " +
            "WHERE o.intrRate2 >= :minRate")
    List<DepositProduct> findByMinInterestRate(@Param("minRate") Double minRate);

    // 예치 금액 한도 내 상품들 조회
    @Query("SELECT d FROM DepositProduct d " +
            "WHERE d.maxLimit >= :amount OR d.maxLimit IS NULL")
    List<DepositProduct> findByAmountLimit(@Param("amount") Long amount);

    // 가입제한 없는 상품들 조회
    List<DepositProduct> findByJoinDeny(Integer joinDeny);

    // 최신 공시월 기준 상품들 조회
    List<DepositProduct> findByDclsMonthOrderByDclsMonthDesc(String dclsMonth);

    // 인기 은행 상품들 조회 (메이저 은행들)
    @Query("SELECT d FROM DepositProduct d " +
            "WHERE d.korCoNm IN ('국민은행', '신한은행', '하나은행', '우리은행', 'KB국민은행', '농협은행')")
    List<DepositProduct> findMajorBankProducts();
}
