
export interface ChannelCapacity {
    name?: string;
    id: number;
    main: boolean;
    venue_template_id?: number;
    virtual_zone_id?: number;
}

export type ChannelCapacities = ChannelCapacity[];
