import { IdName } from '@admin-clients/shared/data-access/models';
import { Observable } from 'rxjs';
import { EventDynamicField, EventFieldContainerType } from './event-restrictions-dynamic-configuration.model';

export type MapValue = { source: string; target: string };
export const emptyMap: MapValue = { source: null, target: null };

export interface EventConfigurationFields<T> {
    id: string;
    value: T;
    container: EventFieldContainerType;
    label: string;
    source$?: Observable<IdName[]>;
    target$?: Observable<IdName[]>;
    type: 'number' | 'text' | 'select' | 'multiselect' | 'map' | 'list' | 'number-list';
}

export const eventRestrictionDynamicType = ({ container, source, type }: EventDynamicField): EventConfigurationFields<unknown>['type'] => {
    if (container === 'SINGLE') {
        if (source) {
            return 'select';
        } else if (type === 'INTEGER') {
            return 'number';
        } else if (type === 'STRING') {
            return 'text';
        } else {
            return null;
        }
    } else if (container === 'LIST') {
        return source ? 'multiselect' : (type === 'STRING' ? 'list' : 'number-list');
    } else if (container === 'MAP') {
        return 'map';
    } else {
        return null;
    }
};
