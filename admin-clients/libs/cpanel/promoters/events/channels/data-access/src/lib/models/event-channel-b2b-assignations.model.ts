import { Id, IdName } from '@admin-clients/shared/data-access/models';

export interface EventChannelB2bAssignations<T = IdName> {
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

export type PutEventChannelB2bAssignations = EventChannelB2bAssignations<Id>;
