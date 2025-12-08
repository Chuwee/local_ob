* A place to share useful recipes in Cypress
In order to check if an element exists without asserting it, use the proxied jQuery function Cypress.$

const $el = Cypress.$('.greeting')
if ($el.length) {
  cy.log('Closing greeting')
  cy.get('.greeting')
    .contains('Close')
    .click()
}
cy.get('.greeting')
  .should('not.be.visible')
