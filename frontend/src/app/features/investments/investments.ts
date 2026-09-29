import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  InvestmentRequest,
  InvestmentResponse,
  InvestmentService
} from '../../core/services/investment';

@Component({
  selector: 'app-investments',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './investments.html'
})
export class Investments implements OnInit {

  private readonly investmentService =
    inject(InvestmentService);

  investments: InvestmentResponse[] = [];

  editingInvestmentId: number | null = null;

  loading = true;
  submitting = false;

  errorMessage = '';
  successMessage = '';

  showForm = false;

  form: InvestmentRequest = {
    name: '',
    investmentType: '',
    quantity: 0,
    buyPrice: 0,
    investedAmount: 0,
    currentValue: null,
    investmentDate: this.getToday(),
    notes: ''
  };

  ngOnInit(): void {
    this.loadInvestments();
  }

  // =========================================================
  // LOAD INVESTMENTS
  // =========================================================

  loadInvestments(): void {

    this.loading = true;
    this.errorMessage = '';

    this.investmentService
      .getAllInvestments()
      .subscribe({

        next: (response) => {

          this.investments = response;

          this.loading = false;
        },

        error: (error) => {

          console.error(
            'Investments loading failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load investments.';

          this.loading = false;
        }

      });
  }

  // =========================================================
  // OPEN FORM
  // =========================================================

  openForm(): void {

    this.editingInvestmentId = null;

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

    this.editingInvestmentId = null;

    this.resetForm();

    this.errorMessage = '';
  }

  // =========================================================
  // EDIT
  // =========================================================

  editInvestment(
    investment: InvestmentResponse
  ): void {

    this.editingInvestmentId = investment.id;

    this.form = {
      name: investment.name,
      investmentType: investment.investmentType,
      quantity: investment.quantity,
      buyPrice: investment.buyPrice,
      investedAmount: investment.investedAmount,
      currentValue: investment.currentValue,
      investmentDate: investment.investmentDate || '',
      notes: investment.notes || ''
    };

    this.showForm = true;

    this.errorMessage = '';
    this.successMessage = '';
  }

  // =========================================================
  // SAVE
  // =========================================================

  saveInvestment(): void {

    this.errorMessage = '';
    this.successMessage = '';

    if (!this.validateForm()) {
      return;
    }

    this.submitting = true;

    // UPDATE
    if (this.editingInvestmentId !== null) {

      this.investmentService
        .updateInvestment(
          this.editingInvestmentId,
          this.form
        )
        .subscribe({

          next: (response) => {

            const index =
              this.investments.findIndex(
                investment =>
                  investment.id === response.id
              );

            if (index !== -1) {

              this.investments[index] =
                response;
            }

            this.investments =
              [...this.investments];

            this.submitting = false;

            this.showForm = false;

            this.editingInvestmentId = null;

            this.resetForm();

            this.successMessage =
              'Investment updated successfully.';
          },

          error: (error) => {

            console.error(
              'Investment update failed:',
              error
            );

            this.submitting = false;

            this.errorMessage =
              error?.error?.message ||
              'Unable to update investment.';
          }

        });

      return;
    }

    // CREATE
    this.investmentService
      .createInvestment(this.form)
      .subscribe({

        next: (response) => {

          this.investments = [
            response,
            ...this.investments
          ];

          this.submitting = false;

          this.showForm = false;

          this.resetForm();

          this.successMessage =
            'Investment added successfully.';
        },

        error: (error) => {

          console.error(
            'Investment creation failed:',
            error
          );

          this.submitting = false;

          this.errorMessage =
            error?.error?.message ||
            'Unable to add investment.';
        }

      });
  }

  // =========================================================
  // DELETE
  // =========================================================

  deleteInvestment(id: number): void {

    const confirmed =
      window.confirm(
        'Are you sure you want to delete this investment?'
      );

    if (!confirmed) {
      return;
    }

    this.investmentService
      .deleteInvestment(id)
      .subscribe({

        next: () => {

          this.investments =
            this.investments.filter(
              investment =>
                investment.id !== id
            );

          this.successMessage =
            'Investment deleted successfully.';
        },

        error: (error) => {

          console.error(
            'Investment deletion failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to delete investment.';
        }

      });
  }

  // =========================================================
  // VALIDATION
  // =========================================================

  private validateForm(): boolean {

    if (!this.form.name.trim()) {

      this.errorMessage =
        'Investment name is required.';

      return false;
    }

    if (!this.form.investmentType.trim()) {

      this.errorMessage =
        'Investment type is required.';

      return false;
    }

    if (
      !this.form.quantity ||
      this.form.quantity <= 0
    ) {

      this.errorMessage =
        'Quantity must be greater than 0.';

      return false;
    }

    if (
      !this.form.buyPrice ||
      this.form.buyPrice <= 0
    ) {

      this.errorMessage =
        'Buy price must be greater than 0.';

      return false;
    }

    if (
      !this.form.investedAmount ||
      this.form.investedAmount <= 0
    ) {

      this.errorMessage =
        'Invested amount must be greater than 0.';

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

      investmentType: '',

      quantity: 0,

      buyPrice: 0,

      investedAmount: 0,

      currentValue: null,

      investmentDate: this.getToday(),

      notes: ''
    };
  }

  // =========================================================
  // TODAY
  // =========================================================

  private getToday(): string {

    const today = new Date();

    return today
      .toISOString()
      .split('T')[0];
  }

  // =========================================================
  // TOTAL INVESTED
  // =========================================================

  get totalInvested(): number {

    return this.investments.reduce(
      (total, investment) =>
        total +
        Number(investment.investedAmount || 0),
      0
    );
  }

  // =========================================================
  // CURRENT VALUE
  // =========================================================

  get totalCurrentValue(): number {

    return this.investments.reduce(
      (total, investment) =>
        total +
        Number(investment.currentValue || 0),
      0
    );
  }

  // =========================================================
  // PROFIT / LOSS
  // =========================================================

  get totalProfitLoss(): number {

    return (
      this.totalCurrentValue -
      this.totalInvested
    );
  }

  // =========================================================
  // PROFIT / LOSS PERCENTAGE
  // =========================================================

  get profitLossPercentage(): number {

    if (this.totalInvested <= 0) {
      return 0;
    }

    return (
      (this.totalProfitLoss /
        this.totalInvested) *
      100
    );
  }
}