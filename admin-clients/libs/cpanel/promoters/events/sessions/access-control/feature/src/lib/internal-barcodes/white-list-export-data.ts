import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataWhiteList: FieldDataGroup[] = [
    {
        fieldKey: 'SESSION.WHITE_LIST.EXPORT.TITLE',
        field: 'white_list',
        isDefault: false,
        fields: [
            {
                field: 'barcode',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.BARCODE',
                isDefault: true
            },
            {
                field: 'status',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.STATUS',
                isDefault: true
            },
            {
                field: 'event.id',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.EVENT_ID',
                isDefault: true
            },
            {
                field: 'event.name',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.EVENT_NAME',
                isDefault: true
            },
            {
                field: 'session.id',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.SESSION_ID',
                isDefault: true
            },
            {
                field: 'session.name',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.SESSION_NAME',
                isDefault: true
            },
            {
                field: 'session.start_date',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.SESSION_START_DATE',
                isDefault: true
            },
            {
                field: 'view',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.VIEW',
                isDefault: true
            },
            {
                field: 'row',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.ROW',
                isDefault: true
            },
            {
                field: 'seat',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.SEAT',
                isDefault: true
            },
            {
                field: 'gate',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.GATE',
                isDefault: true
            },
            {
                field: 'sector',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.SECTOR',
                isDefault: true
            },
            {
                field: 'price_type',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.PRICE_TYPE',
                isDefault: true
            },
            {
                field: 'related_session.id',
                fieldKey: 'SESSION.WHITE_LIST.EXPORT.RELATED_SESSION_ID',
                isDefault: true
            }
        ]
    }
];

