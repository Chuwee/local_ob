import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'any' })
export class DocumentTypesState {
    readonly docTypes = new StateProperty<string[]>();
}
