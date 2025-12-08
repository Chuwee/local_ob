import { TourStatus } from './tour-status.enum';

export interface GetToursRequest {
    entityId?: number;
    status?: TourStatus[];
}
