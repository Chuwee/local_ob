import { Locator, Page } from '@playwright/test';

export class LoginPage {
    readonly page: Page;
    readonly username: Locator;
    readonly password;
    readonly loginButton;

    constructor(page: Page) {
        this.page = page;
        this.username = page.locator('[formcontrolname="username"]');
        this.password = page.locator('[formcontrolname="password"]');
        this.loginButton = page.locator('button[type="submit"]');
    }

    async setUsername(username: string): Promise<Page> {
        await this.username.fill(username);
        return this.page;
    }

    async setPassword(password: string): Promise<Page> {
        await this.password.fill(password);
        return this.page;
    }

    async login(): Promise<Page> {
        await this.loginButton.click();
        return this.page;
    }
}
