export interface EventDynamicField {
    id: string;
    type: EventFieldType;
    container: EventFieldContainerType;
    target?: string;
    source?: string;
}

export type EventFieldContainerType = 'LIST' | 'SINGLE' | 'MAP';
export type EventFieldType = 'INTEGER' | 'STRING' | 'BOOLEAN';
