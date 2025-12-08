import { buildHttpParams } from '@OneboxTM/utils-http';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { VenueAccessControlSystem } from '@admin-clients/shared/data-access/models';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { GetVenueCitiesResponse } from '../models/get-venue-cities-response.model';
import { GetVenueCountriesResponse } from '../models/get-venue-countries-response.model';
import { GetVenueSpacesResponse } from '../models/get-venue-spaces-response.model';
import { GetVenuesRequest } from '../models/get-venues-request.model';
import { GetVenuesResponse } from '../models/get-venues-response.model';
import { PostVenueRequest } from '../models/post-venue-request.model';
import { PostVenueSpaceRequest } from '../models/post-venue-space-request.model';
import { PutVenueRequest } from '../models/put-venue-request.model';
import { PutVenueSpaceRequest } from '../models/put-venue-space-request.model';
import { VenueDetails } from '../models/venue-details.model';
import { VenueSpaceDetails } from '../models/venue-space-details.model';

@Injectable()
export class VenuesApi {
    readonly #http = inject(HttpClient);
    readonly #BASE_API = inject(APP_BASE_API);
    readonly #VENUES_API = `${this.#BASE_API}/mgmt-api/v1/venues`;

    getVenues(request: GetVenuesRequest): Observable<GetVenuesResponse> {
        return this.#http.get<GetVenuesResponse>(this.#VENUES_API, { params: this.convertGetVenuesRequest(request, 'name:asc') });
    }

    getVenue(venueId: number, entityId?: number): Observable<VenueDetails> {
        const params = entityId ? new HttpParams().set('entity_id', entityId.toString()) : undefined;
        return this.#http.get<VenueDetails>(`${this.#VENUES_API}/${venueId}`, { params });
    }

    putVenue(venueId: number, request: PutVenueRequest): Observable<void> {
        return this.#http.put<void>(`${this.#VENUES_API}/${venueId}`, request);
    }

    postVenue(venue: PostVenueRequest): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(this.#VENUES_API, venue);
    }

    deleteVenue(venueId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#VENUES_API}/${venueId}`);
    }

    getVenueCountries(request: GetVenuesRequest): Observable<GetVenueCountriesResponse> {
        return this.#http.get<GetVenueCountriesResponse>(
            `${this.#VENUES_API}/all/countries`, { params: this.convertGetVenuesRequest(request, 'code:asc') });
    }

    getVenueCities(request?: GetVenuesRequest): Observable<GetVenueCitiesResponse> {
        return this.#http.get<GetVenueCitiesResponse>(`${this.#VENUES_API}/all/cities`,
            { params: this.convertGetVenuesRequest(request, 'code:asc') });
    }

    getVenueAccessControlSystem(venueId: number): Observable<VenueAccessControlSystem> {
        return this.#http.get<VenueAccessControlSystem>(`${this.#VENUES_API}/${venueId}/access-control-systems`);
    }

    //Venue spaces
    getVenueSpaces(venueId: number): Observable<GetVenueSpacesResponse> {
        return this.#http.get<GetVenueSpacesResponse>(`${this.#VENUES_API}/${venueId}/spaces`);
    }

    getVenueSpace(venueId: number, spaceId: number): Observable<VenueSpaceDetails> {
        return this.#http.get<VenueSpaceDetails>(`${this.#VENUES_API}/${venueId}/spaces/${spaceId}`);
    }

    postVenueSpace(venueId: number, space: PostVenueSpaceRequest): Observable<{ id: number }> {
        return this.#http.post<{ id: number }>(`${this.#VENUES_API}/${venueId}/spaces`, space);
    }

    putVenueSpace(venueId: number, spaceId: number, request: PutVenueSpaceRequest): Observable<void> {
        return this.#http.put<void>(`${this.#VENUES_API}/${venueId}/spaces/${spaceId}`, request);
    }

    deleteVenueSpace(venueId: number, spaceId: number): Observable<void> {
        return this.#http.delete<void>(`${this.#VENUES_API}/${venueId}/spaces/${spaceId}`);
    }

    private convertGetVenuesRequest(req: GetVenuesRequest, defaultSort: string): HttpParams {
        return buildHttpParams({
            sort: req.sort ?? defaultSort,
            limit: req.limit ?? 20,
            offset: req.offset,
            q: req.q,
            fields: req.fields,
            entity_id: req.entityId,
            country_code: req.countryCode,
            city: req.city,
            include_third_party_venues: !!req.includeThirdPartyVenues,  // ?? so strange, always true or false, no undefineds???
            include_own_template_venues: !!req.includeOwnTemplateVenues,  // ?? so strange, always true or false, no undefineds???
            only_in_use_venues: !!req.onlyInUseVenues  // ?? so strange, always true or false, no undefineds???
        });
    }
}
