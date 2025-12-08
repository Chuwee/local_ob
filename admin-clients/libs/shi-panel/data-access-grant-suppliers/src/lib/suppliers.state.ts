import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { GetSuppliersResponse } from './models/get-suppliers-response.model';

@Injectable()
export class SuppliersState {
    readonly suppliers = new StateProperty<GetSuppliersResponse>();
}
