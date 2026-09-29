import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  IncomeRequest,
  IncomeResponse,
  IncomeService
} from '../../core/services/income';

@Component({
  selector: 'app-income',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './income.html'
})
export class Income implements OnInit {

  private readonly incomeService =
    inject(IncomeService);

  incomes: IncomeResponse[] = [];

  editingIncomeId: number | null = null;

  loading = true;
  submitting = false;

  errorMessage = '';
  successMessage = '';

  showForm = false;

  form: IncomeRequest = {
    category: '',
    amount: 0,
    incomeDate: this.getToday(),
    description: ''
  };

  // =========================================================
  // INIT
  // =========================================================

  ngOnInit(): void {
    this.loadIncome();
  }

  // =========================================================
  // LOAD INCOME
  // =========================================================

  loadIncome(): void {

    this.loading = true;
    this.errorMessage = '';

    this.incomeService.getAllIncome().subscribe({

      next: (response) => {

        this.incomes = response;

        this.loading = false;
      },

      error: (error) => {

        console.error(
          'Income loading failed:',
          error
        );

        this.errorMessage =
          error?.error?.message ||
          'Unable to load income.';

        this.loading = false;
      }

    });
  }

  // =========================================================
  // OPEN ADD FORM
  // =========================================================

  openForm(): void {

    this.editingIncomeId = null;

    this.resetForm();

    this.showForm = true;

    this.successMessage = '';
    this.errorMessage = '';
  }

  // =========================================================
  // EDIT INCOME
  // =========================================================

  editIncome(income: IncomeResponse): void {

    this.editingIncomeId = income.id;

    this.form = {
      category: income.category,
      amount: income.amount,
      incomeDate: income.incomeDate,
      description: income.description || ''
    };

    this.showForm = true;

    this.errorMessage = '';
    this.successMessage = '';

    // Scroll to form
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }

  // =========================================================
  // CLOSE FORM
  // =========================================================

  closeForm(): void {

    this.showForm = false;

    this.editingIncomeId = null;

    this.resetForm();

    this.errorMessage = '';
  }

  // =========================================================
  // SAVE INCOME
  // CREATE / UPDATE
  // =========================================================

  saveIncome(): void {

    this.errorMessage = '';
    this.successMessage = '';

    // Category validation
    if (!this.form.category.trim()) {

      this.errorMessage =
        'Category is required.';

      return;
    }

    // Amount validation
    if (
      !this.form.amount ||
      this.form.amount <= 0
    ) {

      this.errorMessage =
        'Amount must be greater than 0.';

      return;
    }

    // Date validation
    if (!this.form.incomeDate) {

      this.errorMessage =
        'Income date is required.';

      return;
    }

    this.submitting = true;

    // =======================================================
    // UPDATE
    // =======================================================

    if (this.editingIncomeId !== null) {

      this.incomeService
        .updateIncome(
          this.editingIncomeId,
          this.form
        )
        .subscribe({

          next: (response) => {

            const index =
              this.incomes.findIndex(
                income =>
                  income.id === response.id
              );

            if (index !== -1) {

              this.incomes[index] =
                response;
            }

            // Trigger Angular update
            this.incomes = [
              ...this.incomes
            ];

            this.submitting = false;

            this.showForm = false;

            this.editingIncomeId = null;

            this.resetForm();

            this.successMessage =
              'Income updated successfully.';
          },

          error: (error) => {

            console.error(
              'Income update failed:',
              error
            );

            this.submitting = false;

            this.errorMessage =
              error?.error?.message ||
              'Unable to update income.';
          }

        });

      return;
    }

    // =======================================================
    // CREATE
    // =======================================================

    this.incomeService
      .createIncome(this.form)
      .subscribe({

        next: (response) => {

          this.incomes = [
            response,
            ...this.incomes
          ];

          this.submitting = false;

          this.showForm = false;

          this.resetForm();

          this.successMessage =
            'Income added successfully.';
        },

        error: (error) => {

          console.error(
            'Income creation failed:',
            error
          );

          this.submitting = false;

          this.errorMessage =
            error?.error?.message ||
            'Unable to add income.';
        }

      });
  }

  // =========================================================
  // DELETE INCOME
  // =========================================================

  deleteIncome(id: number): void {

    const confirmed =
      window.confirm(
        'Are you sure you want to delete this income?'
      );

    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.incomeService
      .deleteIncome(id)
      .subscribe({

        next: () => {

          this.incomes =
            this.incomes.filter(
              income =>
                income.id !== id
            );

          this.successMessage =
            'Income deleted successfully.';
        },

        error: (error) => {

          console.error(
            'Income deletion failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to delete income.';
        }

      });
  }

  // =========================================================
  // RESET FORM
  // =========================================================

  private resetForm(): void {

    this.form = {

      category: '',

      amount: 0,

      incomeDate: this.getToday(),

      description: ''
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
}