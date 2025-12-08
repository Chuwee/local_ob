import { B2bConditionsClientsGroupType } from './b2b-condition.model';
import { B2bConditions, EventOrEntityId } from './b2b-conditions.model';

export type GetB2bConditionsClientRequest<T extends B2bConditionsClientsGroupType> = EventOrEntityId<T>;

export interface B2bConditionsClient extends B2bConditions {
    client: {
        id: number;
        name: string;
    };
}
