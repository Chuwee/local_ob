import { IdName } from '@admin-clients/shared/data-access/models';

export type ProfessionalSellingChannel = {
    channel: {
        id: number;
        name: string;
    };
    season_ticket?: {
        id: number;
        status: string;
    };
    event?: {
        id: number;
        status: string;
    };
    quotas: {
        id: number;
        description: string;
        template_name: string;
        selected: boolean;
    }[];
};

export interface ChannelB2bAssignations<T = IdName> {
    type: QuotaAssignationsType;
    assignations?: QuotaAssignations<T>[];
}

export interface QuotaAssignations<T> {
    all_clients: boolean;
    quota: T;
    clients?: T[];
}

export enum QuotaAssignationsType {
    all = 'ALL_QUOTAS',
    specific = 'SPECIFIC'
}
