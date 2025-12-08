import { mapMetadata, Metadata } from '@OneboxTM/utils-state';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of, ReplaySubject } from 'rxjs';
import { catchError, filter, finalize, map } from 'rxjs/operators';
import { ToursApi } from './api/tours.api';
import { GetToursRequest } from './models/get-tours-request.model';
import { PostTour } from './models/post-tour.model';
import { PutTour } from './models/put-tour.model';
import { TourListFilters } from './models/tour-list-filters.model';
import { Tour } from './models/tour.model';
import { ToursState } from './state/tours.state';

@Injectable({
    providedIn: 'root'
})
export class ToursService {
    private _tourListFilters = new ReplaySubject<TourListFilters>(1);
    private _tourListFilters$ = this._tourListFilters.asObservable();

    constructor(private _toursApi: ToursApi, private _toursState: ToursState) { }

    loadToursList(request: GetToursRequest): void {
        this._toursState.setToursListLoading(true);
        this._toursApi.getTours(request)
            .pipe(
                mapMetadata(),
                catchError(() => of(null)),
                finalize(() => this._toursState.setToursListLoading(false))
            )
            .subscribe(tours =>
                this._toursState.setToursList(tours)
            );
    }

    clearToursList(): void {
        this._toursState.setToursList(null);
    }

    getToursListData$(): Observable<Tour[]> {
        return this._toursState.getToursList$()
            .pipe(
                filter(tours => !!tours),
                map(tours => tours.data)
            );
    }

    getToursListMetadata$(): Observable<Metadata> {
        return this._toursState.getToursList$().pipe(map(r => r?.metadata));
    }

    isToursListLoading$(): Observable<boolean> {
        return this._toursState.isToursListLoading$();
    }

    loadTour(id: number): void {
        this._toursState.setTourError(null);
        this._toursState.setTourLoading(true);
        this._toursApi.getTour(id)
            .pipe(
                catchError(error => {
                    this._toursState.setTourError(error);
                    return of(null);
                }),
                finalize(() => this._toursState.setTourLoading(false))
            )
            .subscribe(tour =>
                this._toursState.setTour(tour)
            );
    }

    clearTour(): void {
        this._toursState.setTour(null);
    }

    getTour$(): Observable<Tour> {
        return this._toursState.getTour$();
    }

    getTourError$(): Observable<HttpErrorResponse> {
        return this._toursState.getTourError$();
    }

    isTourLoading$(): Observable<boolean> {
        return this._toursState.isTourLoading$();
    }

    isTourSaving$(): Observable<boolean> {
        return this._toursState.isTourSaving$();
    }

    deleteTour(id: number): Observable<void> {
        return this._toursApi.deleteTour(id);
    }

    createTour(tour: PostTour): Observable<number> {
        this._toursState.setTourLoading(true);
        this._toursState.setTourError(null);
        return this._toursApi.postTour(tour)
            .pipe(
                catchError(error => {
                    this._toursState.setTourError(error);
                    return of(null);
                }),
                map(result => result.id),
                finalize(() => this._toursState.setTourLoading(false))
            );
    }

    saveTour(tour: PutTour): Observable<void> {
        this._toursState.setTourSaving(true);
        this._toursState.setTourError(null);
        return this._toursApi.putTour(tour)
            .pipe(
                catchError(error => {
                    this._toursState.setTourError(error);
                    return of(null);
                }),
                finalize(() => this._toursState.setTourSaving(false))
            );
    }

    setTourListFilters(value: TourListFilters): void {
        this._tourListFilters.next(value);
    }

    getTourListFilters$(): Observable<TourListFilters> {
        return this._tourListFilters$;
    }
}
