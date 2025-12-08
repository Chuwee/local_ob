import { nxE2EPreset } from '@nx/playwright/preset';
import { defineConfig, devices } from '@playwright/test';
import { urls } from './data/urls';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
//import { workspaceRoot } from '@nx/devkit';

const env = process.env['APP_ENV'];
const baseURL = env ? urls[env].base : 'http://localhost:4200';
const header = env ? urls[env].header : urls.pre.header;

// For CI, you may want to set BASE_URL to the deployed application.
//const baseURL = process.env['BASE_URL'] || 'http://localhost:3000';

/**
 * Read environment variables from file.
 * https://github.com/motdotla/dotenv
 */
// require('dotenv').config();

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
    ...nxE2EPreset(__filename, { testDir: './e2e' }),
    /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
    use: {
        baseURL,
        ignoreHTTPSErrors: true,
        testIdAttribute: 'data-test',
        extraHTTPHeaders: {
            // eslint-disable-next-line @typescript-eslint/naming-convention
            'ob-waf-trusted': header
        },
        /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
        trace: 'on-first-retry',
        screenshot: 'only-on-failure',
        bypassCSP: true,
        launchOptions: {
            args: ['--disable-web-security']
        }
    },
    /* Run your local dev server before starting the tests */
    // webServer: {
    //   command: 'npm run start',
    //   url: 'http://127.0.0.1:3000',
    //   reuseExistingServer: !process.env.CI,
    //   cwd: workspaceRoot,
    // },
    projects: [
        {
            name: 'Desktop',
            use: {
                ...devices['Desktop'],
                browserName: 'chromium'
            }
        }
        /*
        {
            name: 'chromium',
            use: { ...devices['Desktop Chrome'] }
        }
{
            name: 'firefox',
            use: { ...devices['Desktop Firefox'] },
        },

        {
            name: 'webkit',
            use: { ...devices['Desktop Safari'] },
        },*/

        // Uncomment for mobile browsers support
        /* {
      name: 'Mobile Chrome',
      use: { ...devices['Pixel 5'] },
    },
    {
      name: 'Mobile Safari',
      use: { ...devices['iPhone 12'] },
    }, */

        // Uncomment for branded browsers
        /* {
      name: 'Microsoft Edge',
      use: { ...devices['Desktop Edge'], channel: 'msedge' },
    },
    {
      name: 'Google Chrome',
      use: { ...devices['Desktop Chrome'], channel: 'chrome' },
    } */
    ],
    reporter: [
        ['html'],
        ['playwright-json-summary-reporter'],
        ['playwright-ctrf-json-reporter'],
        ['dot']
    ]
});
