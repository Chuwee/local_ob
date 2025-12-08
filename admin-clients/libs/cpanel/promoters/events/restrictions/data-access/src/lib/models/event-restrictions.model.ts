export interface EventRestriction {
    sid: string;
    name: string;
    type: EventRestrictionType;
    activated: boolean;
    venue_template_sectors?: number[];
    translations?: Record<string, string>;
    fields?: Record<string, number | number[]>;
    new?: boolean;
    loaded?: boolean;
    editing?: boolean;
}

export type EventRestrictionListElem = Partial<EventRestriction>;

export type EventRestrictionList = EventRestrictionListElem[];

export type EventRestrictionField = {
    id: string;
    type: EventRestrictionFieldType;
    container: EventRestrictionContainerType;
    source: EventRestrictionSourceType;
    value: string;
};

export type EventRestrictionStructure = {
    fields: EventRestrictionField[];
    restriction_type: EventRestrictionType;
};

export enum EventRestrictionFieldType {
    string = 'STRING',
    integer = 'INTEGER',
    boolean = 'BOOLEAN'
}

export enum EventRestrictionContainerType {
    list = 'LIST',
    single = 'SINGLE',
    map = 'MAP'
}

export enum EventRestrictionSourceType {
    roleId = 'ROLE_ID',
    quotaId = 'QUOTA_ID',
    sectorId = 'SECTOR_ID',
    capacityId = 'CAPACITY_ID',
    termId = 'TERM_ID',
    oneOfAll = 'ONE_ALL_QUALIFIER',
    timeUnit = 'TIME_UNIT',
    timeLapse = 'TIME_LAPSE'
}

export enum EventRestrictionType {
    role = 'ROLE',
    payment = 'PAYMENT',
    sectorPass = 'SECTOR_PASS',
    observation = 'OBSERVATION',
    sessionStartTime = 'SESSION_START_TIME'
}
