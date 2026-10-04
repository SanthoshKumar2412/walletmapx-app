package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.dashboard.DashboardResponse;
import com.walletmapx.backend.dto.dashboard.MonthlyDashboardResponse;
import com.walletmapx.backend.dto.dashboard.YearlyNetWorthItem;

import java.util.List;

public interface DashboardService {

    /**
     * Dashboard for one month. Income/expenses are scoped to that
     * month; assets, liabilities, investments and net worth are
     * current point-in-time values. month = "YYYY-MM", null = current month.
     */
    DashboardResponse getDashboard(Long userId, String month);

    MonthlyDashboardResponse getMonthlyDashboard(
            Long userId,
            String month
    );

    List<YearlyNetWorthItem> getYearlyNetWorth(
            Long userId
    );
}