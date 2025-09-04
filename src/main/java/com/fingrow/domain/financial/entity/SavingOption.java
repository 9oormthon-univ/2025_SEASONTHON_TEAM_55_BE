package com.fingrow.domain.financial.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "saving_options",
        indexes = {
                @Index(name = "idx_saving_product_id", columnList = "saving_product_id"),
                @Index(name = "idx_save_trm", columnList = "save_trm"),
                @Index(name = "idx_intr_rate2", columnList = "intr_rate2"),
                @Index(name = "idx_rsrv_type", columnList = "rsrv_type")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saving_product_id", nullable = false)
    private SavingProduct savingProduct;

    @Column(name = "intr_rate_type", length = 1)
    private String intrRateType; // 저축 금리 유형 (S: 단리, M: 복리)

    @Column(name = "intr_rate_type_nm", length = 20)
    private String intrRateTypeNm; // 저축금리유형명

    @Column(name = "rsrv_type", length = 1)
    private String rsrvType; // 적립유형 (S: 정액적립식, F: 자유적립식)

    @Column(name = "rsrv_type_nm", length = 20)
    private String rsrvTypeNm; // 적립유형명

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

    // 적립 타입 확인 메서드
    public boolean isFixedAmount() {
        return "S".equals(rsrvType); // 정액적립식
    }

    public boolean isFreeAmount() {
        return "F".equals(rsrvType); // 자유적립식
    }

    // 적금 만기 예상 수익 계산 (복리 계산)
    public Double calculateExpectedReturn(Double monthlyAmount, Integer months) {
        if (getBestRate() == null || getBestRate() <= 0) {
            return monthlyAmount * months; // 이자 없을 경우 원금만
        }

        double monthlyRate = getBestRate() / 100.0 / 12.0; // 월 이자율

        // 적금 복리 공식: PMT * ((1 + r)^n - 1) / r * (1 + r)
        return monthlyAmount * ((Math.pow(1 + monthlyRate, months) - 1) / monthlyRate) * (1 + monthlyRate);
    }
}