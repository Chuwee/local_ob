import { TourStatus } from './tour-status.enum';

export interface PutTour {
    id: number;
    status: TourStatus;
    name: string;
}
