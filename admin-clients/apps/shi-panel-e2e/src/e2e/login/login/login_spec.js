import elements from './login-components';

describe('Tests of Login and authorization issues', () => {

    beforeEach(() => {
        cy.intercept(/\/users\?(?!branch|sp)./).as('GETusers');
    });

    it('loads the right main.js', () => {
        cy.intercept('**/main-*.js').as('mainPrefetch');
        cy.visit('/login');
        cy.wait('@mainPrefetch').its('request.url').should('include', 'client-dists');
    });

    it('access with an invalid token in cookies and redirects to login page', () => {
        cy.setCookie('shi-panel-token', '123key');
        cy.setCookie('user-id', '123key');
        cy.visit('/sales');
        cy.location().should((loc) => {
            expect(loc.protocol).to.eq('https:')
            expect(loc.pathname).to.eq('/login')
            expect(loc.search).to.include('returnUrl=')
        });
        cy.location().then((loc) => {
            console.log(loc)
          });
    });

    it('access without a token in cookies and redirects to login page', () => {
        cy.visit('/sales');
        cy.location().should((loc) => {
            expect(loc.protocol).to.eq('https:')
            expect(loc.pathname).to.eq('/login')
            expect(loc.search).to.include('returnUrl=')
        });
    });

    it('login using the ui', { tags: '@pgl' }, () => {
        cy.fixture('/users.json').then(userData => {
            cy.intercept('POST', '**/users/login').as('POSTlogin');
            cy.intercept('GET', '**/users/*').as('GETuser');
            const user = userData['shiUser'];
            cy.visit('/login');
            cy.get(elements.USERNAME).type(user.username);
            cy.get(elements.PASSWORD).type(user.password);
            cy.get(elements.SUBMIT).click();
            cy.wait('@POSTlogin').then(xhr => {
                expect(xhr.request.body.username).to.eql(user.username);
                expect(xhr.request.body.password).to.eql(user.password);
            })
            cy.wait('@GETuser', { timeout: 10000 }).then(xhr => {
                expect(xhr.response.body.username).to.eql(user.username);
            });
            cy.url().should('not.include', '/login');
        })
    });

    it('login using the ui with entity user and wrong password', () => {
        cy.fixture('/users.json').then(userData => {
            const user = userData['shiUser'];
            cy.visit('/login');
            cy.get(elements.USERNAME).type(user.username);
            cy.get(elements.PASSWORD).type('wrong');
            cy.get(elements.SUBMIT).click();
            cy.get(elements.INCORRECT_LOGIN_ICON).should('be.visible');
            cy.getLiteral('LOGIN.INVALID_GRANT').then(literal => {
                cy.get(elements.INCORRECT_LOGIN_MSG).should('have.text', literal);
            })
        })
    });

    it('login using only token then you will be redirected to login page', () => {
        cy.getToken('shiUser').then(token => {
            window.localStorage.setItem('ob-sidenav-opened', 'true');
            window.localStorage.setItem('shi-panel-token', token);
            cy.visit('/sales');
            cy.location().should((loc) => {
                expect(loc.protocol).to.eq('https:')
                expect(loc.pathname).to.eq('/login')
                expect(loc.search).to.include('returnUrl=')
            });
        })
    });

    it('login using token ok and invalid user then you will be redirected to login page', () => {
        cy.getToken('shiUser').then(token => {
            window.localStorage.setItem('ob-sidenav-opened', 'true');
            window.localStorage.setItem('shi-panel-token', token);
            window.sessionStorage.setItem('user-id', 'xxxxxxxxxxx');
            cy.visit('/sales');
            cy.location().should((loc) => {
                expect(loc.protocol).to.eq('https:')
                expect(loc.pathname).to.eq('/login')
            });
        })
    })

});
