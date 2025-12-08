/* eslint-disable  @typescript-eslint/naming-convention, no-underscore-dangle, id-blacklist, id-match */

import { SeasonTicketSearch } from './season-ticket-search.model';
import { SeasonTicketStatus } from './season-ticket-status.model';

export class SeasonTicketListElement {
    id: number;
    name: string;
    entity: {
        id: number;
        name: string;
    };

    producer: {
        id: number;
        name: string;
    };

    venue_templates: {
        text: string;
    };

    generation_status: string;

    currency_code: string;

    status: SeasonTicketStatus;

    constructor({ id, name, entity, producer, venue_templates, generation_status, currency_code, status }: SeasonTicketSearch) {
        this.id = id;
        this.name = name;
        this.entity = { ...entity };
        this.producer = { ...producer };
        this.venue_templates = { text: SeasonTicketListElement.getVenueText(venue_templates) };
        this.generation_status = generation_status;
        this.currency_code = currency_code;
        this.status = status;
    }

    private static getVenueText(venueTpls: any[]): string {
        if (!venueTpls?.length) {
            return '';
        }

        return venueTpls.length === 1 ?
            venueTpls[0].venue.name :
            Array.from(new Set(venueTpls.map(venueTpl => venueTpl.venue.name)))
                .join(', ');
    }
}

/* eslint-enable */
