export interface VenueTplDynamicTagGroup {
    name: string;
    code: string;
    id?: number;
}

export type PostVenueTplDynamicTagGroup = Omit<VenueTplDynamicTagGroup, 'id'>;

// GET/POST
export interface VenueTplDynamicTagGroupLabel {
    id: number;
    name: string;
    code: string;
    color: string;
    default?: boolean;
}

export type PutVenueTplDynamicTagGroupLabel = Omit<VenueTplDynamicTagGroupLabel, 'id' | 'default'>;

export type PostVenueTplDynamicTagGroupLabel = Omit<VenueTplDynamicTagGroupLabel, 'id'>;
