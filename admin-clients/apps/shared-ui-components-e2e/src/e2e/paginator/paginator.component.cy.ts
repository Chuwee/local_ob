describe('shared-ui-components', () => {
    beforeEach(() =>
        cy.visit(
            '/iframe.html?id=paginatorcomponent--primary&args=canChange$;length;pageSize;pageIndex;'
        )
    );
    it('should render the component', () => {
        cy.get('app-paginator').should('exist');
    });
});
