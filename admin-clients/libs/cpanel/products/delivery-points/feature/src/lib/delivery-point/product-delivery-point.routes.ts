import { authCanActivateGuard } from '@admin-clients/cpanel/core/data-access';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { DeliveryPointDetailsComponent } from './details/delivery-point-details.component';

export const PRODUCTS_DELIVERY_POINT_ROUTES: Routes = [
    {
        path: '',
        component: DeliveryPointDetailsComponent,
        canActivate: [authCanActivateGuard],
        children: [
            {
                path: '',
                loadComponent: () =>
                    import('./general-data/delivery-point-general-data.component').then(m => m.DeliveryPointGeneralDataComponent),
                canActivate: [authCanActivateGuard],
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];
