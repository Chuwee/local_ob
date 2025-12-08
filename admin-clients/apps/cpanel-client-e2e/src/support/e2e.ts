// ***********************************************************
// This example support/e2e.ts is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// Import commands.js using ES2015 syntax:
import './commands';

// load and register the grep feature
// https://github.com/cypress-io/cypress-grep
const registerCypressGrep = require('@cypress/grep');

registerCypressGrep();

beforeEach(() => {
    // this will run before every test in every spec file!!!!!!
    // we always need /myself response in order to get the proper translations for the user
    // you can add any other route if you'll always need it in all the tests
    cy.intercept('**/myself').as('myself');
    cy.intercept(/cpanel-client-translations\/(staging|production)\/.+\.json/).as('translations');
    const sp = Cypress.env('sp');
    if (sp) {
        cy.window().then(win => {
            win.sessionStorage.setItem('sp', sp);
            win.sessionStorage.setItem('fm', 'STRICT');
        });
    }
    cy.intercept('**/authentication', req => {
        req.headers['ob-waf-trusted'] = 'deiTokiu9uenu4aidugu';
        if (Cypress.env('env') === 'pro') {
            req.headers['ob-waf-trusted'] = 'TCdWcZsRh4qVHkLIbTE1';
        }
    });
});
