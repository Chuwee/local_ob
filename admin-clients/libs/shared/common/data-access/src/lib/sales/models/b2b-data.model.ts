import { B2BCategoryType } from './b2b-category-type.enum';

export interface B2BData {
    name: string;
    business_name: string;
    tax_id: string;
    category: B2BCategoryType;
    contact_name: string;
    contact_phone: string;
    contact_email: string;
}
