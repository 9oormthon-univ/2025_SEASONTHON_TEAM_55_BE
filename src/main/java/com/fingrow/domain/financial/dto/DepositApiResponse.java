package com.fingrow.domain.financial.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 금융감독원 예금 API 응답 DTO
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "금융감독원 예금 API 응답")
public class DepositApiResponse {

    @JsonProperty("result")
    private Result result;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        @JsonProperty("baseList")
        private List<DepositProductDto> baseList;

        @JsonProperty("optionList")
        private List<DepositOptionDto> optionList;
    }
}
