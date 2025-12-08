import { SessionTiersAvailability } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export interface VmSessionTiersAvailability extends SessionTiersAvailability {
    node: VmSessionTiersAvailability[];
    id: string;
    level: number;
}
