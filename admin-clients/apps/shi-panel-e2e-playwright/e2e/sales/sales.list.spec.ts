import { expect } from '@playwright/test';
import { Login } from '../../fixtures/login';
import { test } from '../../fixtures/spBranchParamsToGoto';
import { SalesPage } from '../page-objects/sales-page';

test.describe('Tests of sales list', () => {
    test.beforeEach(async ({ request, page }) => {
        const login = new Login(request, page);
        await login.setData('shiUser');
    });

    test('Get list sorted by created date', { tag: ['@pgl'] }, async ({ page }) => {
        const salesPage = new SalesPage(page);
        await page.goto('/sales');

        expect(await page.locator('h1').innerText()).toContain('Sales');

        await salesPage.checkElements();

    });
});

