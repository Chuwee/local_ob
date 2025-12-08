import { B2bCondition, B2bConditionsGroupType, B2bGroupType } from './b2b-condition.model';

export type GetB2bConditionsRequest<T extends B2bConditionsGroupType> = EventOrEntityId<T>;

export type EventOrEntityId<T extends B2bConditionsGroupType> =
    T extends 'EVENT' ? { event_id: number } :
    /*T extends 'ENTITY'*/ { entity_id: number };

export interface B2bConditions {
    condition_group_type: B2bGroupType;
    conditions: B2bCondition[];
}

export interface PutB2bConditions {
    id: number; // operatorId | entityId | eventId
    conditions: B2bCondition[];
}

export type DeleteB2bConditionsRequest<T extends B2bConditionsGroupType> = EventOrEntityId<T>;
