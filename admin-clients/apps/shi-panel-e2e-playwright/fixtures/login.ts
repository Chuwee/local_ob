import { APIRequestContext, Page } from '@playwright/test';

export class Login {
    readonly request: APIRequestContext;
    readonly page: Page;
    readonly env: string;
    readonly users: JSON;

    constructor(request: APIRequestContext, page: Page) {
        this.request = request;
        this.page = page;
        this.env = process.env['APP_ENV'] === 'pro' ? 'pro' : 'pre';
        // eslint-disable-next-line @typescript-eslint/no-var-requires
        this.users = JSON.parse(JSON.stringify(require(`../data/${this.env}/users.json`)));
    }

    async setData(user: string): Promise<Page> {
        const dataRequest = {
            headers: {
                // eslint-disable-next-line @typescript-eslint/naming-convention
                'ob-waf-trusted': 'deiTokiu9uenu4aidugu'
            },
            data: {
                username: this.users[user].username,
                password: this.users[user].password
            }
        };

        if (this.env === 'pro') {
            dataRequest.headers['ob-waf-trusted'] = 'TCdWcZsRh4qVHkLIbTE1';
        }

        const response = await this.request.post('/api/shi-mgmt-api/v1/users/login', dataRequest);
        const oauth = await response.json();

        await this.page.goto('');

        // eslint-disable-next-line @typescript-eslint/dot-notation
        const accessToken = oauth['access_token'];
        // eslint-disable-next-line @typescript-eslint/dot-notation
        const userId = oauth['user_id'];

        await this.page.evaluate(accessToken => window.localStorage.setItem('shi-panel-token', accessToken), accessToken);
        await this.page.evaluate(userId => window.localStorage.setItem('user-id', userId), userId);

        return this.page;
    }
}
