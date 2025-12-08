describe('shared-ui-components', () => {
    beforeEach(() =>
        cy.visit(
            '/iframe.html?id=colorpickercomponent--primary&args=customColors;selectedColor;isDisabled;allowEmptyColor;'
        )
    );
    it('should render the component', () => {
        cy.get('app-color-picker').should('exist');
    });
});
