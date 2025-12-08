import { test as base } from '@playwright/test';

export const test = base.extend({
    page: async ({ page }, use) => {
        const branch = process.env['APP_BRANCH'];
        const sp = process.env['SP'];
        const goto = page.goto.bind(page);
        // eslint-disable-next-line @typescript-eslint/explicit-function-return-type
        function modifiedGoto(url, options) {
            const queryParams: string[] = [];
            if (branch) queryParams.push(`branch=${branch}`);
            if (sp) queryParams.push(`sp=${sp}`);
            if (queryParams.length) url += `${url.indexOf('?') >= 0 ? '&' : '?'}${queryParams.join('&')}`;

            return goto(url, options);
        }
        page.goto = modifiedGoto;
        await use(page);
        page.goto = goto;
    }
});
