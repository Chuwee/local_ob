import { EntityCalendarDayType } from './entity-calendar-day-type.model';

export interface EntityCalendar {
    id: number;
    entity_id: number;
    name: string;
    day_types: EntityCalendarDayType[];
}
