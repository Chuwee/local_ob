import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { SeasonTicketPromotionConditionsComponent } from './conditions/season-ticket-promotion-conditions.component';
import { SeasonTicketPromotionsContainerComponent } from './container/season-ticket-promotions-container.component';
import { SeasonTicketPromotionDetailsComponent } from './details/season-ticket-promotion-details.component';
import { seasonTicketPromotionDetailsResolver } from './details/season-ticket-promotion-details.resolver';
import {
    SeasonTicketPromotionDiscountTypeComponent
} from './discount-type/season-ticket-promotion-discount-type.component';
import {
    SeasonTicketPromotionGeneralDataComponent
} from './general-data/season-ticket-promotion-general-data.component';
import { SeasonTicketPromotionZonesComponent } from './zones/season-ticket-promotion-zones.component';

export const SEASON_TICKET_PROMOTIONS_ROUTES: Routes = [
    {
        path: '',
        component: SeasonTicketPromotionsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':promotionId',
                component: SeasonTicketPromotionDetailsComponent,
                resolve: {
                    promotion: seasonTicketPromotionDetailsResolver
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
                        component: SeasonTicketPromotionGeneralDataComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'SEASON_TICKET.PROMOTIONS.GENERAL_DATA'
                        }
                    },
                    {
                        path: 'conditions',
                        component: SeasonTicketPromotionConditionsComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'SEASON_TICKET.PROMOTIONS.LIMITS_AND_CONDITIONS'
                        }
                    },
                    {
                        path: 'discount-type',
                        pathMatch: 'full',
                        component: SeasonTicketPromotionDiscountTypeComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'SEASON_TICKET.PROMOTIONS.DISCOUNT_TYPE'
                        }
                    },
                    {
                        path: 'zones',
                        component: SeasonTicketPromotionZonesComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'SEASON_TICKET.PROMOTIONS.ZONES'
                        }
                    }
                ]
            }
        ]
    }
];
