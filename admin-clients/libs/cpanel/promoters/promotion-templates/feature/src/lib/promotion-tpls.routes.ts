import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { Routes } from '@angular/router';
import { PromotionTemplatesListComponent } from './list/promotion-tpls-list.component';

export const PROMOTION_TEMPLATES_ROUTES: Routes = [
    {
        path: '',
        component: PromotionTemplatesListComponent,
        canActivate: [authCanActivateGuard]
    },
    {
        path: ':promotionTemplateId',
        loadChildren: () => import('./promotion-template/promotion-tpl.routes')
            .then(r => r.PROMOTION_TEMPLATE_ROUTES),
        data: {
            breadcrumb: 'TITLES.PROMOTION_TEMPLATE_DETAILS'
        }
    }
];
