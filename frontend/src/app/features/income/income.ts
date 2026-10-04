// src/app/features/income/income.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  IncomeRequest,
  IncomeResponse,
  IncomeService
} from '../../core/services/income';

import {
  currentMonth,
  formatMonthLabel,
  monthOf,
  todayLocal
} from '../../core/utils/date';
import { MonthPicker } from '../../shared/month-picker/month-picker';

@Component({
  selector: 'app-income',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MonthPicker
],
  templateUrl: './income.html'
})
export class Income implements OnInit {

  private readonly incomeService =
    inject(IncomeService);

    

  /** Every record from the server (all months). */
  allIncomes: IncomeResponse[] = [];

  /**
   * Month filter (YYYY-MM). Defaults to the current month so a new
   * month starts from zero. Clear the field to see all months.
   */
  selectedMonth = currentMonth();

  editingIncomeId: number | null = null;

  loading = true;
  submitting = false;

  errorMessage = '';
  successMessage = '';

  showForm = false;

  form: IncomeRequest = {
    category: '',
    amount: 0,
    incomeDate: todayLocal(),
    description: ''
  };

  // =========================================================
  // VIEW DATA (the template keeps using `incomes`)
  // =========================================================

  /** Records of the selected month, newest first. */
  get incomes(): IncomeResponse[] {

    const list = this.selectedMonth
      ? this.allIncomes.filter(
          item => monthOf(item.incomeDate) === this.selectedMonth
        )
      : this.allIncomes;

    return [...list].sort(
      (a, b) => b.incomeDate.localeCompare(a.incomeDate)
    );
  }

  /** Total of the selected month (or all time if month is cleared). */
  get monthTotal(): number {

    return this.incomes.reduce(
      (total, item) => total + Number(item.amount || 0),
      0
    );
  }

  get monthLabel(): string {
    return formatMonthLabel(this.selectedMonth);
  }

  // =========================================================
  // INIT
  // =========================================================

  ngOnInit(): void {
    this.loadIncomes();
  }

  // =========================================================
  // LOAD
  // =========================================================

  loadIncomes(): void {

    this.loading = true;
    this.errorMessage = '';

    this.incomeService.getAllIncome().subscribe({

      next: (response) => {
        this.allIncomes = response;
        this.loading = false;
      },

      error: (error) => {
        console.error('Income loading failed:', error);

        this.errorMessage =
          error?.error?.message || 'Unable to load income.';

        this.loading = false;
      }
    });
  }

  // =========================================================
  // FORM
  // =========================================================

  openForm(): void {

    this.editingIncomeId = null;

    this.resetForm();

    this.showForm = true;

    this.errorMessage = '';
    this.successMessage = '';
  }

  closeForm(): void {

    this.showForm = false;

    this.editingIncomeId = null;

    this.resetForm();

    this.errorMessage = '';
  }

  editIncome(item: IncomeResponse): void {

    this.editingIncomeId = item.id;

    this.form = {
      category: item.category,
      amount: item.amount,
      incomeDate: item.incomeDate,
      description: item.description || ''
    };

    this.showForm = true;

    this.errorMessage = '';
    this.successMessage = '';

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // =========================================================
  // SAVE (CREATE / UPDATE)
  // =========================================================

  saveIncome(): void {

    this.errorMessage = '';
    this.successMessage = '';

    if (!this.form.category.trim()) {
      this.errorMessage = 'Category is required.';
      return;
    }

    if (!this.form.amount || this.form.amount <= 0) {
      this.errorMessage = 'Amount must be greater than 0.';
      return;
    }

    if (!this.form.incomeDate) {
      this.errorMessage = 'Income date is required.';
      return;
    }

    this.submitting = true;

    // ---------- UPDATE ----------
    if (this.editingIncomeId !== null) {

      this.incomeService
        .updateIncome(this.editingIncomeId, this.form)
        .subscribe({

          next: (response) => {

            this.allIncomes = this.allIncomes.map(
              item => item.id === response.id ? response : item
            );

            this.followMonthOf(response.incomeDate);

            this.submitting = false;
            this.showForm = false;
            this.editingIncomeId = null;

            this.resetForm();

            this.successMessage = 'Income updated successfully.';
          },

          error: (error) => {
            console.error('Income update failed:', error);

            this.submitting = false;

            this.errorMessage =
              error?.error?.message || 'Unable to update income.';
          }
        });

      return;
    }

    // ---------- CREATE ----------
    this.incomeService
      .createIncome(this.form)
      .subscribe({

        next: (response) => {

          this.allIncomes = [response, ...this.allIncomes];

          this.followMonthOf(response.incomeDate);

          this.submitting = false;
          this.showForm = false;

          this.resetForm();

          this.successMessage = 'Income added successfully.';
        },

        error: (error) => {
          console.error('Income creation failed:', error);

          this.submitting = false;

          this.errorMessage =
            error?.error?.message || 'Unable to add income.';
        }
      });
  }

  // =========================================================
  // DELETE
  // =========================================================

  deleteIncome(id: number): void {

    const confirmed = window.confirm(
      'Are you sure you want to delete this income?'
    );

    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.incomeService.deleteIncome(id).subscribe({

      next: () => {
        this.allIncomes = this.allIncomes.filter(item => item.id !== id);
        this.successMessage = 'Income deleted successfully.';
      },

      error: (error) => {
        console.error('Income deletion failed:', error);

        this.errorMessage =
          error?.error?.message || 'Unable to delete income.';
      }
    });
  }

  // =========================================================
  // HELPERS
  // =========================================================

  /**
   * If the month filter is active and the saved record belongs to
   * another month, jump to that month so the record does not seem
   * to vanish.
   */
  private followMonthOf(date: string): void {

    if (this.selectedMonth) {
      this.selectedMonth = monthOf(date);
    }
  }

  private resetForm(): void {

    this.form = {
      category: '',
      amount: 0,
      incomeDate: todayLocal(),
      description: ''
    };
  }
}