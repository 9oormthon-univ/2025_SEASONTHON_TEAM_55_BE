package com.fingrow.domain.financial.service;

import com.fingrow.domain.financial.dto.*;
import com.fingrow.domain.financial.entity.DepositOption;
import com.fingrow.domain.financial.entity.DepositProduct;
import com.fingrow.domain.financial.entity.SavingOption;
import com.fingrow.domain.financial.entity.SavingProduct;
import com.fingrow.domain.financial.repository.DepositOptionRepository;
import com.fingrow.domain.financial.repository.DepositProductRepository;
import com.fingrow.domain.financial.repository.SavingOptionRepository;
import com.fingrow.domain.financial.repository.SavingProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FinancialProductService {

    private final DepositProductRepository depositProductRepository;
    private final DepositOptionRepository depositOptionRepository;
    private final SavingProductRepository savingProductRepository;
    private final SavingOptionRepository savingOptionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${finlife.api.key:YOUR_API_KEY}")
    private String apiKey;

    private static final String BASE_URL = "https://finlife.fss.or.kr/finlifeapi";

    // =========================== 데이터 동기화 ===========================

    /**
     * 예금 상품 데이터 동기화
     */
    public void syncDepositProducts() {
        try {
            String url = BASE_URL + "/depositProductsSearch.json?auth=" + apiKey + "&topFinGrpNo=020000&pageNo=1";
            log.info("예금 상품 API 호출: {}", url);

            DepositApiResponse response = restTemplate.getForObject(url, DepositApiResponse.class);

            if (response == null || response.getResult() == null) {
                throw new RuntimeException("API 응답이 null입니다.");
            }

            // 기존 데이터 삭제
            depositOptionRepository.deleteAll();
            depositProductRepository.deleteAll();
            log.info("기존 예금 데이터 삭제 완료");

            // 상품 정보 저장
            Map<String, DepositProduct> productMap = new HashMap<>();

            if (response.getResult().getBaseList() != null) {
                for (DepositProductDto dto : response.getResult().getBaseList()) {
                    DepositProduct product = DepositProduct.builder()
                            .finPrdtCd(dto.getFinPrdtCd())
                            .korCoNm(dto.getKorCoNm())
                            .finPrdtNm(dto.getFinPrdtNm())
                            .joinWay(dto.getJoinWay())
                            .mtrtInt(dto.getMtrtInt())
                            .spclCnd(dto.getSpclCnd())
                            .joinDeny(dto.getJoinDeny())
                            .joinMember(dto.getJoinMember())
                            .etcNote(dto.getEtcNote())
                            .maxLimit(dto.getMaxLimit())
                            .dclsMonth(dto.getDclsMonth())
                            .dclsStrtDay(dto.getDclsStrtDay())
                            .dclsEndDay(dto.getDclsEndDay())
                            .finCoNo(dto.getFinCoNo())
                            .build();

                    DepositProduct savedProduct = depositProductRepository.save(product);
                    productMap.put(dto.getFinPrdtCd(), savedProduct);
                }
            }

            // 옵션 정보 저장
            if (response.getResult().getOptionList() != null) {
                for (DepositOptionDto dto : response.getResult().getOptionList()) {
                    DepositProduct product = productMap.get(dto.getFinPrdtCd());
                    if (product != null) {
                        DepositOption option = DepositOption.builder()
                                .depositProduct(product)
                                .intrRateType(dto.getIntrRateType())
                                .intrRateTypeNm(dto.getIntrRateTypeNm())
                                .intrRate(dto.getIntrRate())
                                .intrRate2(dto.getIntrRate2())
                                .saveTrm(dto.getSaveTrm())
                                .build();

                        depositOptionRepository.save(option);
                    }
                }
            }

            log.info("예금 상품 데이터 동기화 완료: {} 개 상품", productMap.size());

        } catch (Exception e) {
            log.error("예금 상품 데이터 동기화 실패", e);
            throw new RuntimeException("예금 상품 데이터 동기화 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 적금 상품 데이터 동기화
     */
    public void syncSavingProducts() {
        try {
            String url = BASE_URL + "/savingProductsSearch.json?auth=" + apiKey + "&topFinGrpNo=020000&pageNo=1";
            log.info("적금 상품 API 호출: {}", url);

            SavingApiResponse response = restTemplate.getForObject(url, SavingApiResponse.class);

            if (response == null || response.getResult() == null) {
                throw new RuntimeException("API 응답이 null입니다.");
            }

            // 기존 데이터 삭제
            savingOptionRepository.deleteAll();
            savingProductRepository.deleteAll();
            log.info("기존 적금 데이터 삭제 완료");

            // 상품 정보 저장
            Map<String, SavingProduct> productMap = new HashMap<>();

            if (response.getResult().getBaseList() != null) {
                for (SavingProductDto dto : response.getResult().getBaseList()) {
                    SavingProduct product = SavingProduct.builder()
                            .finPrdtCd(dto.getFinPrdtCd())
                            .korCoNm(dto.getKorCoNm())
                            .finPrdtNm(dto.getFinPrdtNm())
                            .joinWay(dto.getJoinWay())
                            .mtrtInt(dto.getMtrtInt())
                            .spclCnd(dto.getSpclCnd())
                            .joinDeny(dto.getJoinDeny())
                            .joinMember(dto.getJoinMember())
                            .etcNote(dto.getEtcNote())
                            .maxLimit(dto.getMaxLimit())
                            .dclsMonth(dto.getDclsMonth())
                            .dclsStrtDay(dto.getDclsStrtDay())
                            .dclsEndDay(dto.getDclsEndDay())
                            .finCoNo(dto.getFinCoNo())
                            .build();

                    SavingProduct savedProduct = savingProductRepository.save(product);
                    productMap.put(dto.getFinPrdtCd(), savedProduct);
                }
            }

            // 옵션 정보 저장
            if (response.getResult().getOptionList() != null) {
                for (SavingOptionDto dto : response.getResult().getOptionList()) {
                    SavingProduct product = productMap.get(dto.getFinPrdtCd());
                    if (product != null) {
                        SavingOption option = SavingOption.builder()
                                .savingProduct(product)
                                .intrRateType(dto.getIntrRateType())
                                .intrRateTypeNm(dto.getIntrRateTypeNm())
                                .rsrvType(dto.getRsrvType())
                                .rsrvTypeNm(dto.getRsrvTypeNm())
                                .intrRate(dto.getIntrRate())
                                .intrRate2(dto.getIntrRate2())
                                .saveTrm(dto.getSaveTrm())
                                .build();

                        savingOptionRepository.save(option);
                    }
                }
            }

            log.info("적금 상품 데이터 동기화 완료: {} 개 상품", productMap.size());

        } catch (Exception e) {
            log.error("적금 상품 데이터 동기화 실패", e);
            throw new RuntimeException("적금 상품 데이터 동기화 실패: " + e.getMessage(), e);
        }
    }

    // =========================== 상품 추천 ===========================

    /**
     * 목표 기반 상품 추천
     */
    public RecommendationResponse recommendProducts(RecommendationRequest request) {
        log.info("상품 추천 요청: 목표금액={}, 목표기간={}개월, 월예산={}",
                request.getTargetAmount(), request.getTargetMonths(), request.getMonthlyBudget());

        List<ProductRecommendation> recommendations = new ArrayList<>();

        // 예금 상품 추천 (목표금액을 한번에 예치)
        List<DepositProduct> topDeposits = getTopDepositProducts(request.getTargetMonths(), 3);
        for (DepositProduct deposit : topDeposits) {
            ProductRecommendation rec = createDepositRecommendation(deposit, request);
            if (rec != null) {
                recommendations.add(rec);
            }
        }

        // 적금 상품 추천 (월납입으로 목표금액 달성)
        List<SavingProduct> topSavings = getTopSavingProducts(request.getTargetMonths(), 3);
        for (SavingProduct saving : topSavings) {
            ProductRecommendation rec = createSavingRecommendation(saving, request);
            if (rec != null) {
                recommendations.add(rec);
            }
        }

        // 최적 조합 계산
        OptimalCombination combination = calculateOptimalCombination(request);

        return RecommendationResponse.builder()
                .targetAmount(request.getTargetAmount())
                .targetMonths(request.getTargetMonths())
                .monthlyBudget(request.getMonthlyBudget())
                .recommendations(recommendations)
                .optimalCombination(combination)
                .totalProducts(recommendations.size())
                .build();
    }

    /**
     * 최고 예금 상품 조회
     */
    private List<DepositProduct> getTopDepositProducts(Integer term, int limit) {
        List<DepositProduct> products = depositProductRepository.findByTermOrderByBestRateDesc(term);
        return products.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 최고 적금 상품 조회
     */
    private List<SavingProduct> getTopSavingProducts(Integer term, int limit) {
        List<SavingProduct> products = savingProductRepository.findByTermOrderByBestRateDesc(term);
        return products.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 예금 상품 추천 생성
     * 🔧 수정: Long → double 변환 이슈 해결
     */
    private ProductRecommendation createDepositRecommendation(DepositProduct deposit, RecommendationRequest request) {
        DepositOption bestOption = deposit.getOptions().stream()
                .filter(o -> o.getSaveTrm().equals(request.getTargetMonths()))
                .max(Comparator.comparing(DepositOption::getBestRate))
                .orElse(null);

        if (bestOption == null) {
            return null;
        }

        double expectedAmount = calculateDepositReturn(
                (double) request.getTargetAmount(),  // 🔧 수정: Long.doubleValue() → (double) 캐스팅
                bestOption.getBestRate(),
                request.getTargetMonths()
        );

        return ProductRecommendation.builder()
                .productType("예금")
                .bankName(deposit.getKorCoNm())
                .productName(deposit.getFinPrdtNm())
                .interestRate(bestOption.getBestRate())
                .term(bestOption.getSaveTrm())
                .expectedReturn(expectedAmount - (double) request.getTargetAmount())  // 🔧 수정: Long → double 캐스팅
                .expectedTotalAmount(expectedAmount)
                .specialCondition(deposit.getSpclCnd())
                .joinWay(deposit.getJoinWay())
                .maxLimit(deposit.getMaxLimit())
                .initialAmount((double) request.getTargetAmount())  // 🔧 수정: Long → double 캐스팅
                .monthlyAmount(0.0)
                .riskLevel("매우낮음")
                .build();
    }

    /**
     * 적금 상품 추천 생성
     * 🔧 수정: Long → double 변환 이슈 해결
     */
    private ProductRecommendation createSavingRecommendation(SavingProduct saving, RecommendationRequest request) {
        SavingOption bestOption = saving.getOptions().stream()
                .filter(o -> o.getSaveTrm().equals(request.getTargetMonths()))
                .max(Comparator.comparing(SavingOption::getBestRate))
                .orElse(null);

        if (bestOption == null) {
            return null;
        }

        double monthlyAmount = (double) request.getTargetAmount() / request.getTargetMonths();  // 🔧 수정: Long → double 캐스팅
        double expectedAmount = bestOption.calculateExpectedReturn(monthlyAmount, request.getTargetMonths());

        return ProductRecommendation.builder()
                .productType("적금")
                .bankName(saving.getKorCoNm())
                .productName(saving.getFinPrdtNm())
                .interestRate(bestOption.getBestRate())
                .term(bestOption.getSaveTrm())
                .expectedReturn(expectedAmount - (double) request.getTargetAmount())  // 🔧 수정: Long → double 캐스팅
                .expectedTotalAmount(expectedAmount)
                .specialCondition(saving.getSpclCnd())
                .joinWay(saving.getJoinWay())
                .maxLimit(saving.getMaxLimit())
                .initialAmount(0.0)
                .monthlyAmount(monthlyAmount)
                .reserveType(bestOption.getRsrvTypeNm())
                .riskLevel("매우낮음")
                .build();
    }

    /**
     * 예금 수익률 계산 (단리)
     * 🔧 수정: 파라미터 타입 Double → double로 변경
     */
    private double calculateDepositReturn(double principal, Double rate, Integer months) {
        if (rate == null || rate <= 0) return principal;
        return principal * (1 + (rate / 100.0) * (months / 12.0));
    }

    /**
     * 최적 조합 계산
     * 🔧 수정: Long → double 변환 이슈 해결
     */
    private OptimalCombination calculateOptimalCombination(RecommendationRequest request) {
        // 50:50 예적금 조합 예시
        long depositAmount = request.getTargetAmount() / 2;
        long savingTotalAmount = request.getTargetAmount() - depositAmount;
        double savingMonthlyAmount = (double) savingTotalAmount / request.getTargetMonths();

        // 최고 금리 상품들로 수익률 계산
        List<DepositProduct> topDeposits = getTopDepositProducts(request.getTargetMonths(), 1);
        List<SavingProduct> topSavings = getTopSavingProducts(request.getTargetMonths(), 1);

        double totalExpectedReturn = 0.0;

        if (!topDeposits.isEmpty()) {
            DepositProduct deposit = topDeposits.get(0);
            DepositOption bestDepositOption = deposit.getOptions().stream()
                    .filter(o -> o.getSaveTrm().equals(request.getTargetMonths()))
                    .max(Comparator.comparing(DepositOption::getBestRate))
                    .orElse(null);

            if (bestDepositOption != null) {
                double depositReturn = calculateDepositReturn(
                        (double) depositAmount,  // 🔧 수정: Long → double 캐스팅
                        bestDepositOption.getBestRate(),
                        request.getTargetMonths()
                );
                totalExpectedReturn += (depositReturn - (double) depositAmount);  // 🔧 수정: Long → double 캐스팅
            }
        }

        if (!topSavings.isEmpty()) {
            SavingProduct saving = topSavings.get(0);
            SavingOption bestSavingOption = saving.getOptions().stream()
                    .filter(o -> o.getSaveTrm().equals(request.getTargetMonths()))
                    .max(Comparator.comparing(SavingOption::getBestRate))
                    .orElse(null);

            if (bestSavingOption != null) {
                double savingReturn = bestSavingOption.calculateExpectedReturn(
                        savingMonthlyAmount, request.getTargetMonths()
                );
                totalExpectedReturn += (savingReturn - (double) savingTotalAmount);  // 🔧 수정: Long → double 캐스팅
            }
        }

        return OptimalCombination.builder()
                .depositAmount(depositAmount)
                .savingMonthlyAmount((long) savingMonthlyAmount)  // 🔧 수정: double → Long 캐스팅
                .totalExpectedReturn(totalExpectedReturn)
                .expectedTotalAmount((double) request.getTargetAmount() + totalExpectedReturn)  // 🔧 수정: Long → double 캐스팅
                .riskLevel("낮음")
                .description(String.format("예금 %d만원 + 적금 월 %.0f만원의 안정적인 조합",
                        depositAmount / 10000, savingMonthlyAmount / 10000))
                .build();
    }

    // =========================== 조회 서비스 ===========================

    /**
     * 예금 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public List<DepositProduct> getAllDepositProducts() {
        return depositProductRepository.findAll();
    }

    /**
     * 적금 상품 목록 조회
     */
    @Transactional(readOnly = true)
    public List<SavingProduct> getAllSavingProducts() {
        return savingProductRepository.findAll();
    }

    /**
     * 상품 검색
     */
    @Transactional(readOnly = true)
    public SearchResponse searchProducts(String keyword) {
        List<DepositProduct> deposits = depositProductRepository.findByFinPrdtNmContaining(keyword);
        deposits.addAll(depositProductRepository.findByKorCoNmContaining(keyword));

        List<SavingProduct> savings = savingProductRepository.findByFinPrdtNmContaining(keyword);
        savings.addAll(savingProductRepository.findByKorCoNmContaining(keyword));

        return SearchResponse.builder()
                .keyword(keyword)
                .depositProducts(deposits.stream().distinct().collect(Collectors.toList()))
                .savingProducts(savings.stream().distinct().collect(Collectors.toList()))
                .totalCount(deposits.size() + savings.size())
                .build();
    }
}