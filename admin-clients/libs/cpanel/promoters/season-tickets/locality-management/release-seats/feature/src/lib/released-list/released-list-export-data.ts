import { FieldDataGroup } from '@admin-clients/shared/data-access/models';

// TODO: change fields when back is finished
export const exportDataReleasedList: FieldDataGroup[] = [
    {
        fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.TITLE',
        field: 'personal_data',
        isDefault: false,
        fields: [
            {
                field: 'name',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.NAME',
                isDefault: true
            },
            {
                field: 'surname',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.SURNAME',
                isDefault: true
            },
            {
                field: 'email',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.EMAIL',
                isDefault: true
            },
            {
                field: 'gender',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.GENDER',
                isDefault: false
            },
            {
                field: 'birthday',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.BIRTHDAY',
                isDefault: false
            },
            {
                field: 'id_card',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.ID_CARD',
                isDefault: false
            },
            {
                field: 'phone_number',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.PHONE',
                isDefault: false
            },
            {
                field: 'address',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.ADDRESS',
                isDefault: false
            },
            {
                field: 'city',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.CITY',
                isDefault: false
            },
            {
                field: 'postal_code',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.POSTAL_CODE',
                isDefault: false
            },
            {
                field: 'country_subdivision',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.COUNTRY_SUBDIVISION',
                isDefault: false
            },
            {
                field: 'country',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.COUNTRY',
                isDefault: false
            },
            {
                field: 'language',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.LANGUAGE',
                isDefault: false
            },
            {
                field: 'sign_up_date',
                fieldKey: 'RELEASED.EXPORT.PERSONAL_DATA.SIGN_UP_DATE',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'RELEASED.EXPORT.MEMBER_DATA.TITLE',
        field: 'member_data',
        isDefault: false,
        fields: [
            {
                field: 'member_id',
                fieldKey: 'RELEASED.EXPORT.MEMBER_DATA.MEMBER_ID',
                isDefault: false
            },
            {
                field: 'product_client_id',
                fieldKey: 'RELEASED.EXPORT.MEMBER_DATA.PRODUCT_CLIENT_ID',
                isDefault: false
            },
            {
                field: 'manager_email',
                fieldKey: 'RELEASED.EXPORT.MEMBER_DATA.MANAGER_EMAIL',
                isDefault: false
            }
        ]
    },
    {
        fieldKey: 'RELEASED.EXPORT.RELEASED_DATA.TITLE',
        field: 'released_data',
        isDefault: false,
        fields: [
            {
                field: 'release_status',
                fieldKey: 'RELEASED.EXPORT.RELEASED_DATA.RELEASE_STATUS',
                isDefault: false
            },
            {
                field: 'release.price',
                fieldKey: 'RELEASED.EXPORT.RELEASED_DATA.PRICE',
                isDefault: true
            },
            {
                field: 'earnings',
                fieldKey: 'RELEASED.EXPORT.RELEASED_DATA.EARNINGS',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'RELEASED.EXPORT.OPERATION_DATA.TITLE',
        field: 'operation_data',
        isDefault: false,
        fields: [
            {
                field: 'order.code',
                fieldKey: 'RELEASED.EXPORT.OPERATION_DATA.ORDER.CODE',
                isDefault: true
            },
            {
                field: 'order.date',
                fieldKey: 'RELEASED.EXPORT.OPERATION_DATA.ORDER.DATE',
                isDefault: true
            },
            {
                field: 'channel',
                fieldKey: 'RELEASED.EXPORT.OPERATION_DATA.CHANNEL',
                isDefault: true
            }
        ]
    },
    {
        fieldKey: 'RELEASED.EXPORT.TICKET_DATA.TITLE',
        field: 'ticket_data',
        isDefault: false,
        fields: [
            {
                field: 'event_name',
                fieldKey: 'RELEASED.EXPORT.TICKET_DATA.EVENT_NAME',
                isDefault: true
            },
            {
                field: 'session.name',
                fieldKey: 'RELEASED.EXPORT.TICKET_DATA.SESSION.NAME',
                isDefault: true
            },
            {
                field: 'session.date',
                fieldKey: 'RELEASED.EXPORT.TICKET_DATA.SESSION.DATE',
                isDefault: true
            },
            {
                field: 'venue',
                fieldKey: 'RELEASED.EXPORT.TICKET_DATA.VENUE',
                isDefault: false
            },
            {
                field: 'promoter',
                fieldKey: 'RELEASED.EXPORT.TICKET_DATA.PROMOTER',
                isDefault: false
            }
        ]
    }
];

