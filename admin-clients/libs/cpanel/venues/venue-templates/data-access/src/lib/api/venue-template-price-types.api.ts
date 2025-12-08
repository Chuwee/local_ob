import { TicketContentImage, TicketContentImageRequest, TicketContentText } from '@admin-clients/cpanel/promoters/events/data-access';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { IdName } from '@admin-clients/shared/data-access/models';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
    GetPriceTypeRestricion,
    PostPriceTypeRestriction,
    RestrictedPriceZones
} from '../models/price-type-restriction.model';
import { VenueTemplatePriceTypeChannelContent } from '../models/venue-template-price-type-channel-content.model';

@Injectable({
    providedIn: 'root'
})
export class VenueTemplatePriceTypesApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly VENUE_TEMPLATES_API = `${this.BASE_API}/mgmt-api/v1/venue-templates`;

    private readonly _http = inject(HttpClient);

    getVenueTplPriceTypeChannelContent(venueTemplateId: number, priceTypeId: number): Observable<VenueTemplatePriceTypeChannelContent[]> {
        return this._http.get<VenueTemplatePriceTypeChannelContent[]>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/price-types/${priceTypeId}/channel-contents`);
    }

    postEventPriceTypeChannelContent(
        venueTemplateId: number,
        priceTypeId: string,
        textsToSave: VenueTemplatePriceTypeChannelContent[]): Observable<void> {
        return this._http.post<void>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/price-types/${priceTypeId}/channel-contents`, textsToSave);
    }

    // RESTRICTIONS

    getVenueTplRestrictedPriceTypes(venueTplId: number): Observable<RestrictedPriceZones> {
        return this._http.get<RestrictedPriceZones>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/restricted-price-types`);
    }

    getPriceTypeRestriction(venueTplId: number, priceTypeId: number): Observable<GetPriceTypeRestricion> {
        return this._http.get<GetPriceTypeRestricion>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/price-types/${priceTypeId}/restrictions`);
    }

    postPriceTypeRestriction(venueTplId: number, priceTypeId: number, restriction: PostPriceTypeRestriction): Observable<void> {
        return this._http.post<void>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/price-types/${priceTypeId}/restrictions`, restriction);
    }

    deletePriceTypeRestriction(venueTplId: number, priceTypeId: number): Observable<void> {
        return this._http.delete<void>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/price-types/${priceTypeId}/restrictions`);
    }

    // PRICE TYPE TICKET CONTENTS

    getModifiedTicketContentPriceTypes(venueTplId: number): Observable<IdName[]> {
        return this._http.get<IdName[]>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/ticket-contents/changed-price-types`);
    }

    getPriceTypeTicketContentTexts(
        contentType: string,
        venueTemplateId: number,
        priceTypeId: number
    ): Observable<TicketContentText[]> {
        return this._http.get<TicketContentText[]>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/price-types/${priceTypeId}/ticket-contents/${contentType}/texts`);
    }

    postPriceTypeTicketContentTexts(
        contentType: string,
        venueTemplateId: number,
        priceTypeId: number,
        textsToSave: TicketContentText[]
    ): Observable<void> {
        return this._http.post<void>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/price-types/${priceTypeId}/ticket-contents/${contentType}/texts`, textsToSave);
    }

    // PRICE TYPE TICKET CONTENTS

    getPriceTypeTicketContentImages(
        contentType: string,
        venueTplId: number,
        priceTypeId: number
    ): Observable<TicketContentImage[]> {
        return this._http.get<TicketContentImage[]>(this.contentUrl(venueTplId, priceTypeId, contentType) + `/images`);
    }

    postPriceTypeTicketContentImages(
        contentType: string,
        venueTplId: number,
        priceTypeId: number,
        images: TicketContentImageRequest[]
    ): Observable<void> {
        return this._http.post<void>(this.contentUrl(venueTplId, priceTypeId, contentType) + `/images`, images);
    }

    deletePriceTypeTicketContentImages(
        contentType: string,
        venueTplId: number,
        priceTypeId: number,
        language: string,
        type: string
    ): Observable<void> {
        return this._http.delete<void>(
            `${this.contentUrl(venueTplId, priceTypeId, contentType)}/images/languages/${language}/types/${type}`);
    }

    private contentUrl = (venueTplId: number, priceTypeId: number, contentType: string): string =>
        `${this.VENUE_TEMPLATES_API}/${venueTplId}/price-types/${priceTypeId}/ticket-contents/${contentType}`;

}
