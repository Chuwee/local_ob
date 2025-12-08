import { StateProperty } from '@OneboxTM/utils-state';
import { Entity } from '@admin-clients/shared/common/data-access';
import { Injectable } from '@angular/core';
import { DestinationChannel } from '../models/destination-channel.model';

@Injectable({ providedIn: 'root' })
export class FeverState {
    readonly entities = new StateProperty<Entity[]>();
    readonly destinationChannels = new StateProperty<DestinationChannel[]>();
}
