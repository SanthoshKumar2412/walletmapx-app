package com.walletmapx.backend.repository;

import com.walletmapx.backend.entity.Liability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LiabilityRepository extends JpaRepository<Liability, Long> {

    List<Liability> findByUserId(Long userId);

    List<Liability> findByUserIdOrderByCreatedAtDesc(Long userId);
}