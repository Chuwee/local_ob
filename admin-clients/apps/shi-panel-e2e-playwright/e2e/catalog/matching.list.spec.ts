import { expect } from '@playwright/test';
import { Login } from '../../fixtures/login';
import { test } from '../../fixtures/spBranchParamsToGoto';
import { MatchingPage } from '../page-objects/matching-page';

test.describe('Test matching list', () => {
    test.beforeEach(async ({ request, page }) => {
        const login = new Login(request, page);
        await login.setData('shiUser');
    });

    test('Get list sorted by status asc', { tag: ['@pgl'] }, async ({ page }) => {
        const matchingPage = new MatchingPage(page);
        await page.goto('/matchings');

        expect(await page.locator('h1').innerText()).toContain('Matching');

        await matchingPage.checkElements();
    });
});
