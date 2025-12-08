import { Locator, Page, expect } from '@playwright/test';

export class SalesPage {
    readonly page: Page;
    readonly aggreatedData: Locator;
    readonly saleHeaderRow: Locator;
    readonly saleRow: Locator;

    constructor(page: Page) {
        this.page = page;
        this.aggreatedData = page.locator('app-aggregated-data');
        this.saleHeaderRow = page.locator('mat-header-row');
        this.saleRow = page.locator('mat-row');
    }

    async checkElements(): Promise<Page> {
        await expect(this.aggreatedData).toBeVisible();
        await expect(this.saleHeaderRow).toBeVisible();
        await expect(this.saleRow.first()).toBeVisible();

        return this.page;
    }
}
