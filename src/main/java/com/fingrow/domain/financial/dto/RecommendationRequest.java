package com.fingrow.domain.financial.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;


/**
 * 상품 추천 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상품 추천 요청")
public class RecommendationRequest {

    @NotNull(message = "목표 금액은 필수입니다.")
    @Min(value = 1000000, message = "목표 금액은 최소 100만원 이상이어야 합니다.")
    @Schema(description = "목표 금액", example = "5000000", required = true)
    private Long targetAmount;

    @NotNull(message = "목표 기간은 필수입니다.")
    @Min(value = 1, message = "목표 기간은 최소 1개월 이상이어야 합니다.")
    @Max(value = 60, message = "목표 기간은 최대 60개월까지 가능합니다.")
    @Schema(description = "목표 기간(개월)", example = "12", required = true)
    private Integer targetMonths;

    @Min(value = 0, message = "월 예산은 0원 이상이어야 합니다.")
    @Schema(description = "월 예산 (적금용)", example = "500000")
    private Long monthlyBudget;

    @Schema(description = "선호 은행", example = "우리은행")
    private String preferredBank;

    @Schema(description = "위험도 선호", example = "LOW", allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private String riskPreference = "LOW";
}
