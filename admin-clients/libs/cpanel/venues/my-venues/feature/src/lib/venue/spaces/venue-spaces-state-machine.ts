import { Injectable, OnDestroy } from '@angular/core';
import { ActivatedRoute, GuardsCheckEnd, Router } from '@angular/router';
import { Observable, of, Subject } from 'rxjs';
import { filter, first, mapTo, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { VenueSpacesLoadCase, VenuesService, VenuesState, VenueSpace } from '@admin-clients/cpanel/venues/data-access';

export type VenueSpacesStateParams = {
    state: VenueSpacesLoadCase;
    idPath?: number;
};

@Injectable()
export class VenueSpacesStateMachine implements OnDestroy {
    private _onDestroy = new Subject<void>();
    private _idPath: number;
    private _venueId: number;
    private _spaceId: number;

    constructor(
        private _venuesService: VenuesService,
        private _venuesState: VenuesState,
        private _route: ActivatedRoute,
        private _router: Router
    ) {
        this.getListDetailState$()
            .pipe(
                //with filter(state => !!state) doesn't work
                filter(state => state !== null),
                takeUntil(this._onDestroy)
            ).subscribe(state => {
                switch (state) {
                    case VenueSpacesLoadCase.loadVenueSpace:
                        this.loadVenueSpaceWithRefreshList();
                        break;
                    case VenueSpacesLoadCase.selectVenueSpace:
                        this.selectVenueSpace();
                        break;
                    case VenueSpacesLoadCase.none:
                    default:
                        break;
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    setCurrentState({ state, idPath }: VenueSpacesStateParams): void {
        this._idPath = idPath;
        this._venuesState.listDetailState.setValue(state);
    }

    getListDetailState$(): Observable<VenueSpacesLoadCase> {
        return this._venuesState.listDetailState.getValue$();
    }

    private loadVenueSpaceWithRefreshList(): void {
        this.loadVenueSpacesList();
        this.getVenueSpacesList()
            .subscribe(venueSpacesList => {
                if (venueSpacesList.length) {
                    this.setVenueSpaceIdOfSpaceList(venueSpacesList);
                    this.loadVenueSpace();
                    this.navigateToVenueSpace();
                }
            });
    }

    private selectVenueSpace(): void {
        this.getVenueSpacesList()
            .subscribe(venueSpaces => {
                if (venueSpaces.length) {
                    this.setVenueSpaceId(this._idPath);
                    this.navigateToVenueSpace();
                }
            });

        this._router.events
            .pipe(first(event => event instanceof GuardsCheckEnd))
            .subscribe((event: GuardsCheckEnd) => {
                if (event.shouldActivate) {
                    this.loadVenueSpace();
                }
            });
    }

    private setVenueSpaceId(spaceId: number): void {
        this._spaceId = spaceId;
    }

    private setVenueSpaceIdOfSpaceList(venueSpacesList: VenueSpace[]): void {
        if (this._idPath && !!venueSpacesList.length &&
            venueSpacesList.some(venueSpaceFromList => venueSpaceFromList.id === this._idPath)) {
            this.setVenueSpaceId(this._idPath);
        } else {
            this.setVenueSpaceId(venueSpacesList[0].id);
        }
    }

    private navigateToVenueSpace(): void {
        this._venuesService.getVenue$()
            .pipe(take(1))
            .subscribe(_ => {
                const path = this.currentPath();
                this._router.navigate([path], { relativeTo: this._route });
            });
    }

    private loadVenueSpace(): void {
        this._venuesService.clearVenueSpace();
        this._venuesService.loadVenueSpace(this._venueId, this._spaceId);
    }

    private loadVenueSpacesList(): void {
        this._venuesService.getVenue$()
            .pipe(first(venue => !!venue))
            .subscribe(venue => {
                this._venuesService.clearVenueSpacesList();
                this._venueId = venue.id;
                this._venuesService.loadVenueSpacesList(venue.id);
            });
    }

    private getVenueSpacesList(): Observable<VenueSpace[]> {
        return this._venuesService.getVenueSpacesListData$()
            .pipe(
                first(venueSpacesList => !!venueSpacesList),
                switchMap(venueSpacesList => {
                    if (!venueSpacesList.length) {
                        return this._venuesService.getVenue$()
                            .pipe(
                                tap(venue => this._router.navigate(['/venues', venue.id, 'spaces'])),
                                mapTo(venueSpacesList)
                            );
                    } else {
                        return of(venueSpacesList);
                    }
                }),
                take(1)
            );
    }

    private currentPath(): string {
        return this._innerPath ?
            this._spaceId.toString() + '/' + this._innerPath : this._spaceId.toString();
    }

    // gets the inner path (tab route) if found
    private get _innerPath(): string {
        return this._route.snapshot.children[0]?.children[0]?.routeConfig.path;
    }
}
