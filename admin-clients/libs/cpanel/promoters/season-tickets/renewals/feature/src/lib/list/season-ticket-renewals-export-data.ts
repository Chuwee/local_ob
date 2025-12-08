import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataSeasonTicketRenewal: FieldDataGroup[] = [
    {
        fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT.CUSTOMER.TITLE',
        field: 'customer',
        isDefault: false,
        fields: [
            {
                field: 'name',
                fieldKey: 'CUSTOMER.NAME',
                isDefault: true
            },
            {
                field: 'surname',
                fieldKey: 'CUSTOMER.SURNAME',
                isDefault: true
            },
            {
                field: 'email',
                fieldKey: 'CUSTOMER.EMAIL',
                isDefault: true
            },
            {
                field: 'birthday',
                fieldKey: 'CUSTOMER.BIRTHDAY',
                isDefault: false
            },
            {
                field: 'gender',
                fieldKey: 'CUSTOMER.GENDER',
                isDefault: false
            },
            {
                field: 'id_card',
                fieldKey: 'CUSTOMER.ID_CARD',
                isDefault: false
            },
            {
                field: 'phone_number',
                fieldKey: 'CUSTOMER.PHONE',
                isDefault: false
            },
            {
                field: 'address',
                fieldKey: 'CUSTOMER.ADDRESS',
                isDefault: false
            },
            {
                field: 'city',
                fieldKey: 'CUSTOMER.CITY',
                isDefault: false
            },
            {
                field: 'postal_code',
                fieldKey: 'CUSTOMER.POSTAL_CODE',
                isDefault: false
            },
            {
                field: 'country_subdivision',
                fieldKey: 'CUSTOMER.COUNTRY_SUBDIVISION',
                isDefault: false
            },
            {
                field: 'country',
                fieldKey: 'CUSTOMER.COUNTRY',
                isDefault: false
            },
            {
                field: 'language',
                fieldKey: 'CUSTOMER.LANGUAGE',
                isDefault: false
            },
            {
                field: 'sign_up_date',
                fieldKey: 'CUSTOMER.SIGN_UP_DATE',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT.TITLE',
        field: 'renewals',
        isDefault: false,
        fields: [
            {
                field: 'id',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.ID',
                isDefault: true
            },
            {
                field: 'historic_seat.sector',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.HISTORIC_SEAT.SECTOR',
                isDefault: true
            },
            {
                field: 'historic_seat.row',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.HISTORIC_SEAT.ROW',
                isDefault: true
            },
            {
                field: 'historic_seat.seat',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.HISTORIC_SEAT.SEAT',
                isDefault: true
            },
            {
                field: 'actual_seat.sector',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.ACTUAL_SEAT.SECTOR',
                isDefault: true
            },
            {
                field: 'actual_seat.row',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.ACTUAL_SEAT.ROW',
                isDefault: true
            },
            {
                field: 'actual_seat.seat',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.ACTUAL_SEAT.SEAT',
                isDefault: true
            },
            {
                field: 'actual_rate',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.RATE',
                isDefault: false
            },
            {
                field: 'renewal_status',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.RENEWAL_STATUS',
                isDefault: false
            },
            {
                field: 'balance',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.BALANCE',
                isDefault: false
            },
            {
                field: 'auto_renewal',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.AUTO_RENEWAL',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT.MEMBER.TITLE',
        field: 'member',
        isDefault: false,
        fields: [
            {
                field: 'member_id',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.MEMBER_ID',
                isDefault: false
            },
            {
                field: 'product_client_id',
                fieldKey: 'SEASON_TICKET.RENEWALS.EXPORT_LIST.PRODUCT_CLIENT_ID',
                isDefault: false
            }
        ]
    }
];
