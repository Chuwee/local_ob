import { StateProperty } from '@OneboxTM/utils-state';
import { GetMatchingCountriesResponse, GetMatchingsResponse } from '@admin-clients/shi-panel/utility-models';
import { Injectable } from '@angular/core';

@Injectable()
export class BlacklistedMatchingsState {
    readonly blacklist = new StateProperty<GetMatchingsResponse>();
    readonly countries = new StateProperty<GetMatchingCountriesResponse>();
}
