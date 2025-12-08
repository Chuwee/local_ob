import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { PutATMChannelConfiguration } from '../models/put-channel-configuration.model';
import { PutATMMemberPriceZones } from '../models/put-member-price-zones.model';

@Injectable({
    providedIn: 'root'
})
export class ExternalAtmApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly BASE_URL = `${this.BASE_API}/atm-api/v1`;

    private readonly _http = inject(HttpClient);

    /**
     * This is a wizard that runs like a Job
     * It sets the fake promotions for the member ad-hoc development for Club Atlético de Madrid
     *
     * What it does? For the selected event, it sets up every promotion selected and sended in the body as fixed 0 value
     * and also add the ids of the selected promotions to the channel config (couchbase) map
     *
     * it also adds the automatic seat selection flag to channel config
     *
     * @param eventId Event ID
     */
    channelConfiguration(eventId: number, body: PutATMChannelConfiguration): Observable<void> {
        return this._http.put<void>(this.BASE_URL + `/wizard/events/${eventId}/channels-configuration`, body);
    }

    /**
     * Wizard endpoint to setup atm member pricezones for ad hoc integration
     *
     * @param eventId Event ID
     * @param venueTemplateId Venue template ID
     */
    memberPriceZones(eventId: number, venueTemplateId: number, body: PutATMMemberPriceZones): Observable<void> {
        return this._http.put<void>(
            this.BASE_URL + `/wizard/events/${eventId}/venue-templates/${venueTemplateId}/price-type-mappings`,
            body
        );
    }
}
