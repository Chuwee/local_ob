import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { ChannelLiteralsTextContent } from '../models/post-literals.model';

@Injectable({
    providedIn: 'root'
})
export class LiteralsState {
    readonly literals = new StateProperty<ChannelLiteralsTextContent[]>();
}
