import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { ProductConfigLiteralsComponent } from './configuration/literals/product-literals.component';
import { ProductConfigurationComponent } from './configuration/product-configuration.component';
import { ProductConfigVariantsComponent } from './configuration/variants/product-config-variants.component';
import { ProductPrincipalInfoComponent } from './principal-info/product-principal-info.component';
import { ProductGeneralDataComponent } from './product-general-data.component';

export const PRODUCT_GENERAL_DATA_ROUTES: Routes = [
    {
        path: '',
        component: ProductGeneralDataComponent,
        data: {
            breadcrumb: 'PRODUCT.GENERAL_DATA.TITLE'
        },
        children: [
            {
                path: '',
                redirectTo: 'principal-info',
                pathMatch: 'full'
            },
            {
                path: 'principal-info',
                component: ProductPrincipalInfoComponent,
                data: {
                    breadcrumb: 'PRODUCT.PRINCIPAL_INFO.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'configuration',
                component: ProductConfigurationComponent,
                data: {
                    breadcrumb: 'PRODUCT.CONFIGURATION.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()],
                children: [
                    {
                        path: '',
                        redirectTo: 'variants',
                        pathMatch: 'full'
                    },
                    {
                        path: 'variants',
                        component: ProductConfigVariantsComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'PRODUCT.VARIANTS.VARIANTS'
                        }
                    },
                    {
                        path: 'literals',
                        component: ProductConfigLiteralsComponent,
                        canDeactivate: [unsavedChangesGuard()],
                        data: {
                            breadcrumb: 'PRODUCT.VARIANTS.LITERALS.TITLE'
                        }
                    }
                ]
            }
        ]
    }
];
