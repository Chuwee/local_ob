export interface PutTicketTemplate {
    id?: number;
    name?: string;
    default?: boolean;
    design_id?: number;
    languages?: {
        default: string;
        selected: string[];
    };
}
