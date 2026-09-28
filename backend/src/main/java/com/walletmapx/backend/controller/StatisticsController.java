package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.statistics.MonthlyTrendItem;
import com.walletmapx.backend.dto.statistics.StatisticsResponse;
import com.walletmapx.backend.service.StatisticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * All-time totals, net worth, and income/expense by category.
     */
    @GetMapping("/overview")
    public ResponseEntity<StatisticsResponse> getOverview(
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                statisticsService.getOverview(userId)
        );
    }

    /**
     * Monthly income/expense/savings series for the last
     * `months` months (default 6, max 24), oldest first.
     */
    @GetMapping("/trend")
    public ResponseEntity<List<MonthlyTrendItem>> getTrend(
            @RequestParam(defaultValue = "6") int months,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                statisticsService.getTrend(userId, months)
        );
    }
}
