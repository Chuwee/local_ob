export interface MemberDatesFilter {
    enabled: boolean;
    access: AccessUser[],
    default_access?: Date;
}

export interface AccessUser {
    user: string;
    date: Date;
}

export interface PeriodsDates {
    user: string;
    date: string;
    time: string;
}
