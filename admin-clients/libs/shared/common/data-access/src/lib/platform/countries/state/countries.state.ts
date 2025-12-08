import { StateProperty } from '@OneboxTM/utils-state';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { Country, CountryWithTaxCalculation } from '../model/country.model';

@Injectable({ providedIn: 'root' })
export class CountriesState {
    private _countries = new BaseStateProp<Country[]>();
    readonly setCountries = this._countries.setValueFunction();
    readonly getCountries$ = this._countries.getValueFunction();
    readonly isCountriesLoading$ = this._countries.getInProgressFunction();
    readonly setCountriesLoading$ = this._countries.setInProgressFunction();

    readonly allCountries = new StateProperty<CountryWithTaxCalculation[]>();
}
