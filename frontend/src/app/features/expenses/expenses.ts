// src/app/features/expenses/expenses.ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  ExpenseRequest,
  ExpenseResponse,
  ExpenseService
} from '../../core/services/expense';

import {
  currentMonth,
  formatMonthLabel,
  monthOf,
  todayLocal
} from '../../core/utils/date';
import { MonthPicker } from '../../shared/month-picker/month-picker';

@Component({
  selector: 'app-expenses',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MonthPicker
],
  templateUrl: './expenses.html'
})
export class Expenses implements OnInit {

  private readonly expenseService =
    inject(ExpenseService);

  /** Every record from the server (all months). */
  allExpenses: ExpenseResponse[] = [];

  /**
   * Month filter (YYYY-MM). Defaults to the current month so a new
   * month starts from zero. Clear the field to see all months.
   */
  selectedMonth = currentMonth();

  editingExpenseId: number | null = null;

  loading = true;
  submitting = false;

  errorMessage = '';
  successMessage = '';

  showForm = false;

  form: ExpenseRequest = {
    category: '',
    amount: 0,
    expenseDate: todayLocal(),
    description: ''
  };

  // =========================================================
  // VIEW DATA (the template keeps using `expenses`)
  // =========================================================

  /** Records of the selected month, newest first. */
  get expenses(): ExpenseResponse[] {

    const list = this.selectedMonth
      ? this.allExpenses.filter(
          item => monthOf(item.expenseDate) === this.selectedMonth
        )
      : this.allExpenses;

    return [...list].sort(
      (a, b) => b.expenseDate.localeCompare(a.expenseDate)
    );
  }

  /** Total of the selected month (or all time if month is cleared). */
  get monthTotal(): number {

    return this.expenses.reduce(
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
    this.loadExpenses();
  }

  // =========================================================
  // LOAD
  // =========================================================

  loadExpenses(): void {

    this.loading = true;
    this.errorMessage = '';

    this.expenseService.getAllExpenses().subscribe({

      next: (response) => {
        this.allExpenses = response;
        this.loading = false;
      },

      error: (error) => {
        console.error('Expense loading failed:', error);

        this.errorMessage =
          error?.error?.message || 'Unable to load expenses.';

        this.loading = false;
      }
    });
  }

  // =========================================================
  // FORM
  // =========================================================

  openForm(): void {

    this.editingExpenseId = null;

    this.resetForm();

    this.showForm = true;

    this.errorMessage = '';
    this.successMessage = '';
  }

  closeForm(): void {

    this.showForm = false;

    this.editingExpenseId = null;

    this.resetForm();

    this.errorMessage = '';
  }

  editExpense(item: ExpenseResponse): void {

    this.editingExpenseId = item.id;

    this.form = {
      category: item.category,
      amount: item.amount,
      expenseDate: item.expenseDate,
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

  saveExpense(): void {

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

    if (!this.form.expenseDate) {
      this.errorMessage = 'Expense date is required.';
      return;
    }

    this.submitting = true;

    // ---------- UPDATE ----------
    if (this.editingExpenseId !== null) {

      this.expenseService
        .updateExpense(this.editingExpenseId, this.form)
        .subscribe({

          next: (response) => {

            this.allExpenses = this.allExpenses.map(
              item => item.id === response.id ? response : item
            );

            this.followMonthOf(response.expenseDate);

            this.submitting = false;
            this.showForm = false;
            this.editingExpenseId = null;

            this.resetForm();

            this.successMessage = 'Expense updated successfully.';
          },

          error: (error) => {
            console.error('Expense update failed:', error);

            this.submitting = false;

            this.errorMessage =
              error?.error?.message || 'Unable to update expense.';
          }
        });

      return;
    }

    // ---------- CREATE ----------
    this.expenseService
      .createExpense(this.form)
      .subscribe({

        next: (response) => {

          this.allExpenses = [response, ...this.allExpenses];

          this.followMonthOf(response.expenseDate);

          this.submitting = false;
          this.showForm = false;

          this.resetForm();

          this.successMessage = 'Expense added successfully.';
        },

        error: (error) => {
          console.error('Expense creation failed:', error);

          this.submitting = false;

          this.errorMessage =
            error?.error?.message || 'Unable to add expense.';
        }
      });
  }

  // =========================================================
  // DELETE
  // =========================================================

  deleteExpense(id: number): void {

    const confirmed = window.confirm(
      'Are you sure you want to delete this expense?'
    );

    if (!confirmed) {
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';

    this.expenseService.deleteExpense(id).subscribe({

      next: () => {
        this.allExpenses = this.allExpenses.filter(item => item.id !== id);
        this.successMessage = 'Expense deleted successfully.';
      },

      error: (error) => {
        console.error('Expense deletion failed:', error);

        this.errorMessage =
          error?.error?.message || 'Unable to delete expense.';
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
      expenseDate: todayLocal(),
      description: ''
    };
  }
}