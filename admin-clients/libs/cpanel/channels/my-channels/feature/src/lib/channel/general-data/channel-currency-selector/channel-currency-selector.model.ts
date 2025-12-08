import { Currency } from '@admin-clients/shared-utility-models';

export interface CurrencySelector {
    selected: Currency[];
    default_currency?: string;
    available?: Currency[];
}
