import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataMappingsList: FieldDataGroup[] = [
    {
        fieldKey: 'MAPPINGS.MAPPINGS_LIST.EXPORT.TITLE',
        field: 'mappings',
        isDefault: false,
        fields: [
            {
                field: 'code',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.CODE',
                isDefault: true
            },
            {
                field: 'supplier',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.SUPPLIER',
                isDefault: true
            },
            {
                field: 'shi_id',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.SHI_ID',
                isDefault: true
            },
            {
                field: 'supplier_id',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.SUPPLIER_ID',
                isDefault: true
            },
            {
                field: 'name',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.NAME',
                isDefault: true
            },
            {
                field: 'date',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.DATE',
                isDefault: true
            },
            {
                field: 'status',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.STATUS',
                isDefault: true
            },
            {
                field: 'category',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.CATEGORY',
                isDefault: true
            },
            {
                field: 'country_code',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.COUNTRY_CODE',
                isDefault: true
            },
            {
                field: 'supplier_event_categories',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.SUPPLIER_EVENT_CATEGORIES',
                isDefault: true
            },
            {
                field: 'username',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.USERNAME',
                isDefault: true
            },
            {
                field: 'created',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.CREATE_DATE',
                isDefault: true
            },
            {
                field: 'updated',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.UPDATE_DATE',
                isDefault: true
            },
            {
                field: 'favorite',
                fieldKey: 'MAPPINGS.MAPPINGS_LIST_EXPORT.FAVORITE',
                isDefault: true
            }
        ]
    }
];

