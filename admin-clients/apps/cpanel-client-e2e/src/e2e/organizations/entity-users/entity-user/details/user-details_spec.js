import elements from '../details/user-details-components';
import $dialogs from '../../../../shared/components/dialogs-components';

describe('Tests of organizations/entity-users/details/user-details', () => {
    let translations;
    context('with user operAdminCy and user tempBlocked', () => {
        let user;
        before(() => {
            cy.fixture('users').then((users) => {
                user = users.tempBlocked;
            });
        });
        beforeEach(() => {
            cy.login('operAdminCy');
            cy.intercept('GET', '**/languages**').as('GETlanguages');
            cy.visit(`/users/${user.id}/register-data`);
        });
        it('sees platform languages in user details', () => {
            cy.wait('@GETlanguages', { timeout: 10000 }).then(xhr => {
                expect(xhr.request.url).to.contain('platform_language=true')
                const langs = xhr.response.body;
                cy.get(elements.LANGUAGE_INPUT).click();
                cy.get(elements.DROPDOWN_OPTION).should('have.length', langs.length + 1)
            });
        });
        it('sets a new password to another user fulfilling conditions one by one', () => {
            cy.intercept('POST', '**/users/*/password', {}).as('POSTpassword');
            cy.wait('@translations', { timeout: 10000 }).then(xhr => {
                translations = xhr.response.body;
                const passwordLiterals = translations.USER.PASSWORD;
                const setPasswordLiteral = translations.TITLES.SET_PASSWORD;
                cy.contains(elements.SET_PASSWORD_BTN, setPasswordLiteral, { timeout: 10000 }).click();
                cy.get(elements.NEW_PASSWORD_INPUT).type('a').blur();
                cy.contains(elements.PASSWORD_CONDITIONS, passwordLiterals.NO_DIGIT)
                    .should('have.class', 'error');
                cy.get(elements.NEW_PASSWORD_INPUT).type('1');
                cy.contains(elements.PASSWORD_CONDITIONS, passwordLiterals.NO_DIGIT)
                    .should('not.have.class', 'error');
                cy.get(elements.NEW_PASSWORD_INPUT).type('2345678').blur();
                cy.contains(elements.PASSWORD_CONDITIONS, passwordLiterals.NO_MIN_LENGTH)
                    .should('have.class', 'error');
                cy.contains(elements.PASSWORD_CONDITIONS, passwordLiterals.NO_LOWER_AND_UPPER_CASE)
                    .should('have.class', 'error');
                cy.get(elements.NEW_PASSWORD_INPUT).type('A');
                cy.contains(elements.PASSWORD_CONDITIONS, passwordLiterals.NO_LOWER_AND_UPPER_CASE)
                    .should('not.have.class', 'error');
                cy.contains(elements.PASSWORD_CONDITIONS, passwordLiterals.NO_SPECIAL_CHARACTER)
                    .should('have.class', 'error');
                cy.get(elements.NEW_PASSWORD_INPUT).type('$');
                cy.contains(elements.PASSWORD_CONDITIONS, passwordLiterals.NO_SPECIAL_CHARACTER)
                    .should('not.have.class', 'error');
                cy.get(elements.NEW_PASSWORD_INPUT).type('9');
                cy.contains(elements.PASSWORD_CONDITIONS, passwordLiterals.NO_MIN_LENGTH)
                    .should('not.have.class', 'error');
                cy.get(elements.CONFIRM_PASSWORD_INPUT).type('a12345678A$').blur();
                cy.get(elements.CONFIRM_PASSWORD_ERROR).should('be.visible');
                cy.get(elements.CONFIRM_PASSWORD_INPUT).type('9');
                cy.get(elements.CONFIRM_PASSWORD_ERROR).should('not.exist');
                cy.get(elements.CONFIRM_PASSWORD_BTN).click();
                cy.wait('@POSTpassword').then(xhr => {
                    expect(xhr.request.body).to.contain(
                        { password: 'a12345678A$9' }
                    )
                });
                cy.get($dialogs.SNACKBAR).should('be.visible');
            })
        });
    });
    context('with user entityMgrChannelCy', () => {
        let user;
        before(() => {
            cy.fixture('users').then((users) => {
                user = users.entityMgrChannelCy;
            });
        });
        beforeEach(() => {
            cy.login('entityMgrChannelCy');
            cy.visit(`/users/${user.id}/register-data`);
            cy.intercept('POST', '**/users/*/password', {}).as('POSTpassword');
        });
        it('sets a new password to self', () => {
            const setPasswordLiteral = translations.TITLES.SET_PASSWORD;
            cy.contains(elements.SET_PASSWORD_BTN, setPasswordLiteral, { timeout: 10000 }).click();
            cy.get(elements.OLD_PASSWORD_INPUT).type('old');
            cy.get(elements.NEW_PASSWORD_INPUT).type('a123456789A$', { delay: 0 });
            cy.get(elements.CONFIRM_PASSWORD_INPUT).type('a123456789A$', { delay: 0 });
            cy.get(elements.CONFIRM_PASSWORD_BTN).click();
            cy.wait('@POSTpassword').then(xhr => {
                expect(xhr.request.body).to.contain(
                    { password: 'a123456789A$', 'old_password': 'old' }
                )
            });
            cy.get($dialogs.SNACKBAR).should('be.visible');
        });
    });
});
