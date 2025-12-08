import { Locator, Page, expect } from '@playwright/test';

export class ListingsPage {
    readonly page: Page;
    readonly aggreatedData: Locator;
    readonly listHeaderRow: Locator;
    readonly listRow: Locator;

    constructor(page: Page) {
        this.page = page;
        this.aggreatedData = page.locator('app-aggregated-data');
        this.listHeaderRow = page.locator('mat-header-row');
        this.listRow = page.locator('mat-row');
    }

    async checkElements(): Promise<Page> {
        await expect(this.aggreatedData).toBeVisible();
        await expect(this.listHeaderRow).toBeVisible();
        await expect(this.listRow.first()).toBeVisible();

        return this.page;
    }
}
