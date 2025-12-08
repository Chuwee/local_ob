import { Filter } from '../../../modules/filters/models/filters.model';

export const eventFilters: Filter[] = [

    {
        target: 'events',
        key: 'producerId',
        filterName: 'PRODUCER.PRODUCER',
        filterType: 'picker',
        filterTitle: 'PRODUCER.SELECT-PRODUCER',
        filterplaceHolder: 'PRODUCER.FIND-PRODUCER-PLACEHOLDER',
        filterOptions: [],
        value: [],
        isMultiple: false
    },
    {
        target: 'events',
        key: 'country',
        filterName: 'COUNTRY.COUNTRY',
        filterType: 'picker',
        filterTitle: 'COUNTRY.SELECT-COUNTRY',
        filterplaceHolder: 'COUNTRY.FIND-COUNTRY-PLACEHOLDER',
        filterOptions: [],
        value: null,
        isMultiple: false
    },
    {
        target: 'events',
        key: 'status',
        filterName: 'STATUS.STATUS',
        filterType: 'buttons',
        filterTitle: 'STATUS.STATUS',
        filterplaceHolder: 'STATUS.STATUS',
        filterOptions: [],
        value: [],
        isMultiple: true
    },
    {
        target: 'events',
        key: 'venueId',
        filterName: 'VENUE.VENUE',
        filterType: 'picker',
        filterTitle: 'VENUE.SELECT-VENUE',
        filterplaceHolder: 'VENUE.FIND-VENUE-PLACEHOLDER',
        filterOptions: [],
        value: null,
        isMultiple: false
    },
    {
        target: 'events',
        key: 'city',
        filterName: 'CITY.CITY',
        filterType: 'picker',
        filterTitle: 'CITY.SELECT-CITY',
        filterplaceHolder: 'CITY.FIND-CITY-PLACEHOLDER',
        filterOptions: [],
        value: [],
        isMultiple: false
    },
    {
        target: 'events',
        key: 'type',
        filterName: 'EVENT_TYPE.EVENT_TYPE',
        filterType: 'picker',
        filterTitle: 'EVENT_TYPE.SELECT-EVENT_TYPE',
        filterplaceHolder: 'EVENT_TYPE.FIND-EVENT_TYPE-PLACEHOLDER',
        filterOptions: [],
        isMultiple: false,
        value: null
    },
    {
        target: 'events',
        key: 'includeArchived',
        filterName: 'ARCHIVED',
        filterType: 'checkbox',
        filterTitle: '',
        filterplaceHolder: '',
        filterOptions: [],
        isMultiple: false,
        value: false
    }
];
