import { getListData, getMetadata, mapMetadata, Metadata, StateManager } from '@OneboxTM/utils-state';
import { IdName, VenueAccessControlSystem } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';
import { VenuesApi } from './api/venues.api';
import { GetVenuesRequest } from './models/get-venues-request.model';
import { PostVenueRequest } from './models/post-venue-request.model';
import { PostVenueSpaceRequest } from './models/post-venue-space-request.model';
import { PutVenueRequest } from './models/put-venue-request.model';
import { PutVenueSpaceRequest } from './models/put-venue-space-request.model';
import { VenueCity } from './models/venue-city.model';
import { VenueCountry } from './models/venue-country.model';
import { VenueDetails } from './models/venue-details.model';
import { VenueSpaceDetails } from './models/venue-space-details.model';
import { VenueSpace } from './models/venue-space.model';
import { VenuesState } from './state/venues.state';

@Injectable()
export class VenuesService {
    readonly #venuesApi = inject(VenuesApi);
    readonly #venuesState = inject(VenuesState);

    readonly venuesList = Object.freeze({
        load: (request: GetVenuesRequest) =>
            StateManager.load(this.#venuesState.venuesList, this.#venuesApi.getVenues(request).pipe(mapMetadata())),
        loadMore: (request: GetVenuesRequest) =>
            StateManager.loadMore(request, this.#venuesState.venuesList, r => this.#venuesApi.getVenues(r).pipe(mapMetadata())),
        clear: () => this.#venuesState.venuesList.setValue(null),
        getData$: () => this.#venuesState.venuesList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#venuesState.venuesList.getValue$().pipe(getMetadata()),
        setEmpty: () => this.#venuesState.venuesList.setValue({
            data: [], metadata: new Metadata({ offset: 0, total: 0 })
        }),
        isLoading$: () => this.#venuesState.venuesList.isInProgress$()
    });

    getVenueNames$(ids: number[]): Observable<IdName[]> {
        return this.#venuesState.venuesCache.getItems$(ids, id => this.#venuesApi.getVenue(id));
    }

    loadVenue(venueId: number, entityId?: number): void {
        this.#venuesState.venue.setError(null);
        this.#venuesState.venue.setInProgress(true);
        this.#venuesApi.getVenue(venueId, entityId)
            .pipe(
                catchError(error => {
                    this.#venuesState.venue.setError(error);
                    return of(null);
                }),
                finalize(() => this.#venuesState.venue.setInProgress(false))
            )
            .subscribe(venue => this.#venuesState.venue.setValue(venue));
    }

    getVenue$(): Observable<VenueDetails> {
        return this.#venuesState.venue.getValue$();
    }

    getVenueError$(): Observable<HttpErrorResponse> {
        return this.#venuesState.venue.getError$();
    }

    clearVenue(): void {
        this.#venuesState.venue.setValue(null);
    }

    isVenueLoading$(): Observable<boolean> {
        return this.#venuesState.venue.isInProgress$();
    }

    saveVenue(venueId: number, request: PutVenueRequest): Observable<void> {
        this.#venuesState.venueSaving.setInProgress(true);
        return this.#venuesApi.putVenue(venueId, request)
            .pipe(finalize(() => this.#venuesState.venueSaving.setInProgress(false)));
    }

    createVenue(venue: PostVenueRequest): Observable<number> {
        this.#venuesState.venueSaving.setInProgress(true);
        return this.#venuesApi.postVenue(venue)
            .pipe(
                map(result => result.id),
                finalize(() => this.#venuesState.venueSaving.setInProgress(false))
            );
    }

    isVenueSaving$(): Observable<boolean> {
        return this.#venuesState.venueSaving.isInProgress$();
    }

    deleteVenue(venueId: number): Observable<void> {
        return this.#venuesApi.deleteVenue(venueId);
    }

    loadVenueCountriesList(req: GetVenuesRequest): void {
        this.#venuesState.venueCountriesList.setInProgress(true);
        this.#venuesApi.getVenueCountries(req)
            .pipe(
                finalize(() => this.#venuesState.venueCountriesList.setInProgress(false))
            )
            .subscribe(venueCountries =>
                this.#venuesState.venueCountriesList.setValue(venueCountries)
            );
    }

    getVenueCountriesListData$(): Observable<VenueCountry[]> {
        return this.#venuesState.venueCountriesList.getValue$().pipe(map(venueCountries => venueCountries?.data));
    }

    isVenueCountriesListLoading$(): Observable<boolean> {
        return this.#venuesState.venueCountriesList.isInProgress$();
    }

    loadVenueCitiesList(req: GetVenuesRequest): void {
        this.#venuesState.venueCitiesList.setInProgress(true);
        this.#venuesApi.getVenueCities(req)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this.#venuesState.venueCitiesList.setInProgress(false))
            )
            .subscribe(venueCities =>
                this.#venuesState.venueCitiesList.setValue(venueCities)
            );
    }

    getVenueCitiesListData$(): Observable<VenueCity[]> {
        return this.#venuesState.venueCitiesList.getValue$().pipe(map(venueCities => venueCities?.data));
    }

    isVenueCitiesListLoading$(): Observable<boolean> {
        return this.#venuesState.venueCitiesList.isInProgress$();
    }

    loadVenueAccessControlSystem(venueId: number): void {
        this.#venuesState.venueAccessControlSystem.setError(null);
        this.#venuesState.venueAccessControlSystem.setInProgress(true);
        this.#venuesApi.getVenueAccessControlSystem(venueId)
            .pipe(
                catchError(error => {
                    this.#venuesState.venueAccessControlSystem.setError(error);
                    return of(null);
                }),
                finalize(() => this.#venuesState.venueAccessControlSystem.setInProgress(false))
            )
            .subscribe(accessControlSystem => this.#venuesState.venueAccessControlSystem.setValue(accessControlSystem));
    }

    getVenueAccessControlSystemError$(): Observable<HttpErrorResponse> {
        return this.#venuesState.venueAccessControlSystem.getError$();
    }

    getVenueAccessControlSystem$(): Observable<VenueAccessControlSystem> {
        return this.#venuesState.venueAccessControlSystem.getValue$();
    }

    clearVenueAccessControlSystem(): void {
        this.#venuesState.venueAccessControlSystem.setValue(null);
    }

    isVenueAccessControlSystemLoading$(): Observable<boolean> {
        return this.#venuesState.venueAccessControlSystem.isInProgress$();
    }

    // Venue spaces list
    loadVenueSpacesList(venueId: number): void {
        this.#venuesState.venueSpacesList.setError(null);
        this.#venuesState.venueSpacesList.setInProgress(true);
        this.#venuesApi.getVenueSpaces(venueId)
            .pipe(
                catchError(error => {
                    this.#venuesState.venueSpacesList.setError(error);
                    return of(null);
                }),
                finalize(() => this.#venuesState.venueSpacesList.setInProgress(false))
            )
            .subscribe(venueSpaces => this.#venuesState.venueSpacesList.setValue(venueSpaces));
    }

    getVenueSpacesListData$(): Observable<VenueSpace[]> {
        return this.#venuesState.venueSpacesList.getValue$().pipe(map(venueSpaces => venueSpaces?.data));
    }

    getVenueSpacesListMetadata$(): Observable<Metadata> {
        return this.#venuesState.venueSpacesList.getValue$().pipe(map(venueSpaces => venueSpaces?.metadata));
    }

    isVenueSpacesListLoading$(): Observable<boolean> {
        return this.#venuesState.venueSpacesList.isInProgress$();
    }

    clearVenueSpacesList(): void {
        this.#venuesState.venueSpacesList.setValue(null);
    }

    // Venue space
    loadVenueSpace(venueId: number, spaceId: number): void {
        this.#venuesState.venueSpace.setError(null);
        this.#venuesState.venueSpace.setInProgress(true);
        this.#venuesApi.getVenueSpace(venueId, spaceId)
            .pipe(
                catchError(error => {
                    this.#venuesState.venueSpace.setError(error);
                    return of(null);
                }),
                finalize(() => this.#venuesState.venueSpace.setInProgress(false))
            )
            .subscribe(venueSpace => this.#venuesState.venueSpace.setValue(venueSpace));
    }

    getVenueSpace$(): Observable<VenueSpaceDetails> {
        return this.#venuesState.venueSpace.getValue$();
    }

    clearVenueSpace(): void {
        this.#venuesState.venueSpace.setValue(null);
    }

    isVenueSpaceLoading$(): Observable<boolean> {
        return this.#venuesState.venueSpace.isInProgress$();
    }

    saveVenueSpace(venueId: number, spaceId: number, request: PutVenueSpaceRequest): Observable<void> {
        this.#venuesState.venueSpaceSaving.setInProgress(true);
        return this.#venuesApi.putVenueSpace(venueId, spaceId, request)
            .pipe(finalize(() => this.#venuesState.venueSpaceSaving.setInProgress(false)));
    }

    createVenueSpace(venueId: number, space: PostVenueSpaceRequest): Observable<number> {
        this.#venuesState.venueSpaceSaving.setInProgress(true);
        return this.#venuesApi.postVenueSpace(venueId, space)
            .pipe(
                map(result => result.id),
                finalize(() => this.#venuesState.venueSpaceSaving.setInProgress(false))
            );
    }

    isVenueSpaceSaving$(): Observable<boolean> {
        return this.#venuesState.venueSpaceSaving.isInProgress$();
    }

    deleteVenueSpace(venueId: number, spaceId: number): Observable<void> {
        return this.#venuesApi.deleteVenueSpace(venueId, spaceId);
    }
}
