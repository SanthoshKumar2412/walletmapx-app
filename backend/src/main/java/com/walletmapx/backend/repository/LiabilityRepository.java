package com.walletmapx.backend.repository;

import com.walletmapx.backend.entity.Liability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface LiabilityRepository extends JpaRepository<Liability, Long> {

    List<Liability> findByUserId(Long userId);

    List<Liability> findByUserIdOrderByCreatedAtDesc(Long userId);
    @Query("select sum(l.outstandingAmount) from Liability l where l.userId = :userId")
    BigDecimal sumOutstanding(@Param("userId") Long userId);
}