import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataCapacity: FieldDataGroup[] = [
    {
        fieldKey: 'CAPACITY.EXPORT.TITLE',
        field: 'capacity_data',
        isDefault: true,
        fields: [
            {
                field: 'row_map.seat_map.id',
                fieldKey: 'CAPACITY.EXPORT.SEAT_ID',
                isDefault: true
            },
            {
                field: 'row_map.seat_map.status',
                fieldKey: 'CAPACITY.EXPORT.SEAT_STATUS',
                isDefault: true
            },
            {
                field: 'row_map.seat_map.blocking_reason',
                fieldKey: 'CAPACITY.EXPORT.SEAT_BLOCKING_REASON',
                isDefault: true
            },
            {
                field: 'row_map.sector',
                fieldKey: 'CAPACITY.EXPORT.SECTOR',
                isDefault: true
            },
            {
                field: 'row_map.name',
                fieldKey: 'CAPACITY.EXPORT.ROW_NAME',
                isDefault: true
            },
            {
                field: 'row_map.seat_map.name',
                fieldKey: 'CAPACITY.EXPORT.SEAT_NAME',
                isDefault: true
            },
            {
                field: 'not_numbered_zone_map.name',
                fieldKey: 'CAPACITY.EXPORT.NOT_NUMBERED_ZONE_NAME',
                isDefault: true
            },
            {
                field: 'row_map.seat_map.price_type',
                fieldKey: 'CAPACITY.EXPORT.SEAT_PRICE_TYPE',
                isDefault: true
            },
            {
                field: 'row_map.seat_map.quota',
                fieldKey: 'CAPACITY.EXPORT.SEAT_QUOTA',
                isDefault: true
            },
            {
                field: 'row_map.seat_map.visibility',
                fieldKey: 'CAPACITY.EXPORT.SEAT_VISIBILITY',
                isDefault: true
            },
            {
                field: 'row_map.seat_map.accessibility',
                fieldKey: 'CAPACITY.EXPORT.SEAT_ACCESSIBILITY',
                isDefault: true
            },
            {
                field: 'row_map.seat_map.gate',
                fieldKey: 'CAPACITY.EXPORT.SEAT_GATE',
                isDefault: true
            }
        ]
    }
];

