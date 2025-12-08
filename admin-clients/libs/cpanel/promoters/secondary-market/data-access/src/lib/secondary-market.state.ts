import { StateProperty } from '@OneboxTM/utils-state';
import { SecondaryMarketConfig } from './models/secondary-market.model';

export class SecondaryMarketState {

    readonly eventConfiguration = new StateProperty<SecondaryMarketConfig>();
    readonly seasonTicketConfiguration = new StateProperty<SecondaryMarketConfig>();
    readonly sessionConfiguration = new StateProperty<SecondaryMarketConfig>();

}
