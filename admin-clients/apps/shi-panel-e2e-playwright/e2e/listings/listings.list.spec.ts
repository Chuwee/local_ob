import { expect } from '@playwright/test';
import { Login } from '../../fixtures/login';
import { test } from '../../fixtures/spBranchParamsToGoto';
import { ListingsPage } from '../page-objects/listings-page';

test.describe('Test listings list', () => {
    test.beforeEach(async ({ request, page }) => {
        const login = new Login(request, page);
        await login.setData('shiUser');
    });

    test('Get list sorted by created date', { tag: ['@pgl'] }, async ({ page }) => {
        const listingsPage = new ListingsPage(page);
        await page.goto('/listings');

        expect(await page.locator('h1').innerText()).toContain('Listings');

        await listingsPage.checkElements();
    });
});
