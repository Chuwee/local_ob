import elements from './entity-categories-components';
import $bottombar from '../../../shared/components/bottombar-components';


describe('Tests of Entites/Categories/My Categories', () => {
    context('using user entityMgrECVCy', () => {
        beforeEach(() => {
            cy.login('entityMgrECVCy');
            cy.intercept('GET', '**/base-categories').as('GETbaseCategories');
            cy.intercept('GET', '**/entity-categories/mapping*').as('GETmappingCategories');
            cy.intercept('GET', '**/entity-categories*').as('GETentityCategories');
            cy.intercept('PUT', '**/entity-categories/mapping*', { statusCode: 204 }).as('PUTmappingCategories');
            cy.visit('/my-entity/categories/my-categories');
        });
        it('sees all base and entity categories and edits one', () => {
            cy.wait('@GETbaseCategories').then(xhr => {
                const parentCategories = xhr.response.body.filter(c => c.parent_id == null)
                const childrenCategories = xhr.response.body.filter(c => c.parent_id != null)
                parentCategories.forEach((parent) => {
                    cy.getLiteral(`ENTITY.CATEGORY_OPTS.${parent.code}`).then(parentLiteral => {
                        cy.contains(elements.CATEGORY_CELL, parentLiteral)
                            .click()
                            .then(() => {
                                const ownChildren = childrenCategories.filter(child => child.parent_id === parent.id)
                                ownChildren.forEach((child) => {
                                    cy.contains(elements.SUBCATEGORY_CELL, child.code)
                                })
                            })
                    })
                })
                cy.contains(elements.CATEGORY_CELL, parentCategories[0].description)
                    .click();
                cy.wait('@GETentityCategories').then(getEntCat => {
                    const entityCategories = getEntCat.response.body;
                    const childrenSelected = childrenCategories.filter(child => child.parent_id === parentCategories[0].id)
                    cy.contains(elements.ROW, childrenSelected[0].description)
                        .within(() => {
                            cy.get(elements.MY_CATEGORY_CELL).first().click();
                        })
                    cy.contains('mat-option', entityCategories[0].code).click();
                    cy.get($bottombar.BOTTOMBAR_SAVE_CHANGES_BTN_ENABLED).click();
                    cy.wait('@PUTmappingCategories').then(putMap => {
                        expect(putMap.request.body).to.deep.include({
                            base_category_id: childrenSelected[0].id,
                            category_id: entityCategories[0].id
                        })
                    })
                })
            })
        })
    })
})
