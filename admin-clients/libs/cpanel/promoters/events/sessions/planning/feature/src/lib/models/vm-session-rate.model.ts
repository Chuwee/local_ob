import { SessionRate } from '@admin-clients/cpanel/promoters/events/sessions/data-access';

export interface VmSessionRate extends Omit<SessionRate, 'default'> {
    isDefault: boolean;
    isVisible: boolean;
}
