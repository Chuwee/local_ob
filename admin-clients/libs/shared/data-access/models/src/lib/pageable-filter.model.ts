export interface ListFilter {
    sort?: string;
    q?: string;
    aggs?: boolean;
}

export interface PageableFilter extends ListFilter {
    limit?: number;
    offset?: number;
}
