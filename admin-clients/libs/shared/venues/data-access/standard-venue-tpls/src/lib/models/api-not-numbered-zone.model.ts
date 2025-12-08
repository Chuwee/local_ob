import { Accessibility } from './seat-accessibility.enum';
import { Visibility } from './seat-visibility.enum';
import { BlockingReasonCounter, QuotaCounter, StatusCounter } from './vm-item.model';

export interface ApiNotNumberedZone {
    id: number;
    sector_id: number;
    name: string;
    capacity: number;
    view_id: number;
    status_counters: StatusCounter[];
    blocking_reason_counters: BlockingReasonCounter[];
    price_type: number;
    quota_counters: QuotaCounter[];
    visibility: Visibility;
    accessibility: Accessibility;
    gate: number;
    dynamic_tag1: number;
    dynamic_tag2: number;
}
