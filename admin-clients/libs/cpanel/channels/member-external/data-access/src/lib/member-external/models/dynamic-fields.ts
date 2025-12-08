import { IdName } from '@admin-clients/shared/data-access/models';
import { Observable } from 'rxjs';
import { DynamicField, FieldContainerType } from './dynamic-configuration.model';

export type MapValue = { source: string; target: string };
export const emptyMap: MapValue = { source: null, target: null };

export interface ConfigurationFields<T> {
    id: string;
    value: T;
    container: FieldContainerType;
    label: string;
    source$?: Observable<IdName[]>;
    target$?: Observable<IdName[]>;
    type: 'number' | 'text' | 'select' | 'multiselect' | 'map' | 'list' | 'number-list';
}

export const dynamicType = ({ container, source, type }: DynamicField): ConfigurationFields<unknown>['type'] => {
    if (container === 'SINGLE') {
        if (source) {
            return 'select';
        }
        if (type === 'INTEGER') {
            return 'number';
        }
        if (type === 'STRING') {
            return 'text';
        }
    } else if (container === 'LIST') {
        return source ? 'multiselect' : (type === 'STRING' ? 'list' : 'number-list');
    } else if (container === 'MAP') {
        return 'map';
    }
    return 'text';
};

export const dynamicValue = ({ value, container, source }: DynamicField): unknown => {
    if (container === 'MAP') {
        return value && Object.entries<string>(value)
            .map(([source, target]) => ({ target, source }))
            .concat(emptyMap) || [emptyMap];
    }
    if (container === 'LIST' && !source) {
        return value?.concat(null) || [null];
    }
    if (container === 'LIST' && source) {
        return value || [null];
    }
    return value;
};
