import { EventStatus } from '@admin-clients/cpanel/promoters/events/data-access';

export interface TourEvent {
    id: number;
    name: string;
    archived: boolean;
    capacity: number;
    start_date: string;
    status: EventStatus;
}
