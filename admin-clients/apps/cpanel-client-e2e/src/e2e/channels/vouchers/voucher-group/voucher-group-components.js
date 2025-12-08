const elements = {
    STATUS_SLIDER: 'mat-slide-toggle button',
    MENU_EMAIL_BTN: 'mat-button-toggle[value="email"]',
    EMAIL: {
        SUBJECT_INPUT: 'input[formcontrolname="subject"]',
        BODY_TEXT_AREA: '[formcontrolname="body"] #tinymce',
        COPYRIGHT_INPUT: 'input[formcontrolname="copyright"]'
    },
    DATA: {
        VG_NAME: 'input[formcontrolname="name"]',
        VG_DESC: 'textarea[formcontrolname="description"]'
    },
    CONFIG: {
        REDEEM_CHANNEL_OPTION: '.channels-list-container mat-list-option',
        REDEEM_CHANNEL_RADIO: 'mat-radio-button'
    }
}
export default elements;
