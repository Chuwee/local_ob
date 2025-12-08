import { Filter } from '../../../modules/filters/models/filters.model';

export const salesFilters: Filter[] = [
    {
        target: 'tickets',
        key: 'channel_entity_id',
        filterName: 'CHANNEL-MANAGER',
        filterType: 'checkboxlist',
        filterTitle: 'CHANNEL-MANAGER-TITLE',
        filterplaceHolder: 'CHANNEL-MANAGER-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'tickets',
        key: 'event_entity_id',
        filterName: 'ORGANIZER',
        filterType: 'checkboxlist',
        filterTitle: 'ORGANIZER-TITLE',
        filterplaceHolder: 'ORGANIZER-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'tickets',
        key: 'channel_id',
        filterName: 'CHANNEL',
        filterType: 'checkboxlist',
        filterTitle: 'CHANNEL-TITLE',
        filterplaceHolder: 'CHANNEL-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'tickets',
        key: 'state',
        filterName: 'STATE',
        filterType: 'picker',
        filterTitle: 'STATE-TITLE',
        filterplaceHolder: 'STATE-PLACEHOLDER',
        filterOptions: [],
        isMultiple: false
    },
    {
        target: 'tickets',
        key: 'print',
        filterName: 'PRINT-STATUS',
        filterType: 'picker',
        filterTitle: 'PRINT-STATUS-TITLE',
        filterplaceHolder: 'PRINT-STATUS-PLACEHOLDER',
        filterOptions: [],
        isMultiple: false
    },
    {
        target: 'tickets',
        key: 'validation',
        filterName: 'ACCESS-CONTROL',
        filterType: 'picker',
        filterTitle: 'ACCESS-CONTROL-TITLE',
        filterplaceHolder: 'ACCESS-CONTROL-PLACEHOLDER',
        filterOptions: [],
        isMultiple: false
    },
    {
        target: 'tickets',
        key: 'ticket_type',
        filterName: 'INVITATIONS',
        filterType: 'picker',
        filterTitle: 'INVITATIONS-TITLE',
        filterplaceHolder: 'INVITATIONS-PLACEHOLDER',
        filterOptions: [],
        isMultiple: false
    },
    {
        target: 'tickets',
        key: 'event_id',
        filterName: 'EVENTS',
        filterType: 'checkboxlist',
        filterTitle: 'EVENTS-TITLE',
        filterplaceHolder: 'EVENTS-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'tickets',
        key: 'session_id',
        filterName: 'SESSIONS',
        filterType: 'checkboxlist',
        filterTitle: 'SESSIONS-TITLE',
        filterplaceHolder: 'SESSIONS-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'tickets',
        key: 'sector_id',
        filterName: 'SECTOR',
        filterType: 'checkboxlist',
        filterTitle: 'SECTOR-TITLE',
        filterplaceHolder: 'SECTOR-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'tickets',
        key: 'price_type_id',
        filterName: 'PRICE-AREA',
        filterType: 'checkboxlist',
        filterTitle: 'PRICE-AREA-TITLE',
        filterplaceHolder: 'PRICE-AREA-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'transactions',
        key: 'event_entity_id',
        filterName: 'ORGANIZER',
        filterType: 'checkboxlist',
        filterTitle: 'ORGANIZER-TITLE',
        filterplaceHolder: 'ORGANIZER-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'transactions',
        key: 'channel_id',
        filterName: 'CHANNEL',
        filterType: 'checkboxlist',
        filterTitle: 'CHANNEL-TITLE',
        filterplaceHolder: 'CHANNEL-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'transactions',
        key: 'type',
        filterName: 'TYPE',
        filterType: 'checkboxlist',
        filterTitle: 'TYPE-TITLE',
        filterplaceHolder: 'TYPE-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'transactions',
        key: 'code',
        filterName: 'COMMERCIAL-CODE',
        filterType: 'picker',
        filterTitle: 'COMMERCIAL-CODE-TITLE',
        filterplaceHolder: 'COMMERCIAL-CODE-PLACEHOLDER',
        filterOptions: [],
        isMultiple: false
    },
    {
        target: 'transactions',
        key: 'event_id',
        filterName: 'EVENTS',
        filterType: 'checkboxlist',
        filterTitle: 'EVENTS-TITLE',
        filterplaceHolder: 'EVENTS-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'transactions',
        key: 'session_id',
        filterName: 'SESSIONS',
        filterType: 'checkboxlist',
        filterTitle: 'SESSIONS-TITLE',
        filterplaceHolder: 'SESSIONS-PLACEHOLDER',
        filterOptions: [],
        isMultiple: true
    }
];
