package com.fingrow.domain.onboard.service;

import com.fingrow.domain.onboard.dto.OnboardDto;
import com.fingrow.domain.onboard.entity.InvestmentPreference;
import com.fingrow.domain.onboard.repository.InvestmentPreferenceRepository;
import com.fingrow.domain.user.entity.User;
import com.fingrow.domain.user.repository.UserRepository;
import com.fingrow.global.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardService {

    private final UserRepository userRepository;
    private final InvestmentPreferenceRepository investmentPreferenceRepository;
    

    /**
     * 온보딩 설문을 처리하고 투자 성향을 분석합니다
     */
    public OnboardDto.SurveyResponse processSurvey(String userId, OnboardDto.SurveyRequest request) {
        log.info("Processing onboard survey for user: {}", userId);

        // 1. 사용자 조회
        Long userIdLong;
        try {
            userIdLong = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            throw new RuntimeException("유효하지 않은 사용자 ID 형식입니다.");
        }
        
        User user = userRepository.findById(userIdLong)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 기존 투자 선호도 조회 또는 새로 생성
        InvestmentPreference preference = getOrCreateInvestmentPreference(user, request);

        // 3. 입력값 검증
        validateInvestmentConsistency(request);
        
        // 4. 투자 성향 분석
        OnboardDto.InvestmentAnalysis analysis = analyzeInvestmentProfile(request);

        // 5. 응답 생성
        return new OnboardDto.SurveyResponse(
                true,
                "투자 성향 분석이 완료되었습니다.",
                analysis
        );
    }

    /**
     * 기존 투자 선호도를 업데이트하거나 새로 생성합니다
     */
    private InvestmentPreference getOrCreateInvestmentPreference(User user, OnboardDto.SurveyRequest request) {
        Optional<InvestmentPreference> existingPreference = investmentPreferenceRepository.findLatestByUser(user);

        if (existingPreference.isPresent()) {
            // 기존 선호도 업데이트
            InvestmentPreference preference = existingPreference.get();
            preference.updatePreferences(
                    request.getRiskLevel(),
                    request.getInvestmentGoal(),
                    request.getTargetAmount(),
                    request.getInvestmentPeriod(),
                    request.getPreferredInvestmentTypes(),
                    request.getMonthlyInvestmentAmount(),
                    request.getCurrentInvestmentExperience(),
                    request.getAdditionalNotes()
            );
            log.info("Updated existing investment preference for user: {}", user.getId());
            return investmentPreferenceRepository.save(preference);
        } else {
            // 새로운 선호도 생성
            InvestmentPreference newPreference = InvestmentPreference.builder()
                    .user(user)
                    .riskLevel(request.getRiskLevel())
                    .investmentGoal(request.getInvestmentGoal())
                    .targetAmount(request.getTargetAmount())
                    .investmentPeriod(request.getInvestmentPeriod())
                    .preferredInvestmentTypes(request.getPreferredInvestmentTypes())
                    .monthlyInvestmentAmount(request.getMonthlyInvestmentAmount())
                    .currentInvestmentExperience(request.getCurrentInvestmentExperience())
                    .additionalNotes(request.getAdditionalNotes())
                    .build();
            log.info("Created new investment preference for user: {}", user.getId());
            return investmentPreferenceRepository.save(newPreference);
        }
    }

    /**
     * 설문 결과를 바탕으로 투자 성향을 분석합니다
     */
    private OnboardDto.InvestmentAnalysis analyzeInvestmentProfile(OnboardDto.SurveyRequest request) {
        RiskLevel riskLevel = request.getRiskLevel();

        // 투자자 유형 결정
        String investorType = determineInvestorType(riskLevel);

        // 추천 상품 생성
        OnboardDto.RecommendedProduct product = createRecommendedProduct(riskLevel, request.getPreferredInvestmentTypes());

        // 투자 성향 설명 생성
        String description = generateInvestorDescription(riskLevel, request.getInvestmentGoal(), request.getInvestmentPeriod());

        return new OnboardDto.InvestmentAnalysis(
                investorType,
                riskLevel,
                product,
                description
        );
    }

    /**
     * 위험 수준에 따른 투자자 유형을 결정합니다
     */
    private String determineInvestorType(RiskLevel riskLevel) {
        switch (riskLevel) {
            case CONSERVATIVE:
                return "안전형 투자자";
            case MODERATE:
                return "안정형 투자자";
            case BALANCED:
                return "균형형 투자자";
            case AGGRESSIVE:
                return "적극형 투자자";
            case SPECULATIVE:
                return "공격형 투자자";
            default:
                return "균형형 투자자";
        }
    }

    /**
     * 위험 수준과 선호 투자 유형에 따른 추천 상품을 생성합니다
     */
    private OnboardDto.RecommendedProduct createRecommendedProduct(RiskLevel riskLevel,
                                                                  java.util.Set<PreferredInvestmentType> preferredTypes) {
        
        PreferredInvestmentType recommendedType;
        String productName;
        String reason;
        String expectedReturn;
        String riskDescription;
        
        // 1순위: 사용자 선호 유형 중에서 위험 수준에 맞는 것 선택
        PreferredInvestmentType userPreferred = selectFromUserPreferences(riskLevel, preferredTypes);
        if (userPreferred != null) {
            recommendedType = userPreferred;
        } else {
            // 2순위: 위험 수준에 따른 기본 추천
            recommendedType = getDefaultRecommendation(riskLevel);
        }
        
        // 상품별 정보 설정
        switch (recommendedType) {
            case SAVINGS:
                productName = "고금리 정기예금/적금";
                expectedReturn = "연 3-4%";
                riskDescription = "매우 낮음 (원금보장)";
                reason = "원금이 보장되어 안전하며, 예금자보호법에 의해 보호받는 상품입니다.";
                break;
            case BONDS:
                productName = "국채/회사채";
                expectedReturn = "연 4-6%";
                riskDescription = "낮음";
                reason = "정부나 우량기업에서 발행하는 채권으로 안정적인 이자수익을 제공합니다.";
                break;
            case ETF:
                productName = "코스피/코스닥 ETF";
                expectedReturn = "연 5-10%";
                riskDescription = "중간";
                reason = "시장 전체에 분산투자하여 개별 주식 위험을 줄이면서 시장 수익률을 추구합니다.";
                break;
            case FUNDS:
                productName = "주식형/혼합형 펀드";
                expectedReturn = "연 7-12%";
                riskDescription = "높음";
                reason = "전문 펀드매니저가 운용하여 높은 수익률을 추구하는 적극적인 투자상품입니다.";
                break;
            default:
                // BALANCED 기본값
                recommendedType = PreferredInvestmentType.ETF;
                productName = "코스피/코스닥 ETF";
                expectedReturn = "연 5-10%";
                riskDescription = "중간";
                reason = "시장 전체에 분산투자하여 개별 주식 위험을 줄이면서 시장 수익률을 추구합니다.";
        }
        
        return new OnboardDto.RecommendedProduct(
                recommendedType, 
                productName, 
                reason, 
                expectedReturn, 
                riskDescription
        );
    }
    
    /**
     * 사용자 선호 유형 중에서 위험 수준에 적합한 것을 선택합니다
     */
    private PreferredInvestmentType selectFromUserPreferences(RiskLevel riskLevel, 
                                                            java.util.Set<PreferredInvestmentType> preferredTypes) {
        
        switch (riskLevel) {
            case CONSERVATIVE:
                // 안전 지향: 예금 > 채권 순으로 선호
                if (preferredTypes.contains(PreferredInvestmentType.SAVINGS)) {
                    return PreferredInvestmentType.SAVINGS;
                }
                if (preferredTypes.contains(PreferredInvestmentType.BONDS)) {
                    return PreferredInvestmentType.BONDS;
                }
                break;
                
            case MODERATE:
                // 보수적: 채권 > 예금 > ETF 순으로 선호  
                if (preferredTypes.contains(PreferredInvestmentType.BONDS)) {
                    return PreferredInvestmentType.BONDS;
                }
                if (preferredTypes.contains(PreferredInvestmentType.SAVINGS)) {
                    return PreferredInvestmentType.SAVINGS;
                }
                if (preferredTypes.contains(PreferredInvestmentType.ETF)) {
                    return PreferredInvestmentType.ETF;
                }
                break;
                
            case BALANCED:
                // 균형형: ETF > 채권 > 펀드 순으로 선호
                if (preferredTypes.contains(PreferredInvestmentType.ETF)) {
                    return PreferredInvestmentType.ETF;
                }
                if (preferredTypes.contains(PreferredInvestmentType.BONDS)) {
                    return PreferredInvestmentType.BONDS;
                }
                if (preferredTypes.contains(PreferredInvestmentType.FUNDS)) {
                    return PreferredInvestmentType.FUNDS;
                }
                break;
                
            case AGGRESSIVE:
            case SPECULATIVE:
                // 적극적: 펀드 > ETF 순으로 선호
                if (preferredTypes.contains(PreferredInvestmentType.FUNDS)) {
                    return PreferredInvestmentType.FUNDS;
                }
                if (preferredTypes.contains(PreferredInvestmentType.ETF)) {
                    return PreferredInvestmentType.ETF;
                }
                break;
        }
        
        return null; // 적합한 선호 유형이 없음
    }
    
    /**
     * 위험 수준에 따른 기본 추천 상품을 반환합니다
     */
    private PreferredInvestmentType getDefaultRecommendation(RiskLevel riskLevel) {
        switch (riskLevel) {
            case CONSERVATIVE:
                return PreferredInvestmentType.SAVINGS;
            case MODERATE:
                return PreferredInvestmentType.BONDS;
            case BALANCED:
                return PreferredInvestmentType.ETF;
            case AGGRESSIVE:
            case SPECULATIVE:
                return PreferredInvestmentType.FUNDS;
            default:
                return PreferredInvestmentType.ETF;
        }
    }

    /**
     * 투자 성향 설명을 생성합니다
     */
    private String generateInvestorDescription(RiskLevel riskLevel, InvestmentGoal goal, InvestmentPeriod period) {
        StringBuilder description = new StringBuilder();

        // 위험 수준별 기본 설명
        switch (riskLevel) {
            case CONSERVATIVE:
                description.append("원금 보장을 최우선으로 하며, 안전한 수익을 추구하는 신중한 투자 성향입니다. ");
                break;
            case MODERATE:
                description.append("안정적인 수익을 추구하며 적당한 위험을 감수할 수 있는 투자 성향입니다. ");
                break;
            case BALANCED:
                description.append("위험과 수익의 균형을 추구하며 다양한 투자 방법을 고려하는 투자 성향입니다. ");
                break;
            case AGGRESSIVE:
                description.append("높은 수익을 위해 상당한 위험을 감수할 수 있는 적극적인 투자 성향입니다. ");
                break;
            case SPECULATIVE:
                description.append("최고 수익을 위해 높은 위험도 기꺼이 감수하는 공격적인 투자 성향입니다. ");
                break;
        }

        // 투자 목표에 따른 추가 설명
        description.append(goal.getDescription()).append("을(를) 목표로 하며, ");

        // 투자 기간에 따른 추가 설명
        switch (period) {
            case SHORT_TERM:
                description.append("단기간 내 목표 달성을 원하는 투자 계획을 가지고 있습니다.");
                break;
            case MEDIUM_TERM:
                description.append("중기적 관점에서 안정적인 자산 증식을 계획하고 있습니다.");
                break;
            case LONG_TERM:
                description.append("장기적 관점에서 꾸준한 자산 성장을 추구합니다.");
                break;
            case VERY_LONG_TERM:
                description.append("초장기 투자를 통한 큰 자산 증식을 목표로 합니다.");
                break;
        }

        return description.toString();
    }


    /**
     * 투자 성향과 선택한 투자 유형 간의 일관성을 검증합니다
     */
    private void validateInvestmentConsistency(OnboardDto.SurveyRequest request) {
        RiskLevel riskLevel = request.getRiskLevel();
        var preferredTypes = request.getPreferredInvestmentTypes();
        
        // 보수적 성향인데 고위험 투자 유형만 선택한 경우 경고
        if (riskLevel == RiskLevel.CONSERVATIVE) {
            boolean hasOnlyHighRiskTypes = preferredTypes.stream()
                    .allMatch(type -> type == PreferredInvestmentType.ETF || type == PreferredInvestmentType.FUNDS);
            if (hasOnlyHighRiskTypes && !preferredTypes.contains(PreferredInvestmentType.SAVINGS) 
                    && !preferredTypes.contains(PreferredInvestmentType.BONDS)) {
                log.warn("Conservative risk level with only high-risk investment types for user");
            }
        }
        
        // 공격적 성향인데 안전 투자 유형만 선택한 경우 경고
        if (riskLevel == RiskLevel.AGGRESSIVE || riskLevel == RiskLevel.SPECULATIVE) {
            boolean hasOnlySafeTypes = preferredTypes.stream()
                    .allMatch(type -> type == PreferredInvestmentType.SAVINGS || type == PreferredInvestmentType.BONDS);
            if (hasOnlySafeTypes) {
                log.warn("Aggressive risk level with only safe investment types for user");
            }
        }
    }
    
    /**
     * 사용자의 현재 투자 선호도를 조회합니다 (가장 최신 데이터)
     */
    @Transactional(readOnly = true)
    public Optional<InvestmentPreference> getUserInvestmentPreference(String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            return investmentPreferenceRepository.findLatestByUserId(userIdLong);
        } catch (NumberFormatException e) {
            log.error("Invalid userId format: {}", userId);
            return Optional.empty();
        }
    }
}