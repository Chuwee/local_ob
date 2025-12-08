import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataErrorRates: FieldDataGroup[] = [
    {
        fieldKey: 'SALES.ERROR_DASHBOARD.EXPORT.TITLE',
        field: 'error_rates',
        isDefault: false,
        fields: [
            {
                field: 'error_responsible',
                fieldKey: 'SALES.SALES_LIST_EXPORT.ERROR_RESPONSIBLE',
                isDefault: true
            },
            {
                field: 'error_cause',
                fieldKey: 'SALES.ERROR_DASHBOARD_EXPORT.ERROR_CAUSE',
                isDefault: true
            },
            {
                field: 'error_count',
                fieldKey: 'SALES.ERROR_DASHBOARD_EXPORT.ERROR_COUNT',
                isDefault: true
            }
        ]
    }
];

