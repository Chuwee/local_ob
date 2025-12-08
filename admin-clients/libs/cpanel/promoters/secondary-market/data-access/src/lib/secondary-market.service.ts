import { StateManager } from '@OneboxTM/utils-state';
import { Injectable } from '@angular/core';
import { SecondaryMarketConfig } from './models/secondary-market.model';
import { SecondaryMarketApi } from './secondary-market.api';
import { SecondaryMarketState } from './secondary-market.state';

@Injectable({
    providedIn: 'root'
})
export class SecondaryMarketService {

    #state = new SecondaryMarketState();
    #api = new SecondaryMarketApi();

    readonly eventConfiguration = Object.freeze({
        load: (eventId: number) =>
            StateManager.load(this.#state.eventConfiguration, this.#api.getEventSecondaryMarket(eventId)),
        get$: () => this.#state.eventConfiguration.getValue$(),
        loading$: () => this.#state.eventConfiguration.isInProgress$(),
        clear: () => this.#state.eventConfiguration.setValue(null),
        save: (eventId: number, config: SecondaryMarketConfig) =>
            StateManager.inProgress(this.#state.eventConfiguration, this.#api.postEventSecondaryMarket(eventId, config))
    });

    readonly sessionConfiguration = Object.freeze({
        load: (sessionId: number) =>
            StateManager.load(this.#state.sessionConfiguration, this.#api.getSessionSecondaryMarket(sessionId)),
        get$: () => this.#state.sessionConfiguration.getValue$(),
        loading$: () => this.#state.sessionConfiguration.isInProgress$(),
        clear: () => this.#state.sessionConfiguration.setValue(null),
        delete: (sessionId: number) =>
            StateManager.inProgress(this.#state.eventConfiguration, this.#api.deleteSessionSecondaryMarket(sessionId)),
        save: (sessionId: number, config: SecondaryMarketConfig) =>
            StateManager.inProgress(this.#state.sessionConfiguration, this.#api.postSessionSecondaryMarket(sessionId, config))
    });

    readonly seasonTicketConfiguration = Object.freeze({
        load: (seasonTicketId: number) =>
            StateManager.load(this.#state.seasonTicketConfiguration, this.#api.getSeasonTicketSecondaryMarket(seasonTicketId)),
        get$: () => this.#state.seasonTicketConfiguration.getValue$(),
        loading$: () => this.#state.seasonTicketConfiguration.isInProgress$(),
        clear: () => this.#state.seasonTicketConfiguration.setValue(null),
        save: (seasonTicketId: number, config: SecondaryMarketConfig) =>
            StateManager.inProgress(this.#state.seasonTicketConfiguration,
                this.#api.postSeasonTicketSecondaryMarket(seasonTicketId, config))
    });

}
