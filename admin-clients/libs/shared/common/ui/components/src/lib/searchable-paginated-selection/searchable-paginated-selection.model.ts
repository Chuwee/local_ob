import { IdName } from '@admin-clients/shared/data-access/models';
import { Observable } from 'rxjs';

export interface PaginatedSelectionLoadEvent {
    limit?: number;
    offset?: number;
}

export interface SearchablePaginatedSelectionLoadEvent extends PaginatedSelectionLoadEvent {
    q?: string;
}

export interface MultiselectField {
    fieldName?: string;
    label?: string;
    options$?: Observable<IdName[]>;
}

export const pageSize = 10;
export const pageChangeDebounceTime = 200;
export const searchChangeDebounceTime = 100;
