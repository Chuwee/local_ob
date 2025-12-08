import { SalesRequestListElementModel } from './sales-requests-list-element.model';
import { SalesRequestsStatus } from './sales-requests-status.model';

export class SalesListElementModel {
    id: number;
    date: string;
    eventName: string;
    channelName: string;
    eventEntityName: string;
    venue: {
        text: string;
        city: string;
    };

    status: SalesRequestsStatus;

    constructor({ id, status, date, channel, event }: SalesRequestListElementModel) {
        this.id = id;
        this.date = date;
        this.eventName = event.name;
        this.eventEntityName = event.entity.name;
        this.channelName = channel.name;
        this.venue = {
            text: SalesListElementModel.getVenueText(event.venues),
            city: SalesListElementModel.getCityText(event.venues)
        };
        this.status = status;
    }

    private static getVenueText(venueTpls: any[]): string {
        if (!venueTpls?.length) {
            return '';
        }

        return venueTpls.length === 1 ?
            venueTpls[0].name :
            Array.from(new Set(venueTpls.map(venueTpl => venueTpl.name)))
                .join(', ');
    }

    private static getCityText(venueTpls: any[]): string {
        if (!venueTpls?.length) {
            return '';
        }

        return venueTpls.length === 1 ?
            venueTpls[0].location.city :
            Array.from(new Set(venueTpls.map(venueTpl => venueTpl.location.city)))
                .join(', ');
    }
}
