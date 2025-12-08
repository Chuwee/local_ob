import { TourStatus } from './tour-status.enum';

export interface TourListFilters {
    entityId?: number;
    status?: TourStatus[];
}
