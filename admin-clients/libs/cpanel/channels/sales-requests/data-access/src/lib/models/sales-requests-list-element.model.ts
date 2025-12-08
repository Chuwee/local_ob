import { SalesRequestsStatus } from './sales-requests-status.model';

export interface SalesRequestListElementModel {
    id: number;
    status: SalesRequestsStatus;
    date: string;
    channel: {
        id: 11;
        name: string;
        entity: {
            id: number;
            name: string;
        };
    };
    event: {
        id: number;
        name: string;
        start_date: string;
        entity: {
            id: number;
            name: string;
        };
        venues: {
            id: number;
            name: string;
            location: {
                city: string;
            };
        }[];
    };
}
