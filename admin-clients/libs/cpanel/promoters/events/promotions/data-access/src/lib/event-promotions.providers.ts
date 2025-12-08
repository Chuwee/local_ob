import { Provider } from '@angular/core';
import { EventPromotionsApi } from './api/event-promotions.api';
import { EventPromotionsService } from './event-promotions.service';
import { EventPromotionsState } from './state/event-promotions.state';

export const eventPromotionsProviders: Provider[] = [
    EventPromotionsApi,
    EventPromotionsState,
    EventPromotionsService
];
