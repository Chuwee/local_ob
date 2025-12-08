import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { Region } from '../model/region.model';

@Injectable({ providedIn: 'root' })
export class RegionsState {
    private _regions = new BaseStateProp<Region[]>();
    readonly setRegions = this._regions.setValueFunction();
    readonly getRegions$ = this._regions.getValueFunction();
    readonly isRegionsLoading$ = this._regions.getInProgressFunction();
    readonly setRegionsLoading$ = this._regions.setInProgressFunction();
}
