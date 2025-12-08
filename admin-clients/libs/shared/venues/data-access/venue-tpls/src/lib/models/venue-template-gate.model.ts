
export interface VenueTemplateGate {
    id: number;
    name: string;
    code?: string;
    color?: string;
    default?: boolean;
}

export type PostVenueTemplateGate = Omit<VenueTemplateGate, 'id'>;
