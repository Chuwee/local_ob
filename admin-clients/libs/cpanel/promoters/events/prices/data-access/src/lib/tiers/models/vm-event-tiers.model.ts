import { EventTiers } from './event-tiers.model';

export interface VMEventTiers extends EventTiers {
    priority: number;
}
