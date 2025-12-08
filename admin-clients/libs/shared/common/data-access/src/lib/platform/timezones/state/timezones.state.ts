import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { Timezone } from '../model/timezone.model';

@Injectable({
    providedIn: 'root'
})
export class TimezonesState {
    readonly timezones = new StateProperty<Timezone[]>();
}
