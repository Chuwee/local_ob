/* eslint-disable @typescript-eslint/no-var-requires */
import { defineConfig } from 'cypress';
import { globalOpts } from '../../cypress.global.opts';

export default defineConfig({
    ...globalOpts,
    e2e: {
        supportFile: 'src/support/e2e.ts',
        specPattern: 'src/e2e/**/*spec.{js,jsx,ts,tsx}',
        env: {
            grepFilterSpecs: true,
            grepOmitFiltered: true,
        },
        baseUrl: 'http://localhost:4200',
        setupNodeEvents(on, config) {
            require('@cypress/grep/src/plugin')(config);
            const env = config.env.env;
            config.env.environment = env;
            config.fixturesFolder = 'src/fixtures/pre';
            config.env.oauth = 'https://hub.oneboxtds.net';
            config.env.backend = 'https://hub.oneboxtds.net';
            if (env?.match(/pre/g) != null) {
                config.env.oauth = `https://hub${env.slice(3)}.oneboxtds.net`;
                config.env.backend = `https://hub${env.slice(3)}.oneboxtds.net`;
                config.baseUrl = config.env.backend;
            }
            if (env && env === 'pro') {
                config.fixturesFolder = 'src/fixtures/pro';
                config.env.oauth = 'https://hub.oneboxtds.com';
                config.env.backend = 'https://hub.oneboxtds.com';
                config.baseUrl = config.env.backend;
            }
            return config;
        },
        // Please ensure you use `cy.origin()` when navigating between domains and remove this option.
        // See https://docs.cypress.io/app/references/migration-guide#Changes-to-cyorigin
        injectDocumentDomain: true,
    },
});
