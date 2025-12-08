import { TourEvent } from './tour-event.model';
import { TourStatus } from './tour-status.enum';

export interface Tour {
    id: number;
    name: string;
    status: TourStatus;
    entity: {
        id: number;
        name: string;
    };
    events: TourEvent[];
}
