package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.dashboard.DashboardResponse;
import com.walletmapx.backend.dto.dashboard.MonthlyDashboardResponse;
import com.walletmapx.backend.dto.dashboard.YearlyNetWorthItem;

import java.util.List;

public interface DashboardService {

    DashboardResponse getDashboard(Long userId);

    MonthlyDashboardResponse getMonthlyDashboard(
            Long userId,
            String month
    );

    List<YearlyNetWorthItem> getYearlyNetWorth(
            Long userId
    );
}