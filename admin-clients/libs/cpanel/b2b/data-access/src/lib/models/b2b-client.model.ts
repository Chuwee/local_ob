import { Country, Region } from '@admin-clients/shared/common/data-access';

export interface B2bClientReduced {
    id: number;
    entity: {
        id: number;
        name: string;
    };
    client_id: number;
    name: string;
    category_type: B2bClientCategoryType;
    tax_id: string;
    iata_code?: string;
    business_name: string;
    creation_date: string;
    status: B2bClientStatus;
    country: Country;
    country_subdivision: Region;
    contact_data: {
        contact_person: string;
        address?: string;
        email: string;
        phone: string;
    };
}

export enum B2bClientCategoryType {
    agency = 'AGENCY',
    sponsor = 'SPONSOR',
    publicAdmin = 'PUBLIC_ADMINISTRATION',
    company = 'COMPANY'
}

export enum B2bClientStatus {
    active = 'ACTIVE',
    inactive = 'INACTIVE'
}

export enum B2bUserType {
    admin = 'ADMIN',
    guest = 'GUEST'
}

export interface B2bClient extends B2bClientReduced {
    description?: string;
    keywords?: string[];
    users: B2bClientUser[];
}

export interface PostB2bClient extends Omit<
    B2bClient, 'users' | 'id' | 'client_id' | 'creation_date' | 'status' | 'country' | 'country_subdivision' | 'entity'
> {
    entity_id?: number;
    country: Partial<Country>;
    country_subdivision: Partial<Region>;
    user: Pick<B2bClientUser, 'username' | 'name' | 'email'>;
}

export interface PutB2bClient extends Partial<Omit<PostB2bClient, 'iata_code' | 'user' | 'contact_data'>> {
    contact_data?: Partial<{
        contact_person: string;
        address: string;
        email: string;
        phone: string;
    }>;
}

export interface B2bClientUser {
    id: number;
    client_id: number;
    username: string;
    name: string;
    email: string;
    creation_date: string; // date-time
    type: B2bUserType;
    external_reference?: string
    api_key: string;
}

export interface PutB2bClientUser extends Partial<Pick<B2bClientUser, 'name' | 'email' | 'type' | 'external_reference'>> {
    entity_id?: number;
}

export interface PostB2bClientUser extends Pick<B2bClientUser, 'username' | 'name' | 'email' | 'type' | 'external_reference'> {
    entity_id?: number;
    password?: string;
}

export interface PostB2bClientUserApiKey extends Pick<B2bClientUser, 'api_key'> { }
