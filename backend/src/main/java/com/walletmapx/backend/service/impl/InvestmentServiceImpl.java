package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.investment.InvestmentRequest;
import com.walletmapx.backend.dto.investment.InvestmentResponse;
import com.walletmapx.backend.entity.Investment;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.InvestmentRepository;
import com.walletmapx.backend.service.InvestmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestmentServiceImpl implements InvestmentService {

    private final InvestmentRepository investmentRepository;

    @Override
    public InvestmentResponse createInvestment(
            Long userId,
            InvestmentRequest request) {

        Investment investment = new Investment();

        investment.setUserId(userId);
        investment.setName(request.getName());
        investment.setInvestmentType(request.getInvestmentType());
        investment.setQuantity(request.getQuantity());
        investment.setBuyPrice(request.getBuyPrice());
        investment.setInvestedAmount(
                request.getInvestedAmount() != null
                        ? request.getInvestedAmount()
                        : BigDecimal.ZERO
        );
        // Blank current value -> assume it is worth what was invested,
        // otherwise net worth silently drops by the full amount.
        investment.setCurrentValue(
                request.getCurrentValue() != null
                        ? request.getCurrentValue()
                        : investment.getInvestedAmount()
        );
        investment.setInvestmentDate(request.getInvestmentDate());
        investment.setNotes(request.getNotes());

        Investment savedInvestment =
                investmentRepository.save(investment);

        return mapToResponse(savedInvestment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestmentResponse> getAllInvestments(
            Long userId) {

        return investmentRepository
                .findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvestmentResponse getInvestmentById(
            Long userId,
            Long investmentId) {

        Investment investment = getUserInvestment(
                userId,
                investmentId
        );

        return mapToResponse(investment);
    }

    @Override
    public InvestmentResponse updateInvestment(
            Long userId,
            Long investmentId,
            InvestmentRequest request) {

        Investment investment = getUserInvestment(
                userId,
                investmentId
        );

        investment.setName(request.getName());
        investment.setInvestmentType(request.getInvestmentType());
        investment.setQuantity(request.getQuantity());
        investment.setBuyPrice(request.getBuyPrice());

        if (request.getInvestedAmount() != null) {
            investment.setInvestedAmount(
                    request.getInvestedAmount()
            );
        }

        // Blank current value -> fall back to the invested amount
        investment.setCurrentValue(
                request.getCurrentValue() != null
                        ? request.getCurrentValue()
                        : investment.getInvestedAmount()
        );

        investment.setInvestmentDate(
                request.getInvestmentDate()
        );

        investment.setNotes(request.getNotes());

        Investment updatedInvestment =
                investmentRepository.save(investment);

        return mapToResponse(updatedInvestment);
    }

    @Override
    public void deleteInvestment(
            Long userId,
            Long investmentId) {

        Investment investment = getUserInvestment(
                userId,
                investmentId
        );

        investmentRepository.delete(investment);
    }

    private Investment getUserInvestment(
            Long userId,
            Long investmentId) {

        Investment investment =
                investmentRepository.findById(investmentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Investment not found with id: "
                                                + investmentId
                                )
                        );

        if (!investment.getUserId().equals(userId)) {
            throw new ResourceNotFoundException(
                    "Investment not found with id: "
                            + investmentId
            );
        }

        return investment;
    }

    private InvestmentResponse mapToResponse(
            Investment investment) {

        return new InvestmentResponse(
                investment.getId(),
                investment.getUserId(),
                investment.getName(),
                investment.getInvestmentType(),
                investment.getQuantity(),
                investment.getBuyPrice(),
                investment.getInvestedAmount(),
                investment.getCurrentValue(),
                investment.getInvestmentDate(),
                investment.getNotes(),
                investment.getCreatedAt(),
                investment.getUpdatedAt()
        );
    }
}