
export interface PutChannelEventRequest {
    event_id: number;
    catalog?: {
        visible: boolean;
        position: number | null;
    };
}
