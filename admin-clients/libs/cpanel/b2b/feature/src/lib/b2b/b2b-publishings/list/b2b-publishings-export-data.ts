import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataB2bPublishing: FieldDataGroup[] = [
    {
        fieldKey: 'B2B_PUBLISHINGS.EXPORT.TICKET_DATA.TITLE',
        field: 'ticket_data',
        isDefault: true,
        fields: [
            {
                field: 'event.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.EVENT.NAME',
                isDefault: true
            },
            {
                field: 'event.id',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.EVENT.ID',
                isDefault: true
            },
            {
                field: 'session.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.SESSION.NAME',
                isDefault: true
            },
            {
                field: 'session.id',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.SESSION.ID',
                isDefault: true
            },
            {
                field: 'session.date',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.SESSION.DATE',
                isDefault: true
            },
            {
                field: 'channel.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CHANNEL.NAME',
                isDefault: true
            },
            {
                field: 'channel.id',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CHANNEL.ID',
                isDefault: true
            },
            {
                field: 'venue.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.VENUE.NAME',
                isDefault: true
            },
            {
                field: 'sector.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.SECTOR.NAME',
                isDefault: true
            },
            {
                field: 'row.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.ROW.NAME',
                isDefault: true
            },
            {
                field: 'seat.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.SEAT.NAME',
                isDefault: true
            },
            {
                field: 'transaction.type',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.TRANSACTION.TYPE',
                isDefault: true
            },
            {
                field: 'transaction.date',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.TRANSACTION.DATE',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'B2B_PUBLISHINGS.EXPORT.CLIENT_DATA.TITLE',
        field: 'client_data',
        isDefault: true,
        fields: [
            {
                field: 'client.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CLIENT.NAME',
                isDefault: true
            },
            {
                field: 'client.id',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CLIENT.ID',
                isDefault: true
            },
            {
                field: 'client.entityId',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CLIENT.ENTITYID',
                isDefault: true
            },
            {
                field: 'user.name',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.USER.NAME',
                isDefault: true
            },
            {
                field: 'user.id',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.USER.ID',
                isDefault: true
            },
            {
                field: 'user.publisherType',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.USER.PUBLISHERTYPE',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'B2B_PUBLISHINGS.EXPORT.ORDER_DATA.TITLE',
        field: 'order_data',
        isDefault: true,
        fields: [
            {
                field: 'price.base',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.PRICE.BASE',
                isDefault: true
            },
            {
                field: 'price.final',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.PRICE.FINAL',
                isDefault: true
            },
            {
                field: 'promo.isAutomatic',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.PROMO.ISAUTOMATIC',
                isDefault: true
            },
            {
                field: 'promo.promotion',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.PROMO.PROMOTION',
                isDefault: true
            },
            {
                field: 'promo.discount',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.PROMO.DISCOUNT',
                isDefault: true
            },
            {
                field: 'promo.channel.isAutomatic',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.PROMO.CHANNEL.ISAUTOMATIC',
                isDefault: true
            },
            {
                field: 'promo.channel.collective',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.PROMO.CHANNEL.COLLECTIVE',
                isDefault: true
            },
            {
                field: 'charge.channel',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CHARGE.CHANNEL',
                isDefault: true
            },
            {
                field: 'charge.promoter',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CHARGE.PROMOTER',
                isDefault: true
            },
            {
                field: 'charge.promoterChannel',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CHARGE.PROMOTERCHANNEL',
                isDefault: true
            },
            {
                field: 'charge.reallocation',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.CHARGE.REALLOCATION',
                isDefault: true
            },
            {
                field: 'fee.ids',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.FEE.IDS',
                isDefault: true
            },
            {
                field: 'fee.names',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.FEE.NAMES',
                isDefault: true
            },
            {
                field: 'fee.isUnitary',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.FEE.ISUNITARY',
                isDefault: true
            },
            {
                field: 'fee.values',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.FEE.VALUES',
                isDefault: true
            },
            {
                field: 'commission.ids',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.COMMISSION.IDS',
                isDefault: true
            },
            {
                field: 'commission.names',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.COMMISSION.NAMES',
                isDefault: true
            },
            {
                field: 'commission.values',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.COMMISSION.VALUES',
                isDefault: true
            },
            {
                field: 'order.code',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.ORDER.CODE',
                isDefault: true
            },
            {
                field: 'order.date',
                fieldKey: 'B2B_PUBLISHINGS.EXPORT.ORDER.DATE',
                isDefault: true
            }
        ]
    }
];

