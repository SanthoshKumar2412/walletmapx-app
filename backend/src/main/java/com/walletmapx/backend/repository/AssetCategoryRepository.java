package com.walletmapx.backend.repository;

import com.walletmapx.backend.entity.AssetCategory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssetCategoryRepository
        extends JpaRepository<AssetCategory, Long> {

    Optional<AssetCategory> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}