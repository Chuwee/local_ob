export interface OriginEntityVisibility {
    type: VisibilityDestinyEntityType;
    visible_entities?: DestinyEntityVisibility[];
    visible_operators?: DestinyOperatorVisibility[];
}

export interface DestinyEntityVisibility {
    type: VisibilityRelationType;
    id: number;
    name: string;
    operator_id: number;
}

export interface DestinyOperatorVisibility {
    id: number;
    name: string;
}

export enum VisibilityDestinyEntityType {
    private = 'PRIVATE',
    public = 'PUBLIC',
    filtered = 'FILTERED'
}

export enum VisibilityRelationType {
    sharedResources = 'SHARED_RESOURCES',
    sharedResourcesWithChannelPromos = 'SHARED_RESOURCES_WITH_CHANNEL_PROMOTIONS'
}
