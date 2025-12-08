import { B2BData } from './b2b-data.model';

export interface InvoiceData {
    address: string;
    city: string;
    country: string;
    country_subdivision: string;
    id_number: string;
    id_number_type: string;
    name: string;
    zip_code: string;
};

export interface BuyerData {
    name: string;
    surname: string;
    email: string;
    b2b_data: B2BData;
    zip_code: string;
    address: string;
    phone: string;
    international_phone?: {
        prefix: string;
        number: string;
    };
    city: string;
    id_number: string;
    country: string;
    country_subdivision: string;
    language: string;
    gender: string;
    profile_data?: {
        id: number;
        name: string;
        attributes?: {
            id: number;
            code?: string;
            value: string;
        }[];
    };
    invoice?: InvoiceData;
    user_id?: string;
}
