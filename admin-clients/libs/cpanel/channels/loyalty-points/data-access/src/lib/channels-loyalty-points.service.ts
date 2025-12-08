import { inject, Injectable } from '@angular/core';
import { StateManager } from '@OneboxTM/utils-state';
import { ChannelsLoyaltyPointsApi } from './channels-loyalty-points.api';
import { ChannelsLoyaltyPointsState } from './channels-loyalty-points.state';
import { ChannelLoyaltyPoints } from './models/channel-loyalty-points.model';

@Injectable({ providedIn: 'root' })
export class ChannelsLoyaltyPointsService {
    readonly #api = inject(ChannelsLoyaltyPointsApi);
    readonly #state = inject(ChannelsLoyaltyPointsState);

    readonly loyaltyPoints = Object.freeze({
        load: (channelId: number) => StateManager.load(
            this.#state.channelLoyaltyPoints, this.#api.getChannelLoyaltyPoints(channelId)
        ),
        update: (channelId: number, config: ChannelLoyaltyPoints) => StateManager.inProgress(
          this.#state.channelLoyaltyPoints, this.#api.putChannelLoyaltyPoints(channelId, config)
        ),
        get$: () => this.#state.channelLoyaltyPoints.getValue$(),
        loading$: () => this.#state.channelLoyaltyPoints.isInProgress$(),
        clear: () => this.#state.channelLoyaltyPoints.setValue(null)
    });
}
