import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  ExpenseRequest,
  ExpenseResponse,
  ExpenseService
} from '../../core/services/expense';

@Component({
  selector: 'app-expenses',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './expenses.html'
})
export class Expenses implements OnInit {

  private readonly expenseService =
    inject(ExpenseService);

  expenses: ExpenseResponse[] = [];

  editingExpenseId: number | null = null;

  loading = true;
  submitting = false;

  errorMessage = '';
  successMessage = '';

  showForm = false;

  form: ExpenseRequest = {
    category: '',
    amount: 0,
    expenseDate: this.getToday(),
    description: ''
  };

  ngOnInit(): void {
    this.loadExpenses();
  }

  // =========================================================
  // LOAD EXPENSES
  // =========================================================

  loadExpenses(): void {

    this.loading = true;
    this.errorMessage = '';

    this.expenseService
      .getAllExpenses()
      .subscribe({

        next: (response) => {

          this.expenses = response;

          this.loading = false;
        },

        error: (error) => {

          console.error(
            'Expense loading failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load expenses.';

          this.loading = false;
        }

      });
  }

  // =========================================================
  // OPEN FORM
  // =========================================================

  openForm(): void {

    this.editingExpenseId = null;

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

    this.editingExpenseId = null;

    this.resetForm();

    this.errorMessage = '';
  }

  // =========================================================
  // EDIT EXPENSE
  // =========================================================

  editExpense(
    expense: ExpenseResponse
  ): void {

    this.editingExpenseId = expense.id;

    this.form = {
      category: expense.category,
      amount: expense.amount,
      expenseDate: expense.expenseDate,
      description: expense.description || ''
    };

    this.showForm = true;

    this.errorMessage = '';
    this.successMessage = '';
  }

  // =========================================================
  // SAVE EXPENSE
  // =========================================================

  saveExpense(): void {

    this.errorMessage = '';
    this.successMessage = '';

    // Validation
    if (!this.form.category.trim()) {

      this.errorMessage =
        'Category is required.';

      return;
    }

    if (
      !this.form.amount ||
      this.form.amount <= 0
    ) {

      this.errorMessage =
        'Amount must be greater than 0.';

      return;
    }

    if (!this.form.expenseDate) {

      this.errorMessage =
        'Expense date is required.';

      return;
    }

    this.submitting = true;

    // =======================================================
    // UPDATE
    // =======================================================

    if (this.editingExpenseId !== null) {

      this.expenseService
        .updateExpense(
          this.editingExpenseId,
          this.form
        )
        .subscribe({

          next: (response) => {

            const index =
              this.expenses.findIndex(
                expense =>
                  expense.id === response.id
              );

            if (index !== -1) {

              this.expenses[index] =
                response;
            }

            this.expenses =
              [...this.expenses];

            this.submitting = false;

            this.showForm = false;

            this.editingExpenseId = null;

            this.resetForm();

            this.successMessage =
              'Expense updated successfully.';
          },

          error: (error) => {

            console.error(
              'Expense update failed:',
              error
            );

            this.submitting = false;

            this.errorMessage =
              error?.error?.message ||
              'Unable to update expense.';
          }

        });

      return;
    }

    // =======================================================
    // CREATE
    // =======================================================

    this.expenseService
      .createExpense(this.form)
      .subscribe({

        next: (response) => {

          this.expenses = [
            response,
            ...this.expenses
          ];

          this.submitting = false;

          this.showForm = false;

          this.resetForm();

          this.successMessage =
            'Expense added successfully.';
        },

        error: (error) => {

          console.error(
            'Expense creation failed:',
            error
          );

          this.submitting = false;

          this.errorMessage =
            error?.error?.message ||
            'Unable to add expense.';
        }

      });
  }

  // =========================================================
  // DELETE EXPENSE
  // =========================================================

  deleteExpense(id: number): void {

    const confirmed =
      window.confirm(
        'Are you sure you want to delete this expense?'
      );

    if (!confirmed) {
      return;
    }

    this.expenseService
      .deleteExpense(id)
      .subscribe({

        next: () => {

          this.expenses =
            this.expenses.filter(
              expense =>
                expense.id !== id
            );

          this.successMessage =
            'Expense deleted successfully.';
        },

        error: (error) => {

          console.error(
            'Expense deletion failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to delete expense.';
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

      expenseDate:
        this.getToday(),

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