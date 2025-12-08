export interface VenueTemplateQuota {
    id: number;
    name: string;
    code?: string;
    color?: string;
    default?: boolean;
}

export type PostVenueTemplateQuota = Omit<VenueTemplateQuota, 'id'>;
