import { Locator, Page, expect } from '@playwright/test';

export class MappingsPage {
    readonly page: Page;
    readonly mappingHeaderRow: Locator;
    readonly mappingRow: Locator;

    constructor(page: Page) {
        this.page = page;
        this.mappingHeaderRow = page.locator('mat-header-row');
        this.mappingRow = page.locator('mat-row');
    }

    async checkElements(): Promise<Page> {
        await expect(this.mappingHeaderRow).toBeVisible();
        await expect(this.mappingRow.first()).toBeVisible();

        return this.page;
    }
}
