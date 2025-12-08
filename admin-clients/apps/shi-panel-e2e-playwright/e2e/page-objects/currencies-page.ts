import { Locator, Page, expect } from '@playwright/test';

export class CurrenciesPage {
    readonly page: Page;
    readonly currencyHeaderRow: Locator;
    readonly currencyRow: Locator;

    constructor(page: Page) {
        this.page = page;
        this.currencyHeaderRow = page.locator('mat-header-row');
        this.currencyRow = page.locator('mat-row');
    }

    async checkElements(): Promise<Page> {
        await expect(this.currencyHeaderRow).toBeVisible();
        await expect(this.currencyRow.first()).toBeVisible();

        return this.page;
    }
}
