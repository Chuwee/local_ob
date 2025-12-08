import { expect } from '@playwright/test';
import { Login } from '../../fixtures/login';
import { test } from '../../fixtures/spBranchParamsToGoto';
import { MappingsPage } from '../page-objects/mappings-page';

test.describe('Test mappings list', () => {
    test.beforeEach(async ({ request, page }) => {
        const login = new Login(request, page);
        await login.setData('shiUser');
    });

    test('Get list sorted by created date', { tag: ['@pgl'] }, async ({ page }) => {
        const mappingsPage = new MappingsPage(page);
        await page.goto('/mappings');

        expect(await page.locator('h1').innerText()).toContain('Mappings');

        await mappingsPage.checkElements();
    });
});
