import elements from './voucher-group-components';
import listElements from '../list/voucher-groups-list-components';
import $bottombar from '../../../shared/components/bottombar-components';
import * as iframeUtils from '../../../shared/utils/iframe-utils';


describe('Tests of Channels/Voucher-group principal info', () => {
    context('using user entityMgrECVCy', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept('PUT', '**/voucher-groups/*', { statusCode: 204 }).as('PUTVoucherGroup');
            cy.visit('/vouchers');
        });
        it('activates a manual voucher-group ready to be activated', () => {
            cy.contains(listElements.CELL_IN_NAME_COL, 'ECV Manual Code + PIN VG').click();
            cy.get(elements.STATUS_SLIDER)
                .should('not.have.class', 'mat-checked')
                .click();
            cy.wait('@PUTVoucherGroup').then(xhr => {
                expect(xhr.request.body).to.eql(
                    { status: 'ACTIVE' }
                );
            });
        });
        it('can not activate a voucher-group without comm texts and validation channels', () => {
            cy.contains(listElements.CELL_IN_NAME_COL, 'ECV Not ready to activate').click();
            cy.get(elements.STATUS_SLIDER)
                .should('have.class', 'mdc-switch--unselected')
                .should('have.class', 'mdc-switch--disabled');
            cy.get('.mat-mdc-tooltip').should('not.exist');
            cy.get(elements.STATUS_SLIDER)
                .trigger('mouseenter', { force: true })
            cy.get('.mat-mdc-tooltip').should('exist')
        });
        it('edits name and description of a manual VG', () => {
            cy.contains(listElements.CELL_IN_NAME_COL, 'ECV Manual Code + PIN VG').click();
            cy.get(elements.DATA.VG_NAME)
                .clear()
                .type('name edited');
            cy.get(elements.DATA.VG_DESC)
                .clear()
                .type('description edited');
            cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            cy.wait('@PUTVoucherGroup').then(xhr => {
                expect(xhr.request.body).to.eql(
                    { name: 'name edited', description: 'description edited' }
                );
            });
        });
        it('edits Email contents of a gift-card VG', () => {
            cy.intercept('GET', '**/gift-card-contents/texts').as('GETtexts');
            cy.intercept('POST', '**/gift-card-contents/texts', {}).as('POSTtexts');
            cy.contains(listElements.CELL_IN_NAME_COL, 'ECV Gift Card').click();
            cy.get(elements.MENU_EMAIL_BTN).click();
            cy.wait('@GETtexts').then(xhr => {
                const esBody = xhr.response.body.find(obj => {
                    return obj.language === 'es-ES' && obj.type === 'EMAIL_BODY'
                })
                const esSubject = xhr.response.body.find(obj => {
                    return obj.language === 'es-ES' && obj.type === 'EMAIL_SUBJECT'
                })
                const esCopyright = xhr.response.body.find(obj => {
                    return obj.language === 'es-ES' && obj.type === 'EMAIL_COPYRIGHT'
                })
                cy.get(elements.EMAIL.SUBJECT_INPUT)
                    .should('have.value', esSubject.value)
                    .clear()
                    .type('subject edited');
                iframeUtils.getRichTextIframeBody('body')
                    .invoke('text')
                    .should('equal', esBody.value.replace(/<p>|<\/p>/g, ''))
                iframeUtils.getRichTextIframeBody('body')
                    .clear()
                    .type('body edited');
                cy.get(elements.EMAIL.COPYRIGHT_INPUT)
                    .should('have.value', esCopyright.value)
                    .clear()
                    .type('copyright edited');
                cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
            })
            cy.wait('@POSTtexts').then(xhr => {
                expect(xhr.request.body).to.eql(
                    [{
                        language: 'es-ES',
                        type: 'EMAIL_SUBJECT',
                        value: 'subject edited'
                    }, {
                        language: 'es-ES',
                        type: 'EMAIL_BODY',
                        value: '<p>body edited</p>'
                    }, {
                        language: 'es-ES',
                        type: 'EMAIL_COPYRIGHT',
                        value: 'copyright edited'
                    }]
                );
            });
        })
    });
});
