import { ListResponse } from '@OneboxTM/utils-state';
import { Country, EntityCustomerType, Region } from '@admin-clients/shared/common/data-access';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { CustomerFriendRelation } from './customer-friends.model';

export enum CustomerGender {
    none = 'None',
    male = 'M',
    female = 'F',
    other = 'O'
}
export enum CustomerStatus {
    active = 'ACTIVE',
    locked = 'LOCKED',
    pending = 'PENDING'
}

export enum CustomerType {
    basic = 'BASIC',
    member = 'MEMBER'
}

export enum CustomerManagedType {
    manager = 'MANAGER',
    managed = 'MANAGED',
    linked = 'LINKED'
}

export interface CustomerProductClientIds {
    client_id: string;
    event: {
        id: number;
        name: string;
        type: string;
    };
    session: {
        id: number;
        name: string;
    };
}

export interface Customer {
    id: string;
    member_data: {
        admin_type: CustomerManagedType;
        manager: string;
        managed_users: number;
        id: string;
        allow_product_client_ids?: boolean;
        product_client_ids?: CustomerProductClientIds[];
    };
    birthday: string;
    email: string;
    entity: {
        id: number;
        name: string;
    };
    location: {
        postal_code: string;
        country?: Country;
        country_subdivision?: Region;
        city: string;
        address: string;
    };
    dates: {
        sign_up_date: string;
        last_import_date: string;
    };
    gender: CustomerGender;
    id_card: string;
    language: string;
    name: string;
    title?: CustomerTitle;
    surname: string;
    password: string;
    phone: string;
    phone_2?: string;
    address_2?: string;
    iban?: string;
    type: CustomerType;
    customer_types?: EntityCustomerType[];
    status: CustomerStatus;
    is_managed?: boolean;
    auth_providers?: AuthProviderType[];
    int_phone?: {
        phone?: string;
        phone_prefix?: string;
    };
    membership_start_date?: string;
}

export interface PostCustomer {
    birthday?: string;
    email: string;
    entity_id: number;
    gender?: CustomerGender;
    language?: string;
    id_card?: string;
    manager_id?: string;
    member_id?: string;
    name: string;
    title?: CustomerTitle;
    phone?: string;
    phone_2?: string;
    address_2?: string;
    surname: string;
    location?: {
        postal_code?: string;
        country_subdivision?: string;
        city?: string;
        country?: string;
        address?: string;
    };
    friend_id?: string;
    relation?: CustomerFriendRelation;
    int_phone?: {
        phone?: string;
        phone_prefix?: string;
    };
    vendor?: { id: string; name: string };
}

export interface PutCustomerProductClientIds {
    product_id?: number;
    client_id?: string;
}

export interface PutCustomer {
    title?: string;
    name?: string;
    surname?: string;
    email?: string;
    phone?: string;
    phone_2?: string;
    address_2?: string;
    gender?: CustomerGender;
    language?: string;
    birthday?: string;
    id_card?: string;
    type?: CustomerType;
    customer_types?: number[];
    member_data?: {
        id?: string;
        admin_type?: CustomerManagedType;
        manager?: string;
        allow_product_client_ids?: boolean;
        product_client_ids?: PutCustomerProductClientIds[];
    };
    location?: {
        postal_code?: string;
        country_subdivision?: string;
        city?: string;
        country?: string;
        address?: string;
    };
    int_phone?: {
        phone?: string;
        phone_prefix?: string;
    };
}

export enum CustomerApiCodeError {
    customerDuplicatedEmail = 'CUSTOMER_DUPLICATED_EMAIL',
    customerAlreadyExists = 'CUSTOMER_ALREADY_EXISTS',
    customerDuplicatedMemberId = 'CUSTOMER_DUPLICATED_MEMBER_ID'
}

export interface CustomerListItem {
    id: string;
    admin_type: CustomerManagedType;
    email: string;
    entity: {
        id: number;
        name: string;
    };
    id_card: string;
    member_data?: {
        manager: string;
        id: string;
        managed_users: number;
    };
    is_managed?: boolean;
    name: string;
    phone: string;
    sign_up_date: string;
    status: CustomerStatus;
    surname: string;
    type: CustomerType;
}

export interface GetCustomersResponse extends ListResponse<CustomerListItem> {
}

export interface GetCustomersRequest extends PageableFilter {
    endDate?: string;
    entityId?: string;
    productId?: number;
    clientId?: string;
    startDate?: string;
    status?: CustomerStatus[];
    type?: CustomerType;
}

export type CustomerFormField = {
    fieldSize: number;
    name: string;
    required: boolean;
    type: 'TEXT' | 'EMAIL' | 'MULTI_KEY';
    fields?: CustormerFormMultiField[];
    uneditable?: boolean;
};

export interface AuthProviderType {
    customer_id: string;
    id: string;
    type: string;
}

export interface CustormerFormMultiField {
    name: string;
    type: 'LIST' | 'TEXT';
    values?: CustomerMultiFieldValue[];
}

export interface CustomerMultiFieldValue {
    label: Record<string, string>;
    unicode: string;
    value: string;
}

export const customerTitle = [
    'MASTER',
    'MR',
    'MRS',
    'MISS',
    'MS',
    'C/O'

] as const;

export type CustomerTitle = typeof customerTitle[number];
