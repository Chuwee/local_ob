// TODO: enum duplicado de session > list, mover este cuando se cree feature

export enum SessionGroupType {
    day = 'DAY',
    week = 'WEEK',
    month = 'MONTH'
}

export const sessionGroupTypeFormats = {
    [SessionGroupType.day]: { token: 'LL', isRange: false },
    [SessionGroupType.week]: { token: 'L', isRange: true },
    [SessionGroupType.month]: { token: 'MMMM YYYY', isRange: false }
};
