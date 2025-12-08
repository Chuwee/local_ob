import { GroupDataAttribute } from './group-data-attribute.model';

export interface GroupData {
    id: number;
    name: string;
    partners: number;
    attendees: number;
    attributes: GroupDataAttribute[];
}
