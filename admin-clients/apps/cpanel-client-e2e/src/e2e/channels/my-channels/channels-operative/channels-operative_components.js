const elements = {
    VERTICAL_MENU: {
        SURCHARGES_TOGGLE: '[routerlink="surcharges"]',
        COMMISSIONS_TOGGLE: '[routerlink="commissions"]'
    },
    RANGES: {
        GENERIC_SUR: '[data-test="genericSurcharges"]',
        PROMOTIONS_SUR: '[data-test="promotionSurcharges"]',
        PROMOTIONS_ENABLER: '#promotions-surcharges-enabled',
        INVITATIONS_SUR: '[data-test="invitationSurcharges"]',
        NEW_RANGE_BUTTON: 'button.mat-primary',
        NEW_RANGE_FROM_INPUT: 'app-currency-input[formcontrolname="from"] input',
        NEW_RANGE_CONFIRM: 'app-new-range-dialog button.mat-primary',
        RANGE_TITLE: '.range-title',
        TABLE_ROWS: '#rangesTable .mat-mdc-row',
        VALUE_CELLS: '.editable-cell',
        FIX_CELL: '.currency-value:eq(0)',
        PERCENTAGE_CELL: '.percentage-value',
        MIN_CELL: '.currency-value:eq(1)',
        MAX_CELL: '.currency-value:eq(2)',
        ROW_FIELDS: '.mat-cell',
        ROW_DELETE: '.actions-cell button',
        RESIZE_LOWER: 'app-delete-range-dialog button.mat-primary:eq(0)',
        RESIZE_HIGHER: 'app-delete-range-dialog button.mat-primary:eq(1)',
        CONFIRM_DELETE: 'app-delete-range-dialog button.mat-primary',
        SURCH_AFTER_PROMS: '#surcharges-before-channel-promotions'
    },
    DELIVERY_METHODS: {
        DELIVERY_METHOD_ROW: '#optionsTable tr[mat-row]',
        CHECKBOX: 'mat-checkbox',
        STAR_SELECTED: '#optionsTable mat-row .mat-column-default .selected',
        STAR_UNSELECTED: '#optionsTable mat-row .mat-column-default .selected',
        PRICE: 'app-currency-input',
    },
    GIFT_CARD: {
        ENABLE_CHECKBOX: 'mat-checkbox[data-test="gift-card-enable"]',
        GC_SELECT: '[data-test="gift-card-group"]',
        GC_OPTION: 'mat-option'
    }
}
export default elements;
