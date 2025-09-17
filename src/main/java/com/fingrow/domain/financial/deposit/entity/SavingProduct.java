package com.fingrow.domain.financial.deposit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

// 적금 상품 기본 정보 엔티티
@Entity
@Table(name = "saving_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fin_prdt_cd", unique = true, nullable = false, length = 50)
    private String finPrdtCd; // 금융상품코드

    @Column(name = "kor_co_nm", length = 100)
    private String korCoNm; // 금융회사명

    @Column(name = "fin_prdt_nm", length = 200)
    private String finPrdtNm; // 금융상품명

    @Column(name = "join_way", columnDefinition = "TEXT")
    private String joinWay; // 가입방법

    @Column(name = "mtrt_int", columnDefinition = "TEXT")
    private String mtrtInt; // 만기후이자율

    @Column(name = "spcl_cnd", columnDefinition = "TEXT")
    private String spclCnd; // 우대조건

    @Column(name = "join_deny")
    private Integer joinDeny; // 가입제한 (1: 제한없음, 2: 서민전용, 3: 일부제한)

    @Column(name = "join_member", columnDefinition = "TEXT")
    private String joinMember; // 가입대상

    @Column(name = "etc_note", columnDefinition = "TEXT")
    private String etcNote; // 기타 유의사항

    @Column(name = "max_limit")
    private Long maxLimit; // 최고한도

    @Column(name = "dcls_month", length = 10)
    private String dclsMonth; // 공시제출월 (YYYY-MM 형식)

    @Column(name = "dcls_strt_day", length = 10)
    private String dclsStrtDay; // 공시시작일

    @Column(name = "dcls_end_day", length = 10)
    private String dclsEndDay; // 공시종료일

    @Column(name = "fin_co_no", length = 10)
    private String finCoNo; // 금융회사코드

    // 적금 금리 옵션들과의 일대다 관계
    @OneToMany(mappedBy = "savingProduct", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SavingOption> options = new ArrayList<>();

    // 편의 메서드
    public void addOption(SavingOption option) {
        this.options.add(option);
        option.setSavingProduct(this);
    }

    public void removeOption(SavingOption option) {
        this.options.remove(option);
        option.setSavingProduct(null);
    }
}