const elements = {
    PAY: {
        NEW_GATEWAY_BTN: 'app-payment-methods button.add-button',
        NEW_DIALOG: {
            NAME: '[data-test="payment-method-name"]',
            LANG_TOGGLE: 'mat-dialog-container mat-button-toggle button',
            LANG_SELECTED: '.dialog-msg-container mat-button-toggle-group .mat-button-toggle-checked',
            NAME_TRANSLATION: '[formgroupname="translations"] input',
            NAME_TRANSLATION_LABEL: '[formgroupname="translations"] .mat-form-field-label',
            DESCRIPTION_INPUT: '[formcontrolname="description"]',
            GATEWAY_SELECTOR: '[formcontrolname="gateway"]',
            GATEWAY_OPTIONS: {
                CASH: 'mat-option:has(span:contains("Cash"))'
            },
            CREATE_BTN: '.mat-dialog-actions button.mat-primary',
            CANCEL_BTN: '.mat-dialog-actions button.mat-button'
        },
        PAYMENT_METHODS_TOGGLE: '[routerlink="payment-methods"]',
        DELETE_BUTTON: '[aria-label="Delete payment method"]',
        VOUCHERS_REDEEM_CHECKBOX: '[data-test="voucher-redeem"] label',
        VOUCHERS_REFUND_CHECKBOX: '[data-test="voucher-refund"] label'
    }
}
export default elements;
