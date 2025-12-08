import { CustomerFormField } from '@admin-clients/cpanel-viewers-customers-data-access';
import { FieldData, FieldDataGroup } from '@admin-clients/shared/data-access/models';

export const exportDataCustomers: FieldDataGroup[] = [
    {
        fieldKey: 'CUSTOMER.EXPORT.BASIC.TITLE',
        field: 'basic',
        isDefault: true,
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
                field: 'gender',
                fieldKey: 'CUSTOMER.GENDER',
                isDefault: false
            },
            {
                field: 'birthday',
                fieldKey: 'CUSTOMER.BIRTHDAY',
                isDefault: false
            },
            {
                field: 'id_card',
                fieldKey: 'CUSTOMER.ID_CARD',
                isDefault: false
            },
            {
                field: 'phone',
                fieldKey: 'CUSTOMER.PHONE',
                isDefault: false
            },
            {
                field: 'location.address',
                fieldKey: 'CUSTOMER.ADDRESS',
                isDefault: false
            },
            {
                field: 'location.city',
                fieldKey: 'CUSTOMER.CITY',
                isDefault: false
            },
            {
                field: 'location.postal_code',
                fieldKey: 'CUSTOMER.POSTAL_CODE',
                isDefault: false
            },
            {
                field: 'location.country_subdivision',
                fieldKey: 'CUSTOMER.PROVINCE',
                isDefault: false
            },
            {
                field: 'location.country',
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
        fieldKey: 'CUSTOMER.EXPORT.CLIENT.TITLE',
        field: 'member',
        isDefault: false,
        fields: [
            {
                field: 'member_data.id',
                fieldKey: 'CUSTOMER.MEMBER_ID',
                isDefault: false
            },
            {
                field: 'member_data.manager',
                fieldKey: 'CUSTOMER.MANAGER_EMAIL',
                isDefault: false
            }
        ]
    }
];

const loyaltyPointsFields: FieldDataGroup[] = [
    {
        fieldKey: 'CUSTOMER.EXPORT.LOYALTY_POINTS.TITLE',
        field: 'loyalty',
        isDefault: false,
        fields: [
            {
                field: 'loyalty_program.balance',
                fieldKey: 'CUSTOMER.EXPORT.POINTS',
                isDefault: false
            }
        ]
    }
];

export const customerTypesFields: FieldData[] = [
    {
        field: 'customer_types.name',
        fieldKey: 'CUSTOMER.EXPORT.CUSTOMER_TYPES',
        isDefault: false
    }
];

const additionalFields: FieldDataGroup[] = [
    {
        fieldKey: 'CUSTOMER.EXPORT.ADDITIONAL_FIELDS.TITLE',
        field: 'additional_fields',
        isDefault: false,
        fields: [
            {
                field: 'title',
                fieldKey: 'CUSTOMER.EXPORT.TITLE',
                isDefault: false
            },
            {
                field: 'phone_2',
                fieldKey: 'CUSTOMER.EXPORT.PHONE_2',
                isDefault: false
            },
            {
                field: 'address_2',
                fieldKey: 'CUSTOMER.EXPORT.ADDRESS_2',
                isDefault: false
            }
        ]
    }
];

export const externalVendorFields: FieldData[] = [
    {
        field: 'external_vendor.id',
        fieldKey: 'CUSTOMER.EXTERNAL_VENDOR_ID',
        isDefault: false
    },
    {
        field: 'external_vendor.customer_id',
        fieldKey: 'CUSTOMER.EXTERNAL_VENDOR_CUSTOMER_ID',
        isDefault: false
    }
];

export const getAdditionalFields = (adminCustomerFields: CustomerFormField[]): FieldDataGroup => {
    const adminCustomerFieldKeys = adminCustomerFields.map(field => field.name);
    const fieldToReturn = additionalFields.find(field => field.field === 'additional_fields');
    fieldToReturn.fields = fieldToReturn.fields.filter(f => adminCustomerFieldKeys.includes(f.field));
    return fieldToReturn;
};

export const exportDataCustomersPoints = [...exportDataCustomers, ...loyaltyPointsFields];
