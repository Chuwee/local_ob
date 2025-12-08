import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { EventPromotionConditionsComponent } from './conditions/event-promotion-conditions.component';
import { EventPromotionsContainerComponent } from './container/event-promotions-container.component';
import { eventPromotionDetailsResolver } from './details/event-promotion-details-resolver';
import { EventPromotionDetailsComponent } from './details/event-promotion-details.component';
import { EventPromotionDiscountTypeComponent } from './discount-type/event-promotion-discount-type.component';
import { EventPromotionGeneralDataComponent } from './general-data/event-promotion-general-data.component';
import { EventPromotionZonesComponent } from './zones/event-promotion-zones.component';

export const EVENT_PROMOTION_ROUTES: Routes = [
    {
        path: '',
        component: EventPromotionsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':promotionId',
                component: EventPromotionDetailsComponent,
                resolve: {
                    promotion: eventPromotionDetailsResolver
                },
                data: {
                    breadcrumb: 'PROMOTION_EDITOR'
                },
                children: [
                    {
                        path: '',
                        pathMatch: 'full',
                        redirectTo: 'general-data'
                    },
                    {
                        path: 'general-data',
                        component: EventPromotionGeneralDataComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'EVENTS.PROMOTIONS.GENERAL_DATA'
                        }
                    },
                    {
                        path: 'conditions',
                        component: EventPromotionConditionsComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'EVENTS.PROMOTIONS.LIMITS_AND_CONDITIONS'
                        }
                    },
                    {
                        path: 'discount-type',
                        pathMatch: 'full',
                        component: EventPromotionDiscountTypeComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'EVENTS.PROMOTIONS.DISCOUNT_TYPE'
                        }
                    },
                    {
                        path: 'zones',
                        component: EventPromotionZonesComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'EVENTS.PROMOTIONS.ZONES'
                        }
                    }
                ]
            }
        ]
    }
];
