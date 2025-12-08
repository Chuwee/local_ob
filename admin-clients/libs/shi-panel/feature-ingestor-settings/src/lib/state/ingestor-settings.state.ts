import { StateProperty } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { IngestorConfiguration } from '../models/ingestor-configuration.model';

@Injectable()
export class IngestorSettingsState {
    readonly ingestorConfiguration = new StateProperty<IngestorConfiguration>();
}
