import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import {
    BiHistoryReport, BiReport
} from '../models/bi-reports.model';

@Injectable({ providedIn: 'root' })
export class BiSupersetState {
    readonly reportsSupersetList = new StateProperty<BiReport[]>();
    readonly reportsSupersetSearch = new StateProperty<BiReport[]>();
    readonly reportsSupersetHistoryList = new StateProperty<BiHistoryReport[]>();
}