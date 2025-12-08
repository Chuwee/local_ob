import { StateProperty } from '@OneboxTM/utils-state';
import { ExportResponse } from '@admin-clients/shared/data-access/models';
import { GetFilterOptionsResponse } from '@admin-clients/shi-panel/utility-models';
import { Injectable } from '@angular/core';
import { GetSalesResponse } from '../models/get-sales-response.model';
import { Transition } from '../models/sale-transition.model';
import { Sale } from '../models/sales.model';

@Injectable()
export class SalesState {
    readonly list = new StateProperty<GetSalesResponse>();
    readonly listExport = new StateProperty<ExportResponse>();
    readonly details = new StateProperty<Sale>();
    readonly transitions = new StateProperty<Transition[]>();
    readonly relaunchSale = new StateProperty<void>();
    readonly relaunchFulfill = new StateProperty<void>();
    readonly deliveryMethods = new StateProperty<GetFilterOptionsResponse>();
    readonly countries = new StateProperty<GetFilterOptionsResponse>();
    readonly currencies = new StateProperty<GetFilterOptionsResponse>();
    readonly taxonomies = new StateProperty<GetFilterOptionsResponse>();
    readonly lastErrors = new StateProperty<GetFilterOptionsResponse>();
}
