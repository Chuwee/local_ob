import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { ShiConfiguration } from '../models/sales-configuration.model';

@Injectable()
export class SalesSettingsState {
    readonly salesConfiguration = new StateProperty<ShiConfiguration>();
}
