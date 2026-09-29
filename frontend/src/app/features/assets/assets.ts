import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  AssetRequest,
  AssetResponse,
  AssetService
} from '../../core/services/asset';

import {
  AssetCategoryResponse,
  AssetCategoryService
} from '../../core/services/asset-category';

@Component({
  selector: 'app-assets',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './assets.html'
})
export class Assets implements OnInit {

  private readonly assetService =
    inject(AssetService);

  private readonly categoryService =
    inject(AssetCategoryService);

  // =========================================================
  // DATA
  // =========================================================

  assets: AssetResponse[] = [];

  categories: AssetCategoryResponse[] = [];

  // =========================================================
  // STATE
  // =========================================================

  loading = true;
  categoriesLoading = true;

  submitting = false;

  showForm = false;

  editingAssetId: number | null = null;

  errorMessage = '';
  successMessage = '';

  // =========================================================
  // FORM
  // =========================================================

  form: AssetRequest = {
    categoryId: null,
    name: '',
    institution: '',
    investedAmount: 0,
    currentValue: 0,
    purchaseDate: this.getToday(),
    notes: ''
  };

  // =========================================================
  // INIT
  // =========================================================

  ngOnInit(): void {

    this.loadAssets();

    this.loadCategories();
  }

  // =========================================================
  // LOAD ASSETS
  // =========================================================

  loadAssets(): void {

    this.loading = true;

    this.errorMessage = '';

    this.assetService
      .getAllAssets()
      .subscribe({

        next: (response) => {

          this.assets = response;

          this.loading = false;
        },

        error: (error) => {

          console.error(
            'Asset loading failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to load assets.';

          this.loading = false;
        }

      });
  }

  // =========================================================
  // LOAD CATEGORIES
  // =========================================================

  loadCategories(): void {

    this.categoriesLoading = true;

    this.categoryService
      .getAllCategories()
      .subscribe({

        next: (response) => {

          this.categories = response;

          this.categoriesLoading = false;
        },

        error: (error) => {

          console.error(
            'Asset categories loading failed:',
            error
          );

          this.categoriesLoading = false;

          this.errorMessage =
            error?.error?.message ||
            'Unable to load asset categories.';
        }

      });
  }

  // =========================================================
  // OPEN CREATE FORM
  // =========================================================

  openForm(): void {

    this.editingAssetId = null;

    this.resetForm();

    this.showForm = true;

    this.clearMessages();
  }

  // =========================================================
  // OPEN EDIT FORM
  // =========================================================

  editAsset(asset: AssetResponse): void {

    this.editingAssetId = asset.id;

    this.form = {

      categoryId: asset.categoryId,

      name: asset.name,

      institution:
        asset.institution || '',

      investedAmount:
        asset.investedAmount ?? 0,

      currentValue:
        asset.currentValue ?? 0,

      purchaseDate:
        asset.purchaseDate || this.getToday(),

      notes:
        asset.notes || ''
    };

    this.showForm = true;

    this.clearMessages();
  }

  // =========================================================
  // CLOSE FORM
  // =========================================================

  closeForm(): void {

    this.showForm = false;

    this.editingAssetId = null;

    this.resetForm();

    this.clearMessages();
  }

  // =========================================================
  // SAVE ASSET
  // =========================================================

  saveAsset(): void {

    this.clearMessages();

    if (!this.validateForm()) {
      return;
    }

    this.submitting = true;

    // =======================================================
    // UPDATE
    // =======================================================

    if (this.editingAssetId !== null) {

      this.assetService
        .updateAsset(
          this.editingAssetId,
          this.form
        )
        .subscribe({

          next: (response) => {

            const index =
              this.assets.findIndex(
                asset =>
                  asset.id === response.id
              );

            if (index !== -1) {

              this.assets[index] =
                response;

              this.assets =
                [...this.assets];
            }

            this.submitting = false;

            this.showForm = false;

            this.editingAssetId = null;

            this.resetForm();

            this.successMessage =
              'Asset updated successfully.';
          },

          error: (error) => {

            console.error(
              'Asset update failed:',
              error
            );

            this.submitting = false;

            this.errorMessage =
              error?.error?.message ||
              'Unable to update asset.';
          }

        });

      return;
    }

    // =======================================================
    // CREATE
    // =======================================================

    this.assetService
      .createAsset(this.form)
      .subscribe({

        next: (response) => {

          this.assets = [
            response,
            ...this.assets
          ];

          this.submitting = false;

          this.showForm = false;

          this.resetForm();

          this.successMessage =
            'Asset added successfully.';
        },

        error: (error) => {

          console.error(
            'Asset creation failed:',
            error
          );

          this.submitting = false;

          this.errorMessage =
            error?.error?.message ||
            'Unable to add asset.';
        }

      });
  }

  // =========================================================
  // DELETE ASSET
  // =========================================================

  deleteAsset(id: number): void {

    const confirmed =
      window.confirm(
        'Are you sure you want to delete this asset?'
      );

    if (!confirmed) {
      return;
    }

    this.assetService
      .deleteAsset(id)
      .subscribe({

        next: () => {

          this.assets =
            this.assets.filter(
              asset => asset.id !== id
            );

          this.successMessage =
            'Asset deleted successfully.';
        },

        error: (error) => {

          console.error(
            'Asset deletion failed:',
            error
          );

          this.errorMessage =
            error?.error?.message ||
            'Unable to delete asset.';
        }

      });
  }

  // =========================================================
  // VALIDATION
  // =========================================================

  private validateForm(): boolean {

    if (!this.form.name.trim()) {

      this.errorMessage =
        'Asset name is required.';

      return false;
    }

    if (
      this.form.investedAmount < 0
    ) {

      this.errorMessage =
        'Invested amount cannot be negative.';

      return false;
    }

    if (
      this.form.currentValue < 0
    ) {

      this.errorMessage =
        'Current value cannot be negative.';

      return false;
    }

    if (!this.form.purchaseDate) {

      this.errorMessage =
        'Purchase date is required.';

      return false;
    }

    return true;
  }

  // =========================================================
  // RESET FORM
  // =========================================================

  private resetForm(): void {

    this.form = {

      categoryId: null,

      name: '',

      institution: '',

      investedAmount: 0,

      currentValue: 0,

      purchaseDate:
        this.getToday(),

      notes: ''
    };
  }

  // =========================================================
  // CLEAR MESSAGES
  // =========================================================

  private clearMessages(): void {

    this.errorMessage = '';

    this.successMessage = '';
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
  // CATEGORY NAME
  // =========================================================

  getCategoryName(
    categoryId: number | null
  ): string {

    if (!categoryId) {
      return 'Uncategorized';
    }

    const category =
      this.categories.find(
        item => item.id === categoryId
      );

    return category?.name ||
      'Uncategorized';
  }

  // =========================================================
  // PROFIT / LOSS
  // =========================================================

  getProfitLoss(
    asset: AssetResponse
  ): number {

    return (
      asset.currentValue -
      asset.investedAmount
    );
  }

  // =========================================================
  // TOTAL INVESTED
  // =========================================================

  get totalInvested(): number {

    return this.assets.reduce(
      (total, asset) =>
        total + (asset.investedAmount || 0),
      0
    );
  }

  // =========================================================
  // TOTAL CURRENT VALUE
  // =========================================================

  get totalCurrentValue(): number {

    return this.assets.reduce(
      (total, asset) =>
        total + (asset.currentValue || 0),
      0
    );
  }

  // =========================================================
  // TOTAL PROFIT / LOSS
  // =========================================================

  get totalProfitLoss(): number {

    return (
      this.totalCurrentValue -
      this.totalInvested
    );
  }
}