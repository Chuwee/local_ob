import { Filter } from '../../../../../modules/filters/models/filters.model';

export const sessionsFilter: Filter[] = [
    {
        target: 'sessions',
        key: 'venueTplId',
        filterName: 'TEMPLATE.VENUE_TEMPLATE',
        filterType: 'picker',
        filterTitle: 'TEMPLATE.SELECT_VENUE_TEMPLATE',
        filterplaceHolder: 'TEMPLATE.FIND_VENUE_TEMPLATE_PLACEHOLDER',
        filterOptions: [],
        isMultiple: false
    },
    {
        target: 'sessions',
        key: 'status',
        filterName: 'SESSION_STATUS.STATUS',
        filterType: 'buttons',
        filterTitle: '',
        filterplaceHolder: '',
        filterOptions: [],
        isMultiple: true
    },
    {
        target: 'sessions',
        key: 'hourRanges',
        filterName: 'HOUR_RANGE.HOUR_RANGE',
        filterType: 'hour_range',
        filterTitle: 'HOUR_RANGE.MODAL_TITLE',
        filterplaceHolder: '',
        filterOptions: [],
        isMultiple: true
    }
];
