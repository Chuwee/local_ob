import { StateProperty } from '@OneboxTM/utils-state';
import { ExportResponse } from '@admin-clients/shared/data-access/models';
import { GetFilterOptionsResponse } from '@admin-clients/shi-panel/utility-models';
import { Injectable } from '@angular/core';
import { GetMappingsResponse } from '../models/get-mappings-response.model';

@Injectable()
export class MappingsState {
    readonly list = new StateProperty<GetMappingsResponse>();
    readonly export = new StateProperty<ExportResponse>();
    readonly countries = new StateProperty<GetFilterOptionsResponse>();
    readonly taxonomies = new StateProperty<GetFilterOptionsResponse>();
}
