package com.walletmapx.backend.config;

import com.walletmapx.backend.repository.UserRepository;
import com.walletmapx.backend.service.MonthlySnapshotService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Keeps every user's current-month net-worth snapshot fresh, even if
 * they never open the dashboard. The last run of a month becomes that
 * month's final value.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotScheduler {

    private final UserRepository userRepository;
    private final MonthlySnapshotService monthlySnapshotService;

    // Every day at 23:55 India time (configurable)
    @Scheduled(
            cron = "${app.snapshot.cron:0 55 23 * * *}",
            zone = "Asia/Kolkata"
    )
    public void refreshAllSnapshots() {
        runForAllUsers("scheduled");
    }

    // Catch-up if the server was off at 23:55
    @EventListener(ApplicationReadyEvent.class)
    public void refreshOnStartup() {
        runForAllUsers("startup");
    }

    private void runForAllUsers(String trigger) {

        List<Long> userIds = userRepository.findAllIds();

        int ok = 0;
        int failed = 0;

        for (Long userId : userIds) {
            try {
                // Own transaction per user (service is @Transactional)
                monthlySnapshotService.refreshCurrentMonthSnapshot(userId);
                ok++;
            } catch (Exception exception) {
                failed++;
                log.warn("Snapshot refresh failed for user {} ({}): {}",
                        userId, trigger, exception.getMessage());
            }
        }

        log.info("Snapshot refresh [{}]: {} ok, {} failed",
                trigger, ok, failed);
    }
}