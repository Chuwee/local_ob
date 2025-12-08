import { EventType } from '@admin-clients/shared/common/data-access';
import { EventStatus } from './event-status.enum';
import { Event } from './event.model';

/* eslint-disable  @typescript-eslint/naming-convention, no-underscore-dangle, id-blacklist, id-match */
export class EventListElement {
    id: number;
    name: string;
    reference: string;
    type: EventType;
    status: EventStatus;
    archived: boolean;
    start_date: string;
    end_date: string;
    currency_code: string;
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
        city: string;
    };

    constructor({
        id, name, reference, type,
        status, archived, start_date, end_date, entity,
        producer, venue_templates, currency_code
    }: Event) {
        this.id = id;
        this.name = name;
        this.reference = reference;
        this.type = type;
        this.status = status;
        this.start_date = start_date;
        this.end_date = end_date;
        this.archived = archived;
        this.currency_code = currency_code;
        this.entity = {
            id: entity?.id,
            name: entity?.name
        };
        this.producer = {
            id: producer?.id,
            name: producer?.name
        };
        this.venue_templates = {
            text: EventListElement.getVenueText(venue_templates),
            city: EventListElement.getCityText(venue_templates)
        };
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

    private static getCityText(venueTpls: any[]): string {
        if (!venueTpls?.length) {
            return '';
        }

        return venueTpls.length === 1 ?
            venueTpls[0].venue.city :
            Array.from(new Set(venueTpls.map(venueTpl => venueTpl.venue.city)))
                .join(', ');
    }
}
/* eslint-enable */
