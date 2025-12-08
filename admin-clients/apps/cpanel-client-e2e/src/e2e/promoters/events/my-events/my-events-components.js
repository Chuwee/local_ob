import moment from 'moment-timezone';
import 'moment/locale/es';

const elements = {
    OPEN_FILTER_BUTTON: 'mat-button-toggle',
    FILTER_STATUS_SELECT: '.status-select',
    FILTER_DROPDOWN_OPTION: 'span.mdc-list-item__primary-text',
    FILTER_ENTITY_SELECT: '.entity-select mat-select.mat-mdc-select',
    FILTER_PRODUCER_SELECT: '.producer-select mat-select.mat-mdc-select',
    FILTER_VENUE_SELECT: '.venue-select mat-select.mat-mdc-select',
    FILTER_COUNTRY_SELECT: '.country-select mat-select.mat-mdc-select',
    FILTER_CITY_SELECT: '.city-select mat-select.mat-mdc-select',
    FILTER_TYPE_SELECT: '.type-select mat-select.mat-mdc-select',
    FILTER_INPUT_TYPE: '.mat-select-search-inner input',
    FILTER_STARTDATE_SELECT: 'input[formcontrolname="startDate"]',
    FILTER_ENDDATE_SELECT: 'input[formcontrolname="endDate"]',
    FILTER_ARCHIVED_CHECKBOX: '.show-archived-filter-container mat-checkbox',
    HEADER_IN_ENTITY_COL: '.mat-mdc-header-cell.cdk-column-entity',
    CELL_IN_STATUS_COL: '.mat-column-status.status-cell',
    CELL_IN_ENTITY_COL: '.mat-column-entity.mat-cell',
    CELL_IN_PRODUCER_COL: '.mat-column-producer.mat-cell',
    CELL_IN_VENUE_COL: '.mat-column-venue.mat-cell',
    CELL_IN_CITY_COL: '.mat-column-city.mat-cell',
    CELL_IN_TYPE_COL: '.mat-column-type.mat-cell',
    CELL_IN_DATE_COL: '.mat-column-start_date.mat-cell',
    CELL_IN_NAME_COL: '.cdk-column-name.mat-mdc-cell',
    CONTEXT_MSG: '.context-notification',
    DELETE_EVENT: '.mat-column-actions button',
    DELETE_EVENT_CONFIRM: '.mat-flat-button',
    NEW_EVENT_BUTTON: '.filter-container button.add-button',
    NEW_EVENT_ENTITY_SELECT: 'mat-select[formcontrolname="entity"]',
    NEW_EVENT_PRODUCER_SELECT: 'mat-select[formcontrolname="producer"]',
    NEW_EVENT_CATEGORY_SELECT: 'mat-select[formcontrolname="category"]',
    NEW_EVENT_TYPE_SELECT: 'mat-select[formcontrolname="type"]',
    NEW_EVENT_SELECT_OPTION: 'mat-option',
    NEW_EVENT_NAME_INPUT: 'input[formcontrolname="name"]',
    NEW_EVENT_CREATE_BUTTON: '.mat-mdc-dialog-actions .ob-button.mat-primary',
    NEXT_PAGE_BUTTON: '.paginator-container button:eq(1)',
    PREVIOUS_PAGE_BUTTON: '.paginator-container .previous-button',
    ROW_LIST: 'mat-row',
    selectStatusValues: function (values) {
        const that = this
        const valuesArr = values.split(',')
        valuesArr.forEach(function (value, index, arr) {
            const keyToMakeSelection = arr.length === index + 1 ? '{esc}' : '{backspace}'
            switch (value) {
                case 'PLANNED':
                    cy.get(that.FILTER_STATUS_OPTION).contains(/(?:Planificado|Planificat|Planned)/).type(keyToMakeSelection)
                    break;
                case 'IN_PROGRAMMING':
                    cy.get(that.FILTER_STATUS_OPTION).contains(/(?:En Programacion|En programació|In programming)/).type(keyToMakeSelection)
                    break;
                case 'READY':
                    cy.get(that.FILTER_STATUS_OPTION).contains(/(?:Preparado|Preparat|Ready)/).type(keyToMakeSelection)
                    break;
                case 'NOT_ACCOMPLISHED':
                    cy.get(that.FILTER_STATUS_OPTION).contains(/(?:No realizado|No realitzat|Not accomplished)/).type(keyToMakeSelection)
                    break;
                case 'CANCELLED':
                    cy.get(that.FILTER_STATUS_OPTION).contains(/(?:Cancelado|Cancel·lat|Cancelled)/).type(keyToMakeSelection)
                    break;
                case 'FINISHED':
                    cy.get(that.FILTER_STATUS_OPTION).contains(/(?:Finalizado|Finalitzat|Finished)/).type(keyToMakeSelection)
                    break;
                case 'IN_PROGRESS':
                    cy.get(that.FILTER_STATUS_OPTION).contains(/(?:En proceso|En procès|In progress)/).type(keyToMakeSelection)
                    break;
            }
        });
    },
    composeReqParams: function (filterObj, filter) {
        switch (filter) {
            case 'status':
                return `${filter}=${filterObj.values}`
            case 'type': {
                const value = (filterObj.key).toUpperCase()
                return `${filter}=${value}`
            }
            case 'country':
                return 'country=ES'
            case 'city': {
                const value = filterObj.key
                return `${filter}=${value}`
            }
            case 'entity':
            case 'producer':
            case 'venue': {
                const value = filterObj.valuesInReq
                return `${filter}_id=${value}`
            }
            case 'start_date': {
                const startValue = moment
                    .tz(filterObj.valuesArr[0], 'DD/MM/YYYY', 'Europe/Berlin')
                    .utc()
                    .format();
                const endValue = moment
                    .tz(filterObj.valuesArr[1], 'DD/MM/YYYY', 'Europe/Berlin')
                    .add(1, 'days')
                    .subtract(1, 'seconds')
                    .utc()
                    .format();
                if (endValue) {
                    return `${filter}=gte:${startValue},lte:${endValue}`
                } else {
                    return `${filter}=gte:${startValue}`
                }
            }
        }
    },
    composeUrlParams: function (filterObj, filter) {
        switch (filter) {
            case 'status':
                return `${filter}=${filterObj.values}`
            case 'type': {
                const value = (filterObj.key).toUpperCase()
                return `${filter}=${value}`
            }
            case 'country':
                return 'country=ES'
            case 'city': {
                const value = filterObj.key
                return `${filter}=${value}`
            }
            case 'entity':
            case 'producer':
            case 'venue': {
                const value = filterObj.valuesInReq
                return `${filter}=${value}`
            }
            case 'start_date': {
                const startValue = moment
                    .tz(filterObj.valuesArr[0], 'DD/MM/YYYY', 'Europe/Berlin')
                    .utc()
                    .format();
                const endValue = moment
                    .tz(filterObj.valuesArr[1], 'DD/MM/YYYY', 'Europe/Berlin')
                    .add(1, 'days')
                    .subtract(1, 'seconds')
                    .utc()
                    .format();
                if (endValue) {
                    return `startDate=${startValue}&endDate=${endValue}`
                } else {
                    return `startDate=${startValue}`
                }
            }
        }
    },
    isBetweenDates: function (text, start, end) {
        let $text = moment.utc(moment(text, 'L').format('YYYY-MM-DD'))
        let $start = moment.utc(moment(start, 'L').format('YYYY-MM-DD'))
        let $end = moment.utc(moment(end, 'L').format('YYYY-MM-DD'))
        return moment($text).isBetween($start, $end)
    }
}
export default elements;
