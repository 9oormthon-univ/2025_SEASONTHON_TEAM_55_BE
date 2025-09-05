package com.fingrow.domain.onboard.repository;

import com.fingrow.domain.onboard.entity.InvestmentPreference;
import com.fingrow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentPreferenceRepository extends JpaRepository<InvestmentPreference, Long> {
    
    Optional<InvestmentPreference> findByUserId(Long userId);
    
    Optional<InvestmentPreference> findByUser(User user);
    
    boolean existsByUserId(Long userId);
    
    void deleteByUserId(Long userId);
    
    @Query("SELECT ip FROM InvestmentPreference ip " +
           "JOIN FETCH ip.user " +
           "WHERE ip.user.id = :userId")
    Optional<InvestmentPreference> findByUserIdWithUser(@Param("userId") Long userId);
    
    /**
     * 사용자의 가장 최신 투자 성향을 조회합니다
     */
    @Query("SELECT ip FROM InvestmentPreference ip " +
           "WHERE ip.user.id = :userId " +
           "ORDER BY ip.updatedAt DESC, ip.createdAt DESC " +
           "LIMIT 1")
    Optional<InvestmentPreference> findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 사용자의 가장 최신 투자 성향을 조회합니다 (User 엔티티로)
     */
    @Query("SELECT ip FROM InvestmentPreference ip " +
           "WHERE ip.user = :user " +
           "ORDER BY ip.updatedAt DESC, ip.createdAt DESC " +
           "LIMIT 1")
    Optional<InvestmentPreference> findLatestByUser(@Param("user") User user);
}