export interface SearchablePaginatedListLoadEvent {
    q?: string;
    limit?: number;
    offset?: number;
}

export const pageSize = 10;
export const pageChangeDebounceTime = 200;
export const searchChangeDebounceTime = 100;
