import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { MatcherConfiguration } from '../models/matcher-configuration.model';

@Injectable()
export class MatcherSettingsState {
    readonly matcherConfiguration = new StateProperty<MatcherConfiguration>();
}
