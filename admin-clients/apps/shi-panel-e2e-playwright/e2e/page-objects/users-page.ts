import { Locator, Page, expect } from '@playwright/test';

export class UsersPage {
    readonly page: Page;
    readonly userHeaderRow: Locator;
    readonly userRow: Locator;

    constructor(page: Page) {
        this.page = page;
        this.userHeaderRow = page.locator('mat-header-row');
        this.userRow = page.locator('mat-row');
    }

    async checkElements(): Promise<Page> {
        await expect(this.userHeaderRow).toBeVisible();
        await expect(this.userRow.first()).toBeVisible();

        return this.page;
    }
}
