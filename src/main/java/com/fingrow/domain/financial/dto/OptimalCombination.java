package com.fingrow.domain.financial.dto;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 최적 조합 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "최적 조합")
public class OptimalCombination {

    @Schema(description = "예금 투입 금액", example = "2500000")
    private Long depositAmount;

    @Schema(description = "적금 월 납입 금액", example = "208333")
    private Long savingMonthlyAmount;

    @Schema(description = "총 예상 수익", example = "150000")
    private Double totalExpectedReturn;

    @Schema(description = "예상 총 금액", example = "5150000")
    private Double expectedTotalAmount;

    @Schema(description = "위험도", example = "낮음")
    private String riskLevel;

    @Schema(description = "조합 설명")
    private String description;
}
