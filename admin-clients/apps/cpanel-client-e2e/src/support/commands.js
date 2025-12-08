import * as utils from '../e2e/shared/utils/utils';

Cypress.Commands.add('getLoginData', (user) => {
    cy.fixture('/users.json').as('userData').then((userData) => {
        const _url = Cypress.env('oauth') + '/oauth/token'
        const loginData = {
            'method': 'POST',
            'url': _url,
            'headers': {
                'ob-waf-trusted': 'deiTokiu9uenu4aidugu',
                'Authorization': 'Basic b25lYm94LWNsaWVudDpvY2VhbnRlY2g='
            },
            'qs': {
                'username': userData[user].username,
                'password': userData[user].password,
                'grant_type': 'password'
            },
            'timeout': 5000,
            'retryOnStatusCodeFailure': true
        }
        if (userData[user].operator) {
            loginData.qs.operator = userData[user].operator
        }
        if (Cypress.env('oauth').match(/oneboxtds.com/)) {
            loginData.headers['ob-waf-trusted'] = 'TCdWcZsRh4qVHkLIbTE1'
        }
        return loginData
    })
});
Cypress.Commands.add('getToken', (user) => {
    cy.getLoginData(user).then(loginData => {
        cy.request(loginData).as('user').then((resp) => {
            expect(resp.status).to.eq(200);
            return resp.body.access_token;
        })
    })
});
Cypress.Commands.add('login', (user) => {
    cy.session([user], () => {
        cy.getLoginData(user).then(loginData => {
            cy.request(loginData).as('user').then((resp) => {
                expect(resp.status).to.eq(200);
                window.localStorage.setItem('token', resp.body.access_token);
                if (Cypress.env('sp')) {
                    window.sessionStorage.setItem('sp', Cypress.env('sp'));
                    window.sessionStorage.setItem('fm', 'STRICT');
                }
            })
        })
    })
});
Cypress.Commands.add('getUserLanguage', () => {
    cy.get('@myself').then(xhr => { return xhr.response.body.language })
});
Cypress.Commands.add('getLiteral', (msgKeyPath) => {
    cy.get('@translations').then(xhr => {
        if (xhr == null) {
            cy.wait('@translations').then(xhr => {
                const translations = xhr.response.body;
                const literal = utils.getNestedValue(translations, msgKeyPath);
                console.log('literal found: ' + literal)
                return literal
            })
        } else {
            const translations = xhr.response.body;
            const literal = utils.getNestedValue(translations, msgKeyPath);
            console.log('literal found: ' + literal)
            return literal
        }
    })
});
Cypress.Commands.add('getElByKey', (keyPath, element) => {
    cy.getLiteral(keyPath).then(literal => {
        return cy.contains(element, literal);
    })
});
Cypress.Commands.add('getFromFixture', (fixture, key) => {
    cy.fixture(fixture).then(fix => {
        const data = utils.getNestedValue(fix, key);
        console.log('data found: ' + data);
        return data
    })
});
Cypress.Commands.overwrite('visit', (originalFn, url, options) => {
    const branch = Cypress.env('branch');
    console.log(Cypress.config('baseUrl'))
    options = { headers: { 'ob-waf-trusted': 'deiTokiu9uenu4aidugu' } }
    if (Cypress.config('baseUrl').match(/oneboxtds.com/)) {
        console.log('PRO')
        options = { headers: { 'ob-waf-trusted': 'TCdWcZsRh4qVHkLIbTE1' } }
    }
    if (branch) {
        options = { headers: options.headers, qs: { branch: branch } };
    }
    return originalFn(url, options)
})
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This is will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
