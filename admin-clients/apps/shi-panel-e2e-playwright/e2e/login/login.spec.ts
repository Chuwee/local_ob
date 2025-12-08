import { expect } from '@playwright/test';
import { test } from '../../fixtures/spBranchParamsToGoto';
import { LoginPage } from '../page-objects/login-page';

const env = process.env['APP_ENV'] === 'pro' ? 'pro' : 'pre';
// eslint-disable-next-line @typescript-eslint/no-var-requires
const users = JSON.parse(JSON.stringify(require(`../../data/${env}/users.json`)));

test('Login using the ui', { tag: ['@pgl'] }, async ({ page }) => {
    const loginPage = new LoginPage(page);

    await page.goto('/');
    await loginPage.setUsername(users.shiUser.username);
    await loginPage.setPassword(users.shiUser.password);
    await loginPage.login();

    await expect(page).not.toHaveURL(/.*\/login/);
});
