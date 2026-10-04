package com.walletmapx.backend.repository;

import com.walletmapx.backend.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface InvestmentRepository extends JpaRepository<Investment, Long> {

    List<Investment> findByUserId(Long userId);
    @Query("select sum(i.currentValue) from Investment i where i.userId = :userId")
    BigDecimal sumCurrentValue(@Param("userId") Long userId);
}