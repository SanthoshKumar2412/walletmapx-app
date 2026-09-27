package com.walletmapx.backend.service;

import com.walletmapx.backend.dto.dashboard.DashboardResponse;
import com.walletmapx.backend.dto.dashboard.MonthlyDashboardResponse;

public interface DashboardService {

    DashboardResponse getDashboard(Long userId);

    MonthlyDashboardResponse getMonthlyDashboard(
            Long userId,
            String month
    );
}