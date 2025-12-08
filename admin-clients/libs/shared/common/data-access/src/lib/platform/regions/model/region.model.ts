export interface Region {
    code: string;
    name: string;
}

export interface RegionWithId extends Region {
    id: number;
}
