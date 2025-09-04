package com.fingrow.domain.financial.repository;

import com.fingrow.domain.financial.entity.SavingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingProductRepository extends JpaRepository<SavingProduct, Long> {

    // 상품코드로 조회
    Optional<SavingProduct> findByFinPrdtCd(String finPrdtCd);

    // 은행명으로 검색
    List<SavingProduct> findByKorCoNmContaining(String bankName);

    // 상품명으로 검색
    List<SavingProduct> findByFinPrdtNmContaining(String productName);

    // 특정 기간의 상품들을 최고금리 순으로 조회
    @Query("SELECT DISTINCT s FROM SavingProduct s " +
            "JOIN s.options o " +
            "WHERE o.saveTrm = :term " +
            "ORDER BY o.intrRate2 DESC")
    List<SavingProduct> findByTermOrderByBestRateDesc(@Param("term") Integer term);

    // 최소 금리 이상인 상품들 조회
    @Query("SELECT DISTINCT s FROM SavingProduct s " +
            "JOIN s.options o " +
            "WHERE o.intrRate2 >= :minRate")
    List<SavingProduct> findByMinInterestRate(@Param("minRate") Double minRate);

    // 적립 한도 내 상품들 조회
    @Query("SELECT s FROM SavingProduct s " +
            "WHERE s.maxLimit >= :amount OR s.maxLimit IS NULL")
    List<SavingProduct> findByAmountLimit(@Param("amount") Long amount);

    // 가입제한 없는 상품들 조회
    List<SavingProduct> findByJoinDeny(Integer joinDeny);

    // 인기 은행 상품들 조회
    @Query("SELECT s FROM SavingProduct s " +
            "WHERE s.korCoNm IN ('국민은행', '신한은행', '하나은행', '우리은행', 'KB국민은행', '농협은행')")
    List<SavingProduct> findMajorBankProducts();

    // 자유적립식 상품들 조회
    @Query("SELECT DISTINCT s FROM SavingProduct s " +
            "JOIN s.options o " +
            "WHERE o.rsrvType = 'F'")
    List<SavingProduct> findFreeAmountProducts();
}
