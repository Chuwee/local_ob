import { StateProperty } from '@OneboxTM/utils-state';
import { ExportResponse } from '@admin-clients/shared/data-access/models';
import { GetMatchingCountriesResponse, GetMatchingsResponse, Matching } from '@admin-clients/shi-panel/utility-models';
import { Injectable } from '@angular/core';
import { GetMatcherStatusResponse } from '../models/get-matcher-status-response.model';
import { GetTaxonomiesResponse } from '../models/get-taxonomies-response.model';

@Injectable()
export class MatchingsState {
    readonly list = new StateProperty<GetMatchingsResponse>();
    readonly blacklist = new StateProperty<GetMatchingsResponse>();
    readonly countries = new StateProperty<GetMatchingCountriesResponse>();
    readonly export = new StateProperty<ExportResponse>();
    readonly details = new StateProperty<Matching>();
    readonly matcherStatus = new StateProperty<GetMatcherStatusResponse>();
    readonly shiTaxonomies = new StateProperty<GetTaxonomiesResponse>();
    readonly supplierTaxonomies = new StateProperty<GetTaxonomiesResponse>();
}
