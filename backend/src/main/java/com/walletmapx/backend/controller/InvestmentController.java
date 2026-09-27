package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.investment.InvestmentRequest;
import com.walletmapx.backend.dto.investment.InvestmentResponse;
import com.walletmapx.backend.service.InvestmentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
public class InvestmentController {

    private final InvestmentService investmentService;

    @PostMapping
    public ResponseEntity<InvestmentResponse> createInvestment(
            @Valid @RequestBody InvestmentRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        InvestmentResponse response =
                investmentService.createInvestment(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<InvestmentResponse>> getAllInvestments(
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                investmentService.getAllInvestments(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentResponse> getInvestmentById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                investmentService.getInvestmentById(
                        userId,
                        id
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestmentResponse> updateInvestment(
            @PathVariable Long id,
            @Valid @RequestBody InvestmentRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                investmentService.updateInvestment(
                        userId,
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvestment(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        investmentService.deleteInvestment(
                userId,
                id
        );

        return ResponseEntity.noContent().build();
    }
}