import elements from './invoicing-entities-configuration-components';
import $bottombar from '../../../shared/components/bottombar-components';
import $dialogs from '../../../shared/components/dialogs-components';

describe('Tests of Operators/Invoicing/Entities configuration', () => {
    context('using user sysAdmin and entity Primavera Sound of España operator', () => {
        let entity;
        before(() => {
            cy.fixture('organizations/entities').then((entities) => {
                entity = entities.PrimaveraSound;
            });
        });
        beforeEach(() => {
            cy.login('sysAdmin');
            cy.intercept('GET', '**/onebox-invoicing/entities').as('GETinvoicingConfigs');
            cy.intercept('POST', `**/onebox-invoicing/entities/${entity.id}`, {}).as('POSTinvoicingConfig');
            cy.intercept('PUT', `**/onebox-invoicing/entities/${entity.id}`, {}).as('PUTinvoicingConfig');
            cy.intercept('GET', /\/entities\?/).as('GETentities');
            cy.visit('/invoicing/entities-configuration');
        });
        // skipped until issue OB-34906 is done
        xit('creates a new entity invoicing configuration', () => {
            cy.get(elements.ADD_CONFIG_BTN, { timeout: 10000 }).click();
            cy.wait('@GETentities').then(xhr => {
                const ps = xhr.response.body.data.find(e => e.id == entity.id);
                cy.get(elements.ADD_CONFIG_ENTITY_SELECT).click();
                cy.contains(elements.ADD_CONFIG_DROPDOWN_OPTION, ps.name).click({ force: true });
            });
            cy.get(elements.ADD_CONFIG_FIXED_INPUT).clear().type('1');
            cy.get(elements.ADD_CONFIG_VARIABLE_INPUT).clear().type('2');
            cy.get(elements.ADD_CONFIG_MIN_INPUT).clear().type('0.1');
            cy.get(elements.ADD_CONFIG_MAX_INPUT).clear().type('0.2');
            cy.get(elements.ADD_CONFIG_INVITATION_INPUT).clear().type('3');
            cy.get(elements.ADD_CONFIG_REFUND_INPUT).clear().type('4');
            cy.get(elements.ADD_CONFIG_CREATE_CONFIG_BTN).click();
            cy.wait('@POSTinvoicingConfig').then(xhr => {
                expect(xhr.request.body).to.eql(
                    { fixed: 1, variable: 2, min: 0.1, max: 0.2, invitation: 3, refund: 4 }
                );
                expect(xhr.request.url).to.contain(entity.id);
            });
        });
        it('sees the invoicing configuration of all entities', () => {
            cy.wait('@GETinvoicingConfigs').then(xhr => {
                const ps = xhr.response.body.find(e => e.entity.id == entity.id);
                cy.contains(elements.ROW, ps.entity.name).within(() => {
                    cy.get(elements.ENTITY_CELL).should('have.text', ps.entity.name);
                    cy.get(elements.FIXED_CELL).should('have.value', ps.fixed);
                    cy.get(elements.VARIABLE_CELL).should('have.value', ps.variable);
                    cy.get(elements.MIN_CELL).should('have.value', ps.min);
                    cy.get(elements.MAX_CELL).should('have.value', ps.max);
                    cy.get(elements.INVITATION_CELL).should('have.value', ps.invitation);
                    cy.get(elements.REFUND_CELL).should('have.value', ps.refund);
                })
            })
        });
        it('edits the invoicing configuration of an entity', () => {
            cy.wait('@GETinvoicingConfigs').then(xhr => {
                const ps = xhr.response.body.find(e => e.entity.id == entity.id);
                cy.contains(elements.ROW, ps.entity.name).within(() => {
                    cy.get(elements.ENTITY_CELL).should('have.text', ps.entity.name);
                    cy.get(elements.FIXED_CELL).clear().type('0.34').should('have.value', '0.34');
                    cy.get(elements.VARIABLE_CELL).clear().type('0.56').should('have.value', '0.56');
                    cy.get(elements.MIN_CELL).clear().type('0.78').should('have.value', '0.78');
                    cy.get(elements.MAX_CELL).clear().type('0.99').should('have.value', '0.99');
                    cy.get(elements.INVITATION_CELL).clear().type('1.01').should('have.value', '1.01');
                    cy.get(elements.REFUND_CELL).clear().type('2').should('have.value', '2');
                })
            })
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTinvoicingConfig').then(xhr => {
                expect(xhr.request.body).to.eql(
                    { fixed: 0.34, variable: 0.56, min: 0.78, max: 0.99, invitation: 1.01, refund: 2 }
                )
                expect(xhr.request.url).to.contain(entity.id);
            });
            cy.get($dialogs.SNACKBAR).should('be.visible');
            cy.getLiteral('FORMS.FEEDBACK.SAVE_SUCCESS').then(literal => {
                cy.get($dialogs.SNACKBAR_MSG).should('have.text', literal);
            });
        })
    })
})
