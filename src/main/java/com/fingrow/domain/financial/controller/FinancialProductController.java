package com.fingrow.domain.financial.controller;

import com.fingrow.domain.financial.dto.RecommendationRequest;
import com.fingrow.domain.financial.dto.RecommendationResponse;
import com.fingrow.domain.financial.dto.SearchResponse;
import com.fingrow.domain.financial.entity.DepositProduct;
import com.fingrow.domain.financial.entity.SavingProduct;
import com.fingrow.domain.financial.service.FinancialProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/financial")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "금융상품 API", description = "예금, 적금 상품 관리 및 추천 API")
public class FinancialProductController {

    private final FinancialProductService financialProductService;

    // ========================== 데이터 동기화 API ==========================

    @PostMapping("/sync/deposits")
    @Operation(
            summary = "예금 상품 데이터 동기화",
            description = "금융감독원 API에서 예금 상품 데이터를 가져와 DB에 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동기화 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<String>> syncDepositProducts() {
        try {
            log.info("예금 상품 데이터 동기화 요청");
            financialProductService.syncDepositProducts();
            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success("예금 상품 데이터 동기화가 완료되었습니다.")
            );
        } catch (Exception e) {
            log.error("예금 상품 데이터 동기화 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("예금 상품 데이터 동기화에 실패했습니다: " + e.getMessage())
            );
        }
    }

    @PostMapping("/sync/savings")
    @Operation(
            summary = "적금 상품 데이터 동기화",
            description = "금융감독원 API에서 적금 상품 데이터를 가져와 DB에 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동기화 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<String>> syncSavingProducts() {
        try {
            log.info("적금 상품 데이터 동기화 요청");
            financialProductService.syncSavingProducts();
            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success("적금 상품 데이터 동기화가 완료되었습니다.")
            );
        } catch (Exception e) {
            log.error("적금 상품 데이터 동기화 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("적금 상품 데이터 동기화에 실패했습니다: " + e.getMessage())
            );
        }
    }

    @PostMapping("/sync/all")
    @Operation(
            summary = "전체 상품 데이터 동기화",
            description = "예금과 적금 상품 데이터를 모두 동기화합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "동기화 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<String>> syncAllProducts() {
        try {
            log.info("전체 상품 데이터 동기화 요청");
            financialProductService.syncDepositProducts();
            financialProductService.syncSavingProducts();
            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success("전체 상품 데이터 동기화가 완료되었습니다.")
            );
        } catch (Exception e) {
            log.error("전체 상품 데이터 동기화 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("전체 상품 데이터 동기화에 실패했습니다: " + e.getMessage())
            );
        }
    }

    // ========================== 상품 조회 API ==========================

    @GetMapping("/deposits")
    @Operation(
            summary = "예금 상품 목록 조회",
            description = "저장된 모든 예금 상품을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<List<DepositProduct>>> getAllDepositProducts() {
        try {
            List<DepositProduct> products = financialProductService.getAllDepositProducts();
            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success(
                            products.size() + "개의 예금 상품을 조회했습니다.",
                            products
                    )
            );
        } catch (Exception e) {
            log.error("예금 상품 조회 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("예금 상품 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    @GetMapping("/savings")
    @Operation(
            summary = "적금 상품 목록 조회",
            description = "저장된 모든 적금 상품을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<List<SavingProduct>>> getAllSavingProducts() {
        try {
            List<SavingProduct> products = financialProductService.getAllSavingProducts();
            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success(
                            products.size() + "개의 적금 상품을 조회했습니다.",
                            products
                    )
            );
        } catch (Exception e) {
            log.error("적금 상품 조회 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("적금 상품 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    // ========================== 상품 검색 API ==========================

    @GetMapping("/search")
    @Operation(
            summary = "상품 검색",
            description = "상품명이나 은행명으로 예금/적금 상품을 검색합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<SearchResponse>> searchProducts(
            @Parameter(description = "검색 키워드", example = "우리은행", required = true)
            @RequestParam @NotBlank(message = "검색 키워드는 필수입니다.") String keyword) {
        try {
            log.info("상품 검색 요청: {}", keyword);
            SearchResponse searchResult = financialProductService.searchProducts(keyword);
            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success(
                            "'" + keyword + "'로 " + searchResult.getTotalCount() + "개 상품을 찾았습니다.",
                            searchResult
                    )
            );
        } catch (Exception e) {
            log.error("상품 검색 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("상품 검색에 실패했습니다: " + e.getMessage())
            );
        }
    }

    // ========================== 상품 추천 API ==========================

    @PostMapping("/recommend")
    @Operation(
            summary = "맞춤형 상품 추천",
            description = "목표 금액과 기간을 기반으로 최적의 예적금 상품을 추천합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<RecommendationResponse>> recommendProducts(
            @Parameter(description = "추천 요청 정보", required = true)
            @RequestBody @Valid RecommendationRequest request) {
        try {
            log.info("상품 추천 요청: 목표금액={}, 기간={}개월", request.getTargetAmount(), request.getTargetMonths());
            RecommendationResponse recommendation = financialProductService.recommendProducts(request);
            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success(
                            recommendation.getTotalProducts() + "개의 상품을 추천합니다.",
                            recommendation
                    )
            );
        } catch (Exception e) {
            log.error("상품 추천 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("상품 추천에 실패했습니다: " + e.getMessage())
            );
        }
    }

    // ========================== 간편 추천 API ==========================

    @GetMapping("/recommend/quick")
    @Operation(
            summary = "간편 상품 추천",
            description = "목표 금액과 기간만으로 빠른 상품 추천을 제공합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추천 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<RecommendationResponse>> quickRecommend(
            @Parameter(description = "목표 금액", example = "5000000", required = true)
            @RequestParam @Min(value = 1000000, message = "목표 금액은 최소 100만원 이상이어야 합니다.") Long targetAmount,

            @Parameter(description = "목표 기간(개월)", example = "12", required = true)
            @RequestParam @Min(value = 1, message = "목표 기간은 최소 1개월 이상이어야 합니다.") Integer targetMonths,

            @Parameter(description = "월 예산", example = "500000")
            @RequestParam(required = false, defaultValue = "0") Long monthlyBudget) {

        try {
            RecommendationRequest request = RecommendationRequest.builder()
                    .targetAmount(targetAmount)
                    .targetMonths(targetMonths)
                    .monthlyBudget(monthlyBudget)
                    .riskPreference("LOW")
                    .build();

            log.info("간편 추천 요청: 목표금액={}, 기간={}개월", targetAmount, targetMonths);
            RecommendationResponse recommendation = financialProductService.recommendProducts(request);
            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success(
                            "간편 추천 완료: " + recommendation.getTotalProducts() + "개 상품",
                            recommendation
                    )
            );
        } catch (Exception e) {
            log.error("간편 추천 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("간편 추천에 실패했습니다: " + e.getMessage())
            );
        }
    }

    // ========================== 통계 API ==========================

    @GetMapping("/stats/summary")
    @Operation(
            summary = "상품 현황 요약",
            description = "전체 상품 수, 평균 금리 등 요약 정보를 제공합니다."
    )
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<Object>> getProductSummary() {
        try {
            List<DepositProduct> deposits = financialProductService.getAllDepositProducts();
            List<SavingProduct> savings = financialProductService.getAllSavingProducts();

            // 간단한 통계 정보 생성
            Object summary = new Object() {
                public final int totalDepositProducts = deposits.size();
                public final int totalSavingProducts = savings.size();
                public final int totalProducts = deposits.size() + savings.size();
                public final String lastUpdateTime = java.time.LocalDateTime.now().toString();
            };

            return ResponseEntity.ok(
                    com.fingrow.domain.financial.dto.ApiResponse.success("상품 현황 요약", summary)
            );
        } catch (Exception e) {
            log.error("상품 현황 조회 실패", e);
            return ResponseEntity.internalServerError().body(
                    com.fingrow.domain.financial.dto.ApiResponse.error("상품 현황 조회에 실패했습니다: " + e.getMessage())
            );
        }
    }

    // ========================== 헬스체크 API ==========================

    @GetMapping("/health")
    @Operation(
            summary = "API 상태 확인",
            description = "API 서버의 상태를 확인합니다."
    )
    public ResponseEntity<com.fingrow.domain.financial.dto.ApiResponse<Object>> healthCheck() {
        Object health = new Object() {
            public final String status = "UP";
            public final String timestamp = java.time.LocalDateTime.now().toString();
            public final String version = "1.0.0";
        };

        return ResponseEntity.ok(
                com.fingrow.domain.financial.dto.ApiResponse.success("API 서버가 정상 작동 중입니다.", health)
        );
    }
}
