package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.statistics.MonthlyTrendItem;
import com.walletmapx.backend.dto.statistics.StatisticsResponse;

import java.util.List;

public interface StatisticsService {

    /**
     * Income, expenses, savings and category breakdown for ONE month
     * (month = "YYYY-MM", null = current month). Assets, liabilities,
     * investments and net worth are current point-in-time values.
     */
    StatisticsResponse getOverview(Long userId, String month);

    List<MonthlyTrendItem> getTrend(Long userId, int months);
}