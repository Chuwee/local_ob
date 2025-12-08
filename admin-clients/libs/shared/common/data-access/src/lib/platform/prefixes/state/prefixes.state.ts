import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { Prefix } from '../model/prefix.model';

@Injectable({ providedIn: 'root' })
export class PrefixesState {
    readonly prefixes = new StateProperty<Prefix[]>();
}
