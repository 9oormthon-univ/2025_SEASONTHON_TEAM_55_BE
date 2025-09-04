package com.fingrow.domain.financial.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 예금 옵션 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "예금 금리 옵션 정보")
public class DepositOptionDto {

    @JsonProperty("fin_prdt_cd")
    @Schema(description = "금융상품코드")
    private String finPrdtCd;

    @JsonProperty("intr_rate_type")
    @Schema(description = "저축금리유형", example = "S")
    private String intrRateType;

    @JsonProperty("intr_rate_type_nm")
    @Schema(description = "저축금리유형명", example = "단리")
    private String intrRateTypeNm;

    @JsonProperty("intr_rate")
    @Schema(description = "저축금리", example = "2.45")
    private Double intrRate;

    @JsonProperty("intr_rate2")
    @Schema(description = "최고우대금리", example = "2.45")
    private Double intrRate2;

    @JsonProperty("save_trm")
    @Schema(description = "저축기간(개월)", example = "12")
    private Integer saveTrm;
}
