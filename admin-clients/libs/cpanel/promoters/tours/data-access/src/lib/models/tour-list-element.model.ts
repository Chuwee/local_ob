import { TourStatus } from './tour-status.enum';

export interface TourListElement {
    id: number;
    name: string;
    status: TourStatus;
    entity: {
        id: number;
        name: string;
    };
}
