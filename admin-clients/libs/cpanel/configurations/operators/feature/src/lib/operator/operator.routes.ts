import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { Routes } from '@angular/router';
import { operatorDetailsResolver } from './details/operator-details-resolver';
import { OperatorDetailsComponent } from './details/operator-details.component';
import { OperatorGeneralDataComponent } from './general-data/operator-general-data.component';
import { OperatorTaxesComponent } from './taxes/operator-taxes.component';

export const OPERATOR_ROUTES: Routes = [
    {
        path: '',
        providers: [

        ],
        component: OperatorDetailsComponent,
        resolve: {
            event: operatorDetailsResolver
        },
        children: [
            {
                path: '',
                redirectTo: 'general-data',
                pathMatch: 'full'
            },
            {
                path: 'general-data',
                component: OperatorGeneralDataComponent,
                data: {
                    breadcrumb: 'OPERATOR.GENERAL_DATA'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'taxes',
                component: OperatorTaxesComponent,
                data: {
                    breadcrumb: 'OPERATOR.TAXES'
                }
            }
        ]
    }
];
