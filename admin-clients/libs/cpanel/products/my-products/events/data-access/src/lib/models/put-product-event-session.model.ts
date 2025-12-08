import { ProductEventSessionSelectionType } from './product-event-session-selection-type.model';

export interface PutProductEventSessions {
    type: ProductEventSessionSelectionType;
    sessions: number[];
}
