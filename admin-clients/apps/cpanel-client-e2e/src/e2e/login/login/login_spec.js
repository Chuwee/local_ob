import elements from './login-components';
import $dialogs from '../../shared/components/dialogs-components'

describe('Tests of Login and authorization issues', { tags: '@pgl' }, () => {
    beforeEach(() => {
        cy.intercept(/\/users\?(?!branch|sp)./).as('GETusers');
    });
    it('loads the right main.js', () => {
        cy.intercept('**/main-*.js').as('mainPrefetch');
        cy.visit('admin/login');
        cy.wait('@mainPrefetch').its('request.url').should('include', 'client-dists');
    });
    it('access with an invalid token in cookies and redirects to login page', () => {
        cy.setCookie('token', '123key');
        cy.visit('admin/events');
        cy.url().should('include', '/login?').and('include', 'returnUrl=');
    });
    it('access without a token in cookies and redirects to login page', () => {
        cy.visit('admin/events');
        cy.url().should('include', '/login?').and('include', 'returnUrl=');
    });
    it('access with a non-authorized token in cookies and redirects to login page', () => {
        cy.getToken('eventMgrEventCy').then((token) => {
            window.localStorage.setItem('token', token);
            cy.visit('admin/users');
            cy.wait('@GETusers', { 'requestTimeout': 10000 }).its('response.statusCode').should('eq', 403);
            cy.get($dialogs.WARN_DIALOG).should('be.visible');
        });
    });
    it('login using the ui with operator user in more than one operators', () => {
        cy.fixture('/users.json').then(userData => {
            const user = userData['operAdminCy'];
            cy.visit('admin/login');
            cy.get(elements.USERNAME).type(user.username);
            cy.get(elements.PASSWORD).type(user.password);
            cy.get(elements.SUBMIT).click();
            cy.get(elements.OPERATOR).should('not.have.class', 'mat-select-disabled').click();
            cy.contains('mat-option', user.operatorName).click();
            cy.get(elements.SUBMIT).click();
            cy.wait('@myself').then(xhr => {
                expect(xhr.response.body.username).to.eql(user.username);
                expect(xhr.response.body.operator.name).to.eql(user.operatorName);
                const entityName = xhr.response.body.entity.name;
                cy.get('.toolbar-title').should('contain', entityName);
            });
        })
    });
    it('login using the ui with entity user', () => {
        cy.fixture('/users.json').then(userData => {
            const user = userData['entityMgrECVCy'];
            cy.visit('admin/login');
            cy.get(elements.USERNAME).type(user.username);
            cy.get(elements.PASSWORD).type(user.password);
            cy.get(elements.SUBMIT).click();
            cy.wait('@myself').then(xhr => {
                const entityName = xhr.response.body.entity.name;
                cy.get('.toolbar-title').should('contain', entityName);
            });
        })
    });
    it('login using the ui with entity user and wrong password', () => {
        cy.fixture('/users.json').then(userData => {
            const user = userData['entityMgrECVCy'];
            cy.visit('admin/login');
            cy.get(elements.USERNAME).type(user.username);
            cy.get(elements.PASSWORD).type('wrong');
            cy.get(elements.SUBMIT).click();
            cy.get(elements.INCORRECT_LOGIN_ICON).should('be.visible');
            cy.getLiteral('LOGIN.INVALID_GRANT').then(literal => {
                cy.get(elements.INCORRECT_LOGIN_MSG).should('have.text', literal);
            })
        })
    });
});
