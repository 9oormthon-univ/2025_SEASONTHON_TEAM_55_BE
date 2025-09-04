package com.fingrow.domain.financial.dto;

import com.fingrow.domain.financial.entity.DepositProduct;
import com.fingrow.domain.financial.entity.SavingProduct;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 검색 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상품 검색 응답")
public class SearchResponse {

    @Schema(description = "검색 키워드")
    private String keyword;

    @Schema(description = "검색된 예금 상품 목록")
    private List<DepositProduct> depositProducts;

    @Schema(description = "검색된 적금 상품 목록")
    private List<SavingProduct> savingProducts;

    @Schema(description = "총 검색 결과 수")
    private Integer totalCount;
}