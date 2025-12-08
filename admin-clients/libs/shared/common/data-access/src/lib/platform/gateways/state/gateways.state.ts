import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { Gateway } from '../model/gateway.model';

@Injectable({ providedIn: 'root' })
export class GatewaysState {
    readonly gatewaysList = new StateProperty<Gateway[]>();
    readonly gateway = new StateProperty<Gateway>();
}
