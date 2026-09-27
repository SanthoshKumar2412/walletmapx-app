package com.walletmapx.backend.controller;

import com.walletmapx.backend.dto.income.IncomeRequest;
import com.walletmapx.backend.dto.income.IncomeResponse;
import com.walletmapx.backend.service.IncomeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/income")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeResponse> createIncome(
            @Valid @RequestBody IncomeRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        IncomeResponse response =
                incomeService.createIncome(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<IncomeResponse>> getAllIncome(
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                incomeService.getAllIncome(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncomeResponse> getIncomeById(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                incomeService.getIncomeById(userId, id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        return ResponseEntity.ok(
                incomeService.updateIncome(
                        userId,
                        id,
                        request
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId = Long.parseLong(
                authentication.getName()
        );

        incomeService.deleteIncome(userId, id);

        return ResponseEntity.noContent().build();
    }
}