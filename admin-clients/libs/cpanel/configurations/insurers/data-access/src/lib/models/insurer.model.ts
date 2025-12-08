import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface Insurer {
    id: number;
    name: string;
    tax_id: string;
    tax_name: string;
    contact_email: string;
    address?: string;
    description?: string;
    zip_code: string;
    phone: string;
    operator?: {
        id: number;
        name: string;
    };
}

export interface InsurerListElem extends Omit<Insurer, 'tax_id' | 'address' | 'description' | 'zip_code'> { }

export class GetInsurersReq implements PageableFilter {
    limit: number;
    offset?: number;

    constructor() {
        this.limit = 20;
        this.offset = 0;
    }
}

export interface PostInsurer {
    name: string;
    operator_id: number;
    tax_id: string;
    tax_name: string;
    shard: string;
    contact_email: string;
    address: string;
    zip_code: string;
    phone: string;
}
