import { expect } from '@playwright/test';
import { Login } from '../../fixtures/login';
import { test } from '../../fixtures/spBranchParamsToGoto';
import { CurrenciesPage } from '../page-objects/currencies-page';

test.describe('Test currencies list', () => {
    test.beforeEach(async ({ request, page }) => {
        const login = new Login(request, page);
        await login.setData('shiUser');
    });

    test('Get list sorted by supplier', { tag: ['@pgl'] }, async ({ page }) => {
        const currenciesPage = new CurrenciesPage(page);
        await page.goto('/currencies');

        expect(await page.locator('h1').innerText()).toContain('Currencies');

        await currenciesPage.checkElements();
    });
});
