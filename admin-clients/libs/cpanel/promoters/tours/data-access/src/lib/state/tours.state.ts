import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetToursResponse } from '../models/get-tours-response.model';
import { Tour } from '../models/tour.model';
import { ToursLoadCase } from '../models/tours-load.case';

@Injectable({
    providedIn: 'root'
})
export class ToursState {
    private _toursList = new BaseStateProp<GetToursResponse>();
    readonly setToursList = this._toursList.setValueFunction();
    readonly getToursList$ = this._toursList.getValueFunction();
    readonly setToursListLoading = this._toursList.setInProgressFunction();
    readonly isToursListLoading$ = this._toursList.getInProgressFunction();
    readonly setToursListError = this._toursList.setErrorFunction();
    readonly getToursListError$ = this._toursList.getErrorFunction();
    // Tour Detail
    private _tour = new BaseStateProp<Tour>();
    readonly setTour = this._tour.setValueFunction();
    readonly getTour$ = this._tour.getValueFunction();
    readonly setTourLoading = this._tour.setInProgressFunction();
    readonly isTourLoading$ = this._tour.getInProgressFunction();
    readonly setTourSaving = this._tour.setInProgressFunction();
    readonly isTourSaving$ = this._tour.getInProgressFunction();
    readonly setTourError = this._tour.setErrorFunction();
    readonly getTourError$ = this._tour.getErrorFunction();
    // List Detail State
    private _listDetailState = new BaseStateProp<ToursLoadCase>(ToursLoadCase.none);
    readonly getListDetailState$ = this._listDetailState.getValueFunction();
    readonly setListDetailState = this._listDetailState.setValueFunction();
}
