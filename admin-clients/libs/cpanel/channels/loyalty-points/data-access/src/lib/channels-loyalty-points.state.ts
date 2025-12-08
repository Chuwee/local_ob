import { Injectable } from '@angular/core';
import { StateProperty } from '@OneboxTM/utils-state';
import { ChannelLoyaltyPoints } from './models/channel-loyalty-points.model';

@Injectable({ providedIn: 'root' })
export class ChannelsLoyaltyPointsState {
    readonly channelLoyaltyPoints = new StateProperty<ChannelLoyaltyPoints>();
}
