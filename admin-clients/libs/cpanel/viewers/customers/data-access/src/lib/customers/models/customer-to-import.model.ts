import { EventType } from '@admin-clients/shared/common/data-access';
import { WsMsg, WsCustomerMsgType } from '@admin-clients/shared/core/data-access';
import { CustomerGender, CustomerTitle } from './customer.model';

export interface CustomerBasicToImport {
    birthday?: string;
    email: string;
    gender?: CustomerGender;
    id_card?: string;
    title?: CustomerTitle;
    name: string;
    phone?: string;
    phone_2?: string;
    surname: string;
    customer_types?: string[];
    address_2?: string;
    location?: {
        address?: string;
        country?: string;
        city?: string;
        postal_code?: string;
        country_subdivision?: string;
    };
    iban?: string;
    bic?: string;
    auth_vendors?: { id: string; vendor: string }[];
}

export interface CustomerMemberToImport {
    member_id?: string;
    manager_email?: string;
    membership_start_date?: string;
}

export interface CustomerProductToImport {
    event?: {
        id: number;
        type: EventType;
        name: string;
    };
    sector_name?: string;
    row_name?: string;
    seat_name?: string;
    not_numbered_zone_name?: string;
    price_zone_name?: string;
    rate_name?: string;
    purchase_date?: string;
    product_client_id?: string;
    auto_renewal?: boolean;
}

export interface CustomerToImport extends CustomerMemberToImport, CustomerBasicToImport {
    products?: CustomerProductToImport[];
}

export interface PostCustomersToImport {
    entity_id: number;
    overwrite_data: boolean;
    import_products: boolean;
    import_customers_as_members?: boolean;
    email_report_destination?: string;
    customers?: CustomerToImport[];
}

export interface CustomersImportResponse {
    import_process_code: number;
}

export interface WsCustomersImportMessageData {
    created: number;
    updated: number;
    errors: number;
    products: number;
}

export type WsCustomersImportMessage = WsMsg<WsCustomerMsgType, WsCustomersImportMessageData>;
