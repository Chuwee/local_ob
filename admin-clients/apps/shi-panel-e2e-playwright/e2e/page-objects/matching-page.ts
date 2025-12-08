import { Locator, Page, expect } from '@playwright/test';

export class MatchingPage {
    readonly page: Page;
    readonly aggreatedData: Locator;
    readonly matchingHeaderRow: Locator;
    readonly matchingRow: Locator;

    constructor(page: Page) {
        this.page = page;
        this.aggreatedData = page.locator('app-aggregated-data');
        this.matchingHeaderRow = page.locator('mat-header-row');
        this.matchingRow = page.locator('mat-row');
    }

    async checkElements(): Promise<Page> {
        await expect(this.aggreatedData).toBeVisible();
        await expect(this.matchingHeaderRow).toBeVisible();
        await expect(this.matchingRow.first()).toBeVisible();

        return this.page;
    }
}
