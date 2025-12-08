import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { GetAttendantFields } from '../model/attendant-field.model';

@Injectable({ providedIn: 'root' })
export class AttendantsState {

    readonly attendantFields = new StateProperty<GetAttendantFields>();

}
