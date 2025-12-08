import { TicketAllocationType } from '@admin-clients/shared/common/data-access';
import { SeasonTicketRenewalSeat } from './models/season-ticket-renewal.model';

export function getLocationInfo(location: SeasonTicketRenewalSeat): string {
    if (location?.seat_type === TicketAllocationType.numbered) {
        return `${location.sector} | ${location.row} | ${location.seat}`;
    } else if (location?.seat_type === TicketAllocationType.notNumbered) {
        return `${location.sector} | ${location.not_numbered_zone}`;
    } else {
        return undefined;
    }
}
