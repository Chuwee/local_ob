import { ProductEventSessionSelectionType } from './product-event-session-selection-type.model';
import { ProductEventSession } from './product-event-session.model';

export interface GetProductEventSessionsResponse {
    sessions: ProductEventSession[];
    type: ProductEventSessionSelectionType;
}
