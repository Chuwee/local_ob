import { StateManager } from '@OneboxTM/utils-state';
import { Injectable, inject } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import { take, map, finalize, first } from 'rxjs/operators';
import { CountriesApi } from './api/countries.api';
import { Country, CountryWithTaxCalculation } from './model/country.model';
import { CountriesState } from './state/countries.state';

@Injectable({
    providedIn: 'root'
})
export class CountriesService {
    readonly #countriesApi = inject(CountriesApi);
    readonly #countriesState = inject(CountriesState);
    readonly #translateService = inject(TranslateService);

    // TODO: Separar el base service del service de CPanel, igual que State y Api, tal y como se hace con el entities-base service

    /* Solo funciona en CPanel */
    readonly allCountries = Object.freeze({
        load: () =>
            StateManager.load(
                this.#countriesState.allCountries,
                this.#loadAllCountries()
            ),
        get$: () => this.#countriesState.allCountries.getValue$(),
        clear: () => this.#countriesState.allCountries.setValue(null),
        loading$: () => this.#countriesState.allCountries.isInProgress$()
    });

    /* Solo funciona en CPanel */
    #loadAllCountries(params: { sort?: boolean } = null): Observable<CountryWithTaxCalculation[]> {
        return combineLatest([
            this.#countriesApi.getCountries(this.#translateService.getFallbackLang()),
            this.#countriesApi.getAllCountries().pipe(first(Boolean))
        ]).pipe(
            map(([translatedCountries, countries]) => {
                const countriesMapped = Object.keys(translatedCountries).map(key => ({
                    code: key,
                    name: translatedCountries[key] || countries?.find(c => c.code === key)?.name,
                    tax_calculation: countries?.find(c => c.code === key)?.tax_calculation
                }));
                return params?.sort
                    ? countriesMapped.sort((a, b) => (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0))
                    : countriesMapped;
            })
        );
    }

    loadCountries(params: { sort?: boolean } = null): void {
        this.#countriesState.getCountries$().pipe(take(1))
            .subscribe(availablecountries => {
                if (!availablecountries) {
                    this.#countriesState.setCountriesLoading$(true);
                    this.#countriesApi.getCountries(this.#translateService.getFallbackLang())
                        .pipe(
                            map(countries => {
                                const countriesMapped = Object.keys(countries).map(key => ({
                                    code: key,
                                    name: countries[key]
                                }));
                                return params?.sort
                                    ? countriesMapped.sort((a, b) => (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0))
                                    : countriesMapped;
                            }),
                            finalize(() => this.#countriesState.setCountriesLoading$(false))
                        )
                        .subscribe(countries => this.#countriesState.setCountries(countries));
                }
            });
    }

    // TODO: ¿Se está usando?
    loadSystemCountries(params: { code?: string; sort?: boolean } = null): void {
        this.#countriesState.setCountriesLoading$(true);
        this.#countriesApi.getSystemCountries(params?.code)
            .pipe(
                map(countries => params?.sort
                    ? countries.sort((a, b) => (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0))
                    : countries),
                finalize(() => this.#countriesState.setCountriesLoading$(false))
            )
            .subscribe(countries => this.#countriesState.setCountries(countries));
    }

    getCountries$(): Observable<Country[]> {
        return this.#countriesState.getCountries$();
    }

    isCountriesLoading$(): Observable<boolean> {
        return this.#countriesState.isCountriesLoading$();
    }

    clearCountries(): void {
        this.#countriesState.setCountries(null);
    }

}
