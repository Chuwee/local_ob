import { FieldType } from './dynamic-configuration.model';

export interface Restriction {
    sid: string;
    activated: boolean;
    member_periods: string[];
    venue_template_id: number;
    venue_template_sectors: number[];
    restriction_name: string;
    restriction_type: RestrictionType;
    translations: Record<string, string>;
    fields: Record<string, number | number[] | string | string[]>;
    loaded?: boolean;
    editing?: boolean;
}

export type RestrictionListElem = Partial<Restriction>;

export type RestrictionList = RestrictionListElem[];

export type RestrictionField = {
    id: string;
    type: FieldType;
    container: ContainerType;
    source?: SourceType;
};

export type RestrictionStructure = {
    fields: RestrictionField[];
    restriction_type: RestrictionType;
};

/* eslint-disable @typescript-eslint/naming-convention */

export enum ContainerType {
    SINGLE = 'SINGLE',
    LIST = 'LIST'
}

export enum SourceType {
    MINIMUM_MAXIMUM = 'MINIMUM_MAXIMUM',
    ROLE_ID = 'ROLE_ID'
}

export enum RestrictionType {
    LIMIT_TYPE = 'LIMIT_TYPE',
    RATIO_TYPE = 'RATIO_TYPE'
}
