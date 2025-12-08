import { UserRoles, authCanMatchGuard } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InvoicingDetailsComponent } from './details/invoicing-details.component';
import { EntitiesInvoicingConfigurationComponent } from './entities-configuration/entities-configuration.component';
import { GenerateInvoicingReportComponent } from './generate-report/generate-report.component';

const routes: Routes = [
    {
        path: '',
        component: InvoicingDetailsComponent,
        canActivate: [roleGuard],
        canMatch: [authCanMatchGuard],
        data: {
            roles: [UserRoles.SYS_MGR]
        },
        children: [
            {
                path: '',
                redirectTo: 'generate-report',
                pathMatch: 'full'
            },
            {
                path: 'generate-report',
                component: GenerateInvoicingReportComponent,
                data: {
                    breadcrumb: 'INVOICING.GENERATE_REPORT.TITLE'
                }
            },
            {
                path: 'entities-configuration',
                component: EntitiesInvoicingConfigurationComponent,
                data: {
                    breadcrumb: 'INVOICING.ENTITIES_CONFIGURATION.TITLE'
                },
                canDeactivate: [unsavedChangesGuard()]
            },
            {
                path: 'operators-configuration',
                loadComponent: () => import('./operators-configuration/operators-configuration.component')
                    .then(m => m.OperatorsInvoicingConfigurationComponent),
                data: {
                    breadcrumb: 'INVOICING.OPERATORS_CONFIGURATION.TITLES.OPERATORS_CONFIGURATION'
                },
                canDeactivate: [unsavedChangesGuard()]
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class InvoicingRoutingModule { }
