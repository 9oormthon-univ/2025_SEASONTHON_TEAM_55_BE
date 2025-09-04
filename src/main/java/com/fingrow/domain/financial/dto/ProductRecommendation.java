package com.fingrow.domain.financial.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 개별 상품 추천 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "개별 상품 추천")
public class ProductRecommendation {

    @Schema(description = "상품 유형", example = "예금")
    private String productType;

    @Schema(description = "은행명", example = "우리은행")
    private String bankName;

    @Schema(description = "상품명", example = "WON플러스예금")
    private String productName;

    @Schema(description = "금리(%)", example = "2.45")
    private Double interestRate;

    @Schema(description = "기간(개월)", example = "12")
    private Integer term;

    @Schema(description = "예상 수익", example = "125000")
    private Double expectedReturn;

    @Schema(description = "예상 총 금액", example = "5125000")
    private Double expectedTotalAmount;

    @Schema(description = "우대 조건")
    private String specialCondition;

    @Schema(description = "가입 방법")
    private String joinWay;

    @Schema(description = "최고 한도")
    private Long maxLimit;

    @Schema(description = "초기 투입 금액", example = "5000000")
    private Double initialAmount;

    @Schema(description = "월 납입 금액", example = "0")
    private Double monthlyAmount;

    @Schema(description = "적립 유형", example = "정액적립식")
    private String reserveType;

    @Schema(description = "위험도", example = "매우낮음")
    private String riskLevel;
}
