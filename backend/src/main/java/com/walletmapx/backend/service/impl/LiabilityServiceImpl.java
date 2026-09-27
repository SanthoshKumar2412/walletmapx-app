package com.walletmapx.backend.service.impl;

import com.walletmapx.backend.dto.liability.LiabilityRequest;
import com.walletmapx.backend.dto.liability.LiabilityResponse;
import com.walletmapx.backend.entity.Liability;
import com.walletmapx.backend.exception.ResourceNotFoundException;
import com.walletmapx.backend.repository.LiabilityRepository;
import com.walletmapx.backend.service.LiabilityService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LiabilityServiceImpl implements LiabilityService {

    private final LiabilityRepository liabilityRepository;

    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public LiabilityResponse createLiability(
            Long userId,
            LiabilityRequest request) {

        Liability liability = new Liability();

        liability.setUserId(userId);

        mapRequestToEntity(request, liability);

        Liability savedLiability =
                liabilityRepository.save(liability);

        return mapToResponse(savedLiability);
    }

    // =========================================================
    // GET ALL USER LIABILITIES
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<LiabilityResponse> getUserLiabilities(
            Long userId) {

        return liabilityRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public LiabilityResponse getLiabilityById(
            Long userId,
            Long liabilityId) {

        Liability liability =
                liabilityRepository.findById(liabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Liability not found"
                                )
                        );

        validateOwnership(liability, userId);

        return mapToResponse(liability);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public LiabilityResponse updateLiability(
            Long userId,
            Long liabilityId,
            LiabilityRequest request) {

        Liability liability =
                liabilityRepository.findById(liabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Liability not found"
                                )
                        );

        validateOwnership(liability, userId);

        mapRequestToEntity(request, liability);

        Liability updatedLiability =
                liabilityRepository.save(liability);

        return mapToResponse(updatedLiability);
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteLiability(
            Long userId,
            Long liabilityId) {

        Liability liability =
                liabilityRepository.findById(liabilityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Liability not found"
                                )
                        );

        validateOwnership(liability, userId);

        liabilityRepository.delete(liability);
    }

    // =========================================================
    // OWNERSHIP VALIDATION
    // =========================================================

    private void validateOwnership(
            Liability liability,
            Long userId) {

        if (!liability.getUserId().equals(userId)) {

            /*
             * Return 404 instead of revealing that
             * the record belongs to another user.
             */
            throw new ResourceNotFoundException(
                    "Liability not found"
            );
        }
    }

    // =========================================================
    // REQUEST -> ENTITY MAPPING
    // =========================================================

    private void mapRequestToEntity(
            LiabilityRequest request,
            Liability liability) {

        liability.setName(request.getName());

        liability.setLiabilityType(
                request.getLiabilityType()
        );

        liability.setPrincipalAmount(
                request.getPrincipalAmount()
        );

        liability.setOutstandingAmount(
                request.getOutstandingAmount()
        );

        liability.setInterestRate(
                request.getInterestRate()
        );

        liability.setMonthlyEmi(
                request.getMonthlyEmi()
        );

        liability.setStartDate(
                request.getStartDate()
        );

        liability.setEndDate(
                request.getEndDate()
        );

        liability.setNotes(
                request.getNotes()
        );
    }

    // =========================================================
    // ENTITY -> RESPONSE MAPPING
    // =========================================================

    private LiabilityResponse mapToResponse(
            Liability liability) {

        return new LiabilityResponse(
                liability.getId(),
                liability.getUserId(),
                liability.getName(),
                liability.getLiabilityType(),
                liability.getPrincipalAmount(),
                liability.getOutstandingAmount(),
                liability.getInterestRate(),
                liability.getMonthlyEmi(),
                liability.getStartDate(),
                liability.getEndDate(),
                liability.getNotes(),
                liability.getCreatedAt(),
                liability.getUpdatedAt()
        );
    }
}