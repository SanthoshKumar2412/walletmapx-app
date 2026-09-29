package com.walletmapx.backend.repository;

import com.walletmapx.backend.entity.MonthlySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MonthlySnapshotRepository
        extends JpaRepository<MonthlySnapshot, Long> {

    List<MonthlySnapshot> findByUserIdOrderBySnapshotMonthDesc(
            Long userId
    );

    List<MonthlySnapshot> findByUserIdOrderBySnapshotMonthAsc(
            Long userId
    );

    Optional<MonthlySnapshot> findByUserIdAndSnapshotMonth(
            Long userId,
            LocalDate snapshotMonth
    );
}