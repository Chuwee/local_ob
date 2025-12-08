import {
    TourListElement, TourListFilters, ToursLoadCase, ToursState, ToursService
} from '@admin-clients/cpanel/promoters/tours/data-access';
import { Injectable, OnDestroy } from '@angular/core';
import { GuardsCheckEnd, Router } from '@angular/router';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, first, take, takeUntil, tap } from 'rxjs/operators';

export type ToursStateParams = {
    state: ToursLoadCase;
    idPath?: string;
};

@Injectable()
export class ToursStateMachine implements OnDestroy {
    private _onDestroy = new Subject<void>();
    private _idPath: number;
    private _tourId: number;
    private _filters: TourListFilters;

    constructor(
        private _toursState: ToursState,
        private _toursSrv: ToursService,
        private _router: Router
    ) {
        combineLatest([
            this._toursSrv.getTourListFilters$(),
            this.getListDetailState$()
        ])
            .pipe(
                filter(([_, state]) => state !== null),
                tap(([filters, state]) => {
                    this._filters = filters || {};
                    switch (state) {
                        case ToursLoadCase.loadTour:
                            this.loadTour();
                            break;
                        case ToursLoadCase.selectedTour:
                            this.selectedTour();
                            break;
                        case ToursLoadCase.loadList:
                            this.loadToursList();
                            break;
                        case ToursLoadCase.none:
                        default:
                            break;
                    }
                }),
                takeUntil(this._onDestroy)
            ).subscribe();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    setCurrentState({ state, idPath }: ToursStateParams): void {
        this._idPath = Number(idPath);
        this._toursState.setListDetailState(state);
    }

    getListDetailState$(): Observable<ToursLoadCase> {
        return this._toursState.getListDetailState$();
    }

    private loadTour(): void {
        this.loadToursList();
        this.getToursList()
            .pipe(
                tap(toursList => {
                    this.setTourIdOfToursList(toursList);
                    if (toursList.length) {
                        this.loadTourDetail();
                        this.navigateToTour();
                    } else {
                        this.navigateToList();
                    }
                })
            ).subscribe();
    }

    private selectedTour(): void {
        this.getToursList()
            .pipe(
                tap(toursList => {
                    if (toursList.length) {
                        this.setTourId(this._idPath);
                        this.navigateToTour();
                    }
                })
            ).subscribe();

        this._router.events.pipe(
            first((event): event is GuardsCheckEnd => event instanceof GuardsCheckEnd),
            tap((event: GuardsCheckEnd) => {
                if (event.shouldActivate) {
                    this.loadTourDetail();
                }
            })
        ).subscribe();
    }

    private setTourId(tourId: number): void {
        this._tourId = tourId;
    }

    private setTourIdOfToursList(toursList: TourListElement[]): void {
        if (this._idPath && !!toursList.length && toursList.some(tourFromList => tourFromList.id === this._idPath)) {
            this.setTourId(this._idPath);
        } else if (toursList.length) {
            this.setTourId(toursList[0].id);
        } else {
            this.setTourId(undefined);
        }
    }

    private navigateToTour(): void {
        this._router.navigate(['/tours', this._tourId]);
    }

    private navigateToList(): void {
        this._router.navigate(['/tours']);
    }

    private loadTourDetail(): void {
        this._toursSrv.clearTour();
        this._toursSrv.loadTour(this._tourId);
    }

    private loadToursList(): void {
        this._toursSrv.clearToursList();
        this._toursSrv.loadToursList(this._filters);
    }

    private getToursList(): Observable<TourListElement[]> {
        return this._toursSrv.getToursListData$()
            .pipe(
                first(toursList => !!toursList),
                take(1)
            );
    }
}

