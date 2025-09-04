package com.fingrow.domain.financial.entity;

import jakarta.persistence.*;
import lombok.*;

// 예금 상품 금리 옵션 엔티티
@Entity
@Table(name = "deposit_options",
        indexes = {
                @Index(name = "idx_deposit_product_id", columnList = "deposit_product_id"),
                @Index(name = "idx_save_trm", columnList = "save_trm"),
                @Index(name = "idx_intr_rate2", columnList = "intr_rate2")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_product_id", nullable = false)
    private DepositProduct depositProduct;

    @Column(name = "intr_rate_type", length = 1)
    private String intrRateType; // 저축 금리 유형 (S: 단리, M: 복리)

    @Column(name = "intr_rate_type_nm", length = 20)
    private String intrRateTypeNm; // 저축금리유형명

    // 🔧 수정: precision, scale 제거 - MySQL DOUBLE 타입 문제 해결
    @Column(name = "intr_rate")
    private Double intrRate; // 저축금리 (기본금리)

    // 🔧 수정: precision, scale 제거 - MySQL DOUBLE 타입 문제 해결
    @Column(name = "intr_rate2")
    private Double intrRate2; // 최고우대금리

    @Column(name = "save_trm")
    private Integer saveTrm; // 저축기간(개월)

    // 최고 금리를 얻기 위한 편의 메서드
    public Double getBestRate() {
        if (intrRate2 != null && intrRate2 > 0) {
            return intrRate2;
        }
        return intrRate != null ? intrRate : 0.0;
    }

    // 연이율 계산
    public Double getAnnualRate() {
        return getBestRate();
    }
}