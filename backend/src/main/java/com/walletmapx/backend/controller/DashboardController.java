package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.dashboard.DashboardResponse;
import com.walletmapx.backend.dto.dashboard.MonthlyDashboardResponse;
import com.walletmapx.backend.dto.dashboard.YearlyNetWorthItem;
import com.walletmapx.backend.service.DashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;


    // =========================================================
    // MAIN DASHBOARD
    // =========================================================

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            Authentication authentication) {

        Long userId =
                Long.parseLong(authentication.getName());

        return ResponseEntity.ok(
                dashboardService.getDashboard(userId)
        );
    }


    // =========================================================
    // MONTHLY DASHBOARD
    // =========================================================

    @GetMapping("/monthly/{month}")
    public ResponseEntity<MonthlyDashboardResponse> getMonthlyDashboard(
            @PathVariable String month,
            Authentication authentication) {

        Long userId =
                Long.parseLong(authentication.getName());

        return ResponseEntity.ok(
                dashboardService.getMonthlyDashboard(
                        userId,
                        month
                )
        );
    }


    // =========================================================
    // NET WORTH HISTORY
    // =========================================================

    @GetMapping("/net-worth/yearly")
    public ResponseEntity<List<YearlyNetWorthItem>> getYearlyNetWorth(
            Authentication authentication) {

        Long userId =
                Long.parseLong(authentication.getName());

        return ResponseEntity.ok(
                dashboardService.getYearlyNetWorth(userId)
        );
    }
}