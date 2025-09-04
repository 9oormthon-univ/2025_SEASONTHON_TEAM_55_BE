package com.fingrow.domain.financial.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 적금 옵션 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "적금 금리 옵션 정보")
public class SavingOptionDto {

    @JsonProperty("fin_prdt_cd")
    @Schema(description = "금융상품코드")
    private String finPrdtCd;

    @JsonProperty("intr_rate_type")
    @Schema(description = "저축금리유형")
    private String intrRateType;

    @JsonProperty("intr_rate_type_nm")
    @Schema(description = "저축금리유형명")
    private String intrRateTypeNm;

    @JsonProperty("rsrv_type")
    @Schema(description = "적립유형", example = "S")
    private String rsrvType;

    @JsonProperty("rsrv_type_nm")
    @Schema(description = "적립유형명", example = "정액적립식")
    private String rsrvTypeNm;

    @JsonProperty("intr_rate")
    @Schema(description = "저축금리")
    private Double intrRate;

    @JsonProperty("intr_rate2")
    @Schema(description = "최고우대금리")
    private Double intrRate2;

    @JsonProperty("save_trm")
    @Schema(description = "저축기간(개월)")
    private Integer saveTrm;
}

