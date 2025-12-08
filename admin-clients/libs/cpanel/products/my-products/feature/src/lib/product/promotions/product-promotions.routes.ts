
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ProductPromotionsContainerComponent } from './container/product-promotions-container.component';
import { productPromotionDetailsResolver } from './promotion/details/product-promotion-details-resolver';
import { ProductPromotionDetailsComponent } from './promotion/details/product-promotion-details.component';
import { ProductPromotionDiscountTypeComponent } from './promotion/discount-type/product-promotion-discount-type.component';
import { ProductPromotionGeneralDataComponent } from './promotion/general-data/product-promotion-general-data.component';

export const PRODUCT_PROMOTIONS_ROUTES: Routes = [
    {
        path: '',
        component: ProductPromotionsContainerComponent,
        children: [
            {
                path: '',
                component: null,
                pathMatch: 'full',
                children: []
            },
            {
                path: ':promotionId',
                component: ProductPromotionDetailsComponent,
                resolve: {
                    promotion: productPromotionDetailsResolver
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
                        component: ProductPromotionGeneralDataComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'PRODUCT.PROMOTIONS.GENERAL_DATA'
                        }
                    },
                    {
                        path: 'type',
                        component: ProductPromotionDiscountTypeComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        pathMatch: 'full',
                        data: {
                            breadcrumb: 'PRODUCT.PROMOTIONS.DISCOUNT_TYPE'
                        }
                    }
                ]
            }
        ]
    }
];
