import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { PackSaleRequestListElem } from '../models/pack-sale-request.model';

@Injectable()
export class PackSaleRequestsState {
    readonly packsSaleRequestsList = new StateProperty<ListResponse<PackSaleRequestListElem>>();
    readonly packsSaleRequestStatus = new StateProperty<void>();
}
