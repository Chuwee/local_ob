import { PageableFilter } from '@admin-clients/shared/data-access/models';

export interface PackSaleRequest {
    id: number;
    status: PackSaleRequestStatus;
    date: string;
    channel: {
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
        };
    };
    pack: {
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
        }
    };
}

export interface PackSaleRequestListElem {
    id: number;
    status: PackSaleRequestStatus;
    date: string;
    channel: {
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
        }
    };
    pack: {
        id: number;
        name: string;
        entity: {
            id: number;
            name: string;
        };
    };
}

export interface GetPacksSaleRequestsReq extends PageableFilter {
    status?: PackSaleRequestStatus[];
    startDate?: string;
    endDate?: string;
    fields?: string[];
}

export const packSaleRequestStatus = ['PENDING', 'ACCEPTED', 'REJECTED'] as const;
export type PackSaleRequestStatus = typeof packSaleRequestStatus[number];
