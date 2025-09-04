package com.fingrow.domain.financial.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 적금 상품 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "적금 상품 정보")
public class SavingProductDto {

    @JsonProperty("fin_prdt_cd")
    @Schema(description = "금융상품코드")
    private String finPrdtCd;

    @JsonProperty("kor_co_nm")
    @Schema(description = "금융회사명")
    private String korCoNm;

    @JsonProperty("fin_prdt_nm")
    @Schema(description = "금융상품명")
    private String finPrdtNm;

    @JsonProperty("join_way")
    @Schema(description = "가입방법")
    private String joinWay;

    @JsonProperty("mtrt_int")
    @Schema(description = "만기후이자율")
    private String mtrtInt;

    @JsonProperty("spcl_cnd")
    @Schema(description = "우대조건")
    private String spclCnd;

    @JsonProperty("join_deny")
    @Schema(description = "가입제한")
    private Integer joinDeny;

    @JsonProperty("join_member")
    @Schema(description = "가입대상")
    private String joinMember;

    @JsonProperty("etc_note")
    @Schema(description = "기타 유의사항")
    private String etcNote;

    @JsonProperty("max_limit")
    @Schema(description = "최고한도")
    private Long maxLimit;

    @JsonProperty("dcls_month")
    @Schema(description = "공시제출월")
    private String dclsMonth;

    @JsonProperty("dcls_strt_day")
    @Schema(description = "공시시작일")
    private String dclsStrtDay;

    @JsonProperty("dcls_end_day")
    @Schema(description = "공시종료일")
    private String dclsEndDay;

    @JsonProperty("fin_co_no")
    @Schema(description = "금융회사코드")
    private String finCoNo;
}

