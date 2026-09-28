package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.statistics.MonthlyTrendItem;
import com.walletmapx.backend.dto.statistics.StatisticsResponse;

import java.util.List;

public interface StatisticsService {

    
    StatisticsResponse getOverview(Long userId);

    List<MonthlyTrendItem> getTrend(Long userId, int months);
}
