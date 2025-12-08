import { CustomerFormField, CustomerGender, CustomerTitle } from '@admin-clients/cpanel-viewers-customers-data-access';
import { Entity, EventType } from '@admin-clients/shared/common/data-access';
import { CsvHeaderMappingField } from '@admin-clients/shared/common/feature/csv';

export interface CsvCustomerBasic {
    address?: string;
    address_2?: string;
    birthday?: string;
    city?: string;
    country?: string;
    email?: string;
    gender?: CustomerGender;
    id_card?: string;
    name?: string;
    title?: CustomerTitle;
    phone?: string;
    phone_2?: string;
    postal_code?: string;
    country_subdivision?: string;
    surname?: string;
    customer_types?: string;
    member_id?: string;
    manager_email?: string;
    iban?: string;
    bic?: string;
    external_id?: string;
    membership_start_date?: string;
}

interface CsvCustomerProductBase {
    event_id?: number;
    event_name?: string;
    event_type?: EventType;
    sector_name?: string;
    price_zone_name?: string;
    rate_name?: string;
    purchase_date?: string;
    product_client_id?: string;
    auto_renewal?: string;
}

export interface CsvCustomerProductOnlyNumbered extends CsvCustomerProductBase {
    row_name?: string;
    seat_name?: string;
}

export interface CsvCustomerProductOnlyNotNumbered extends CsvCustomerProductBase {
    not_numbered_zone_name?: string;
}

export type CsvCustomerProduct = CsvCustomerProductOnlyNumbered & CsvCustomerProductOnlyNotNumbered;

export type CsvCustomer =
    CsvCustomerBasic
    & CsvCustomerProduct;
export type CsvCustomerValueTypes = CsvCustomer[keyof CsvCustomer];

export function createCsvCustomerMappingFields(
    hasEntityCustomerTypes: boolean,
    customerFields: CustomerFormField[],
    isProductsImport: boolean = false,
    vendorConfig: Entity['settings']['external_integration']['auth_vendor'] = null
): CsvHeaderMappingField<CsvCustomerBasic>[] {
    const customerFieldsKeys = (customerFields || [])?.map(field => field.name);
    return [
        { key: 'name', header: 'CUSTOMER.NAME', columnIndex: null, required: true, example: 'Name' },
        { key: 'surname', header: 'CUSTOMER.SURNAME', columnIndex: null, required: true, example: 'Surname' },
        { key: 'email', header: 'CUSTOMER.EMAIL', columnIndex: null, required: true, example: 'example@example.com' },
        { key: 'gender', header: 'CUSTOMER.GENDER', columnIndex: null, example: 'M' },
        { key: 'birthday', header: 'CUSTOMER.BIRTHDAY', columnIndex: null, example: '1997-07-07' },
        { key: 'id_card', header: 'CUSTOMER.ID_CARD', columnIndex: null, example: '11111111a' },
        { key: 'phone', header: 'CUSTOMER.PHONE', columnIndex: null, example: '987987987' },
        { key: 'address', header: 'CUSTOMER.ADDRESS', columnIndex: null, example: '250 street 1' },
        { key: 'city', header: 'CUSTOMER.CITY', columnIndex: null, example: 'City U' },
        { key: 'postal_code', header: 'CUSTOMER.POSTAL_CODE', columnIndex: null, example: '8029' },
        { key: 'country_subdivision', header: 'CUSTOMER.COUNTRY_SUBDIVISION', columnIndex: null, example: 'PT-01' },
        { key: 'country', header: 'CUSTOMER.COUNTRY', columnIndex: null, example: 'PT' },
        { key: 'member_id', header: 'CUSTOMER.MEMBER_ID', columnIndex: null, example: 'IMP01' },
        { key: 'manager_email', header: 'CUSTOMER.MANAGER_EMAIL', columnIndex: null, example: 'admin@example.com' },
        ...(customerFieldsKeys.includes('title') ? [{ key: 'title' as keyof CsvCustomerBasic, header: 'CUSTOMER.TITLE', columnIndex: null, required: false, example: 'MR' }] : []),
        ...(customerFieldsKeys.includes('address_2') ? [{ key: 'address_2' as keyof CsvCustomerBasic, header: 'CUSTOMER.ADDRESS_2', columnIndex: null, example: '250 street 2' }] : []),
        ...(customerFieldsKeys.includes('phone_2') ? [{ key: 'phone_2' as keyof CsvCustomerBasic, header: 'CUSTOMER.PHONE_2', columnIndex: null, example: '999888777' }] : []),
        ...(hasEntityCustomerTypes ?
            [{ key: 'customer_types' as keyof CsvCustomerBasic, header: 'CUSTOMER.CUSTOMER_TYPES', columnIndex: null, example: 'CODE_1' }] : []),
        ...(isProductsImport ? [
            { key: 'iban' as keyof CsvCustomerBasic, header: 'CUSTOMER.IBAN', columnIndex: null, required: false, example: 'ES0000000000000000000000' },
            { key: 'bic' as keyof CsvCustomerBasic, header: 'CUSTOMER.BIC', columnIndex: null, required: false, example: 'CAIXESBBXXX' }
        ] : []),
        ...(vendorConfig?.enabled ? [{
            key: 'external_id' as keyof CsvCustomerBasic, header: 'CUSTOMER.EXTERNAL_ID', columnIndex: null, required: true, example: '123456789'
        }] : []),
        { key: 'membership_start_date', header: 'CUSTOMER.MEMBERSHIP_START_DATE', columnIndex: null, example: '2025-06-23' },
    ];
}

function createCsvCustomerProductsBaseMappingFields(): CsvHeaderMappingField<CsvCustomerProductBase>[] {
    return [
        { key: 'event_id', header: 'CUSTOMER.PRODUCT_EVENT_ID', columnIndex: null, required: true, example: '201' },
        { key: 'event_name', header: 'CUSTOMER.PRODUCT_EVENT', columnIndex: null, required: true, example: 'Event Name' },
        { key: 'sector_name', header: 'CUSTOMER.PRODUCT_SECTOR', columnIndex: null, required: true, example: 'Sector A' },
        { key: 'event_type', header: 'CUSTOMER.PRODUCT_TYPE', columnIndex: null, required: true, example: 'SEASON_TICKET' },
        {
            key: 'price_zone_name', header: 'CUSTOMER.PRODUCT_PRICE_ZONE_NAME', columnIndex: null, required: true,
            example: 'Price A'
        },
        { key: 'rate_name', header: 'CUSTOMER.PRODUCT_RATE', columnIndex: null, required: true, example: 'Rate A' },
        {
            key: 'purchase_date', header: 'CUSTOMER.PRODUCT_PURCHASE_DATE', columnIndex: null, required: true,
            example: '2021-11-21'
        },
        { key: 'product_client_id', header: 'CUSTOMER.PRODUCT_CLIENT_ID', columnIndex: null, example: '123' },
        { key: 'auto_renewal', header: 'CUSTOMER.AUTO_RENEWAL', columnIndex: null, required: false, example: 'false' }
    ];
}

export function createCsvCustomerProductsOnlyNumberedMappingFields(): CsvHeaderMappingField<CsvCustomerProductOnlyNumbered>[] {
    const csvCustomerProductOnlyNumbered: CsvHeaderMappingField<CsvCustomerProductOnlyNumbered>[] = [
        { key: 'row_name', header: 'CUSTOMER.PRODUCT_ROW', columnIndex: null, required: true, example: '1' },
        { key: 'seat_name', header: 'CUSTOMER.PRODUCT_SEAT', columnIndex: null, required: true, example: '1' }
    ];

    return csvCustomerProductOnlyNumbered.concat(createCsvCustomerProductsBaseMappingFields());
}

export function createCsvCustomerProductsOnlyNotNumberedMappingFields(): CsvHeaderMappingField<CsvCustomerProductOnlyNotNumbered>[] {
    const csvCustomerProductOnlyNotNumbered: CsvHeaderMappingField<CsvCustomerProductOnlyNotNumbered>[] = [
        {
            key: 'not_numbered_zone_name', header: 'CUSTOMER.PRODUCT_NOT_NUMBERED_ZONE', columnIndex: null, required: true,
            example: '1'
        }
    ];

    return csvCustomerProductOnlyNotNumbered.concat(createCsvCustomerProductsBaseMappingFields());
}

export function createCsvCustomerProductsMappingFields(): CsvHeaderMappingField<CsvCustomerProduct>[] {
    const csvCustomerProductOnlyNumbered: CsvHeaderMappingField<CsvCustomerProductOnlyNumbered>[] = [
        { key: 'row_name', header: 'CUSTOMER.PRODUCT_ROW', columnIndex: null, required: true, example: '1' },
        { key: 'seat_name', header: 'CUSTOMER.PRODUCT_SEAT', columnIndex: null, required: true, example: '1' }
    ];

    const csvCustomerProductOnlyNotNumbered: CsvHeaderMappingField<CsvCustomerProductOnlyNotNumbered>[] = [
        {
            key: 'not_numbered_zone_name', header: 'CUSTOMER.PRODUCT_NOT_NUMBERED_ZONE', columnIndex: null, required: true,
            example: ''
        }
    ];

    return csvCustomerProductOnlyNumbered
        .concat(csvCustomerProductOnlyNotNumbered)
        .concat(createCsvCustomerProductsBaseMappingFields());
}

