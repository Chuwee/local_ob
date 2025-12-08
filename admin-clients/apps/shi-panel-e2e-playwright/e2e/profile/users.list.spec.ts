import { expect } from '@playwright/test';
import { Login } from '../../fixtures/login';
import { test } from '../../fixtures/spBranchParamsToGoto';
import { UsersPage } from '../page-objects/users-page';

test.describe('Test users list', () => {
    test.beforeEach(async ({ request, page }) => {
        const login = new Login(request, page);
        await login.setData('shiUser');
    });

    test('Get list sorted by name', { tag: ['@pgl'] }, async ({ page }) => {
        const usersPage = new UsersPage(page);
        await page.goto('/users');

        expect(await page.locator('h1').innerText()).toContain('Users');

        await usersPage.checkElements();
    });
});
