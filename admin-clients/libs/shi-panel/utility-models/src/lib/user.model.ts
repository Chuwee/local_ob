import { BasicUser } from '@admin-clients/shared/data-access/models';

export interface User extends BasicUser {
    status: UserStatus;
    surname?: string;
    language?: string;
    timezone?: string;
    api_key: string;
    scopes: [
        string
    ];
    role: UserRoles;
    permissions: UserPermissions[];
}

export enum UserStatus {
    active = 'ACTIVE',
    disabled = 'DISABLED'
}

export enum UserRoles {
    admin = 'ADMIN',
    owner = 'OWNER',
    user = 'USER'
}

export enum UserPermissions {
    exchangeRateRead = 'exchange_rate_read',
    exchangeRateWrite = 'exchange_rate_write',
    userWrite = 'user_write',
    userRead = 'user_read',
    listingWrite = 'listing_write',
    listingRead = 'listing_read',
    mappingWrite = 'mapping_write',
    mappingRead = 'mapping_read',
    matchingWrite = 'matching_write',
    matchingRead = 'matching_read',
    ingestorWrite = 'ingestor_write',
    ingestorRead = 'ingestor_read',
    configurationWrite = 'configuration_write',
    configurationRead = 'configuration_read',
    salesWrite = 'sales_write',
    salesRead = 'sales_read'
}
