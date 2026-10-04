package com.walletmapx.backend.repository;

import com.walletmapx.backend.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    List<Asset> findByUserId(Long userId);

    List<Asset> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Asset> findByUserIdAndCategoryId(
            Long userId,
            Long categoryId
    );
    
    @Query("select sum(a.currentValue) from Asset a where a.userId = :userId")
    BigDecimal sumCurrentValue(@Param("userId") Long userId);
}