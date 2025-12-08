import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { take, map, finalize } from 'rxjs/operators';
import { RegionsApi } from './api/regions.api';
import { Region } from './model/region.model';
import { RegionsState } from './state/regions.state';

@Injectable({
    providedIn: 'root'
})
export class RegionsService {

    constructor(
        private _regionsApi: RegionsApi,
        private _regionState: RegionsState,
        private _translateService: TranslateService
    ) { }

    loadRegions(): void {
        this._regionState.getRegions$().pipe(take(1))
            .subscribe(availableRegions => {
                if (!availableRegions) {
                    this._regionState.setRegionsLoading$(true);
                    this._regionsApi.getRegions(this._translateService.getFallbackLang())
                        .pipe(
                            map(regions => Object.keys(regions).map(key => ({
                                code: key,
                                name: regions[key]
                            }))),
                            finalize(() => this._regionState.setRegionsLoading$(false))
                        )
                        .subscribe(regions => this._regionState.setRegions(regions));
                }
            });
    }

    getRegions$(): Observable<Region[]> {
        return this._regionState.getRegions$();
    }

    isRegionsLoading$(): Observable<boolean> {
        return this._regionState.isRegionsLoading$();
    }

    loadSystemRegions(code: string): void {
        this._regionState.setRegionsLoading$(true);
        this._regionsApi.getSystemRegions(code)
            .pipe(
                finalize(() => this._regionState.setRegionsLoading$(false))
            )
            .subscribe(regions => this._regionState.setRegions(regions));
    }

    clearRegions(): void {
        this._regionState.setRegions(null);
    }
}
