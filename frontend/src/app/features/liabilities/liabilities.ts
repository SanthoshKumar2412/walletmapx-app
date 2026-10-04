import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  LiabilityRequest,
  LiabilityResponse,
  LiabilityService
} from '../../core/services/liability';

@Component({
  selector: 'app-liabilities',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './liabilities.html'
})
export class Liabilities implements OnInit {

  private readonly liabilityService =
    inject(LiabilityService);

  liabilities: LiabilityResponse[] = [];

  editingLiabilityId: number | null = null;

  loading = true;
  submitting = false;

  errorMessage = '';
  successMessage = '';

  showForm = false;

  form: LiabilityRequest = {
    name: '',
    liabilityType: '',
    principalAmount: 0,
    outstandingAmount: 0,
    interestRate: 0,
    monthlyEmi: 0,
    startDate: '',
    endDate: '',
    notes: ''
  };

  ngOnInit(): void {
    this.loadLiabilities();
  }

  // =========================================================
  // LOAD LIABILITIES
  // =========================================================

  loadLiabilities(): void {

    this.loading = true;
    this.errorMessage = '';

    this.liabilityService
      .getAllLiabilities()
      .subscribe({

        next: (response) => {

          this.liabilities = response;

          this.loading = false;
        },

        error: (error) => {

          console.error(
            'Liabilities loading failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load liabilities.';

          this.loading = false;
        }

      });
  }

  // =========================================================
  // OPEN CREATE FORM
  // =========================================================

  openForm(): void {

    this.editingLiabilityId = null;

    this.resetForm();

    this.showForm = true;

    this.errorMessage = '';
    this.successMessage = '';
  }

  // =========================================================
  // CLOSE FORM
  // =========================================================

  closeForm(): void {

    this.showForm = false;

    this.editingLiabilityId = null;

    this.resetForm();

    this.errorMessage = '';
  }

  // =========================================================
  // EDIT
  // =========================================================

  editLiability(
    liability: LiabilityResponse
  ): void {

    this.editingLiabilityId = liability.id;

    this.form = {
      name: liability.name,
      liabilityType: liability.liabilityType,
      principalAmount: liability.principalAmount,
      outstandingAmount: liability.outstandingAmount,
      interestRate: liability.interestRate,
      monthlyEmi: liability.monthlyEmi,
      startDate: liability.startDate || '',
      endDate: liability.endDate || '',
      notes: liability.notes || ''
    };

    this.showForm = true;

    this.errorMessage = '';
    this.successMessage = '';
  }

  // =========================================================
  // SAVE
  // =========================================================

  saveLiability(): void {

    this.errorMessage = '';
    this.successMessage = '';

    if (!this.validateForm()) {
      return;
    }

    this.submitting = true;

    // UPDATE
    if (this.editingLiabilityId !== null) {

      this.liabilityService
        .updateLiability(
          this.editingLiabilityId,
          this.form
        )
        .subscribe({

          next: (response) => {

            const index =
              this.liabilities.findIndex(
                liability =>
                  liability.id === response.id
              );

            if (index !== -1) {

              this.liabilities[index] =
                response;
            }

            this.liabilities =
              [...this.liabilities];

            this.submitting = false;

            this.showForm = false;

            this.editingLiabilityId = null;

            this.resetForm();

            this.successMessage =
              'Liability updated successfully.';
          },

          error: (error) => {

            console.error(
              'Liability update failed:',
              error
            );

            this.submitting = false;

            this.errorMessage =
              error?.error?.message ||
              'Unable to update liability.';
          }

        });

      return;
    }

    // CREATE
    this.liabilityService
      .createLiability(this.form)
      .subscribe({

        next: (response) => {

          this.liabilities = [
            response,
            ...this.liabilities
          ];

          this.submitting = false;

          this.showForm = false;

          this.resetForm();

          this.successMessage =
            'Liability added successfully.';
        },

        error: (error) => {

          console.error(
            'Liability creation failed:',
            error
          );

          this.submitting = false;

          this.errorMessage =
            error?.error?.message ||
            'Unable to add liability.';
        }

      });
  }

  // =========================================================
  // DELETE
  // =========================================================

  deleteLiability(id: number): void {

    const confirmed =
      window.confirm(
        'Are you sure you want to delete this liability?'
      );

    if (!confirmed) {
      return;
    }

    this.liabilityService
      .deleteLiability(id)
      .subscribe({

        next: () => {

          this.liabilities =
            this.liabilities.filter(
              liability =>
                liability.id !== id
            );

          this.successMessage =
            'Liability deleted successfully.';
        },

        error: (error) => {

          console.error(
            'Liability deletion failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to delete liability.';
        }

      });
  }

  // =========================================================
  // VALIDATION
  // =========================================================

  private validateForm(): boolean {

    if (!this.form.name.trim()) {
      this.errorMessage =
        'Liability name is required.';
      return false;
    }

    if (!this.form.liabilityType.trim()) {
      this.errorMessage =
        'Liability type is required.';
      return false;
    }

    if (
      !this.form.principalAmount ||
      this.form.principalAmount <= 0
    ) {
      this.errorMessage =
        'Principal amount must be greater than 0.';
      return false;
    }

    if (
      this.form.outstandingAmount == null ||
      this.form.outstandingAmount < 0
    ) {
      this.errorMessage =
        'Outstanding amount cannot be negative.';
      return false;
    }

    if (
      this.form.interestRate == null ||
      this.form.interestRate < 0
    ) {
      this.errorMessage =
        'Interest rate cannot be negative.';
      return false;
    }

    if (
      this.form.monthlyEmi == null ||
      this.form.monthlyEmi < 0
    ) {
      this.errorMessage =
        'Monthly EMI cannot be negative.';
      return false;
    }

    return true;
  }

  // =========================================================
  // RESET FORM
  // =========================================================

  private resetForm(): void {

    this.form = {

      name: '',

      liabilityType: '',

      principalAmount: 0,

      outstandingAmount: 0,

      interestRate: 0,

      monthlyEmi: 0,

      startDate: '',

      endDate: '',

      notes: ''
    };
  }

  // =========================================================
  // TOTAL OUTSTANDING
  // =========================================================

  get totalOutstanding(): number {

    return this.liabilities.reduce(
      (total, liability) =>
        total +
        Number(
          liability.outstandingAmount || 0
        ),
      0
    );
  }

  // =========================================================
  // TOTAL EMI - ACTIVE LOANS ONLY
  // =========================================================

  /**
   * Paid-off loans (outstanding = 0) no longer have
   * an EMI to pay, so they must not be counted.
   */
  get totalMonthlyEmi(): number {

    return this.liabilities
      .filter(
        liability =>
          Number(
            liability.outstandingAmount || 0
          ) > 0
      )
      .reduce(
        (total, liability) =>
          total +
          Number(
            liability.monthlyEmi || 0
          ),
        0
      );
  }

  // =========================================================
  // TOTAL PRINCIPAL
  // =========================================================

  get totalPrincipal(): number {

    return this.liabilities.reduce(
      (total, liability) =>
        total +
        Number(
          liability.principalAmount || 0
        ),
      0
    );
  }
}