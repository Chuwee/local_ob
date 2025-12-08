import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataCollectiveCodes: FieldDataGroup[] = [
    {
        fieldKey: 'COLLECTIVE.CODES.EXPORT.TITLE',
        field: 'collective_codes_data',
        isDefault: true,
        fields: [
            {
                field: 'code',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.CODE',
                isDefault: true
            },
            {
                field: 'usage.limit',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.USAGE_LIMIT',
                isDefault: true
            },
            {
                field: 'usage.current',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.USAGE_CURRENT',
                isDefault: true
            },
            {
                field: 'validation_method',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDATION_METHOD',
                isDefault: false
            },
            {
                field: 'validity_period.from',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDITY_FROM',
                isDefault: true
            },
            {
                field: 'validity_period.to',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDITY_TO',
                isDefault: true
            }
        ]
    }
];
export const exportDataCollectiveCodesUser: FieldDataGroup[] = [
    {
        fieldKey: 'COLLECTIVE.CODES.EXPORT.TITLE',
        field: 'collective_codes_data',
        isDefault: true,
        fields: [
            {
                field: 'code',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.USER',
                isDefault: true
            },
            {
                field: 'usage.limit',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.USAGE_LIMIT',
                isDefault: true
            },
            {
                field: 'usage.current',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.USAGE_CURRENT',
                isDefault: true
            },
            {
                field: 'validation_method',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDATION_METHOD',
                isDefault: false
            },
            {
                field: 'validity_period.from',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDITY_FROM',
                isDefault: true
            },
            {
                field: 'validity_period.to',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDITY_TO',
                isDefault: true
            }
        ]
    }
];

export const exportDataCollectiveCodesUserPass: FieldDataGroup[] = [
    {
        fieldKey: 'COLLECTIVE.CODES.EXPORT.TITLE',
        field: 'collective_codes_data',
        isDefault: true,
        fields: [
            {
                field: 'code',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.USER',
                isDefault: true
            },
            {
                field: 'key',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.KEY',
                isDefault: true
            },
            {
                field: 'usage.limit',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.USAGE_LIMIT',
                isDefault: true
            },
            {
                field: 'usage.current',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.USAGE_CURRENT',
                isDefault: true
            },
            {
                field: 'validation_method',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDATION_METHOD',
                isDefault: false
            },
            {
                field: 'validity_period.from',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDITY_FROM',
                isDefault: true
            },
            {
                field: 'validity_period.to',
                fieldKey: 'COLLECTIVE.CODES.EXPORT.VALIDITY_TO',
                isDefault: true
            }
        ]
    }
];

