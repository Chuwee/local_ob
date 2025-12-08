import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { GetTerminalsResponse } from '../models/get-terminals-response.model';
import { Terminal } from '../models/terminal.model';

@Injectable()
export class TerminalsState {
    readonly terminal = new StateProperty<Terminal>();
    readonly terminals = new StateProperty<GetTerminalsResponse>();
}
