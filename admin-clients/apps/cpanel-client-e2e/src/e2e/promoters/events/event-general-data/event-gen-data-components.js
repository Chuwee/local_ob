const elements = {
    EVENT_SIDEBAR_ITEM: '.event-sidebar-label',
    CONFIG_VENUE_REPORTS: '[formcontrolname="allow_venue_reports"]',
    CONFIG_FESTIVAL: '[formcontrolname="enableFestival"]',
    SESSIONPACK_ENABLE_CHECKBOX: '[formgroupname="sessionPack"] [formcontrolname="enabled"]',
    SESSIONPACK_TYPE_RADIOS: '[formgroupname="sessionPack"] [formcontrolname="type"]',
    BOOKINGS_ENABLE_CHECKBOX: 'app-event-booking [formcontrolname="enable"]',
    CONTACT_DATA_NAME: '#contactData [aria-label="name"]',
    CONTACT_DATA_SURNAME: '#contactData [aria-label="surname"]',
    CONTACT_DATA_EMAIL: '#contactData [aria-label="email"]',
    CONTACT_DATA_PHONE: '#contactData [aria-label="phone_number"]',
    CONTACT_DATA_IMPORT_BTN: '.event-import-data-button .ob-link',
    INVOICE_DATA_RADIO_BTN: '#invoiceData mat-radio-button',
    INVOICE_INFO_ITEM: '.invoice-info mat-list-item',
    CATEGORIES_CUSTOM_SELECT: '#categoriesData mat-select[aria-label="custom category id"]',
    CATEGORIES_BASE_SELECT: '#categoriesData mat-select[aria-label="category id"]',
    TIERS_CHECKBOX: 'mat-checkbox[formcontrolname = "enableTiers"]',
    TIERS_CHECKBOX_INPUT: 'mat-checkbox[formcontrolname = "enableTiers"] input'
}
export default elements;
