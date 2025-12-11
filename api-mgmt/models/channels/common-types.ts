// Common types used across channel models

export interface IdName {
    id?: number;
    name?: string;
}

export interface Currency {
    id?: number;
    code?: string;
    name?: string;
    symbol?: string;
}

export interface PageableFilter {
    page?: number;
    size?: number;
    sort?: string;
}

export interface ListResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

export interface Id {
    id: number;
}

export interface ImageRestrictions {
    width: number;
    height: number;
    size: number;
}

export interface ContentImage<T = string> {
    id?: number;
    type?: T;
    url?: string;
    file?: string;
}

export interface BaseContentImage<T = string> extends ContentImage<T> {
    // Base interface for content images
}

export interface RangeElement {
    min?: number;
    max?: number;
}

export enum Weekdays {
    MONDAY = 'MONDAY',
    TUESDAY = 'TUESDAY',
    WEDNESDAY = 'WEDNESDAY',
    THURSDAY = 'THURSDAY',
    FRIDAY = 'FRIDAY',
    SATURDAY = 'SATURDAY',
    SUNDAY = 'SUNDAY'
}

export interface InteractiveVenues {
    enabled?: boolean;
    venue_id?: number;
}

export enum PromotionType {
    DISCOUNT = 'DISCOUNT',
    UPGRADE = 'UPGRADE'
}

export interface ImportComContentsGroups {
    // Communication content import groups
    [key: string]: any;
}
