package com.walletmapx.backend;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
public class WalletmapxBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalletmapxBackendApplication.class, args);
    }

    /**
     * Set the JVM default timezone to India Standard Time.
     *
     * This ensures LocalDate.now(), YearMonth.now(), etc.
     * use Asia/Kolkata instead of the cloud server's default UTC timezone.
     */
    @PostConstruct
    void setDefaultTimeZone() {
        TimeZone.setDefault(
            TimeZone.getTimeZone("Asia/Kolkata")
        );
    }
}