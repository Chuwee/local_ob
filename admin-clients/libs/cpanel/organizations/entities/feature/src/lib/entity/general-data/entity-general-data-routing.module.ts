import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [{
    path: '',
    loadComponent: () => import('./entity-general-data.component').then(c => c.EntityGeneralDataComponent),
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'principal-info'
        },
        {
            path: 'principal-info',
            loadComponent: () => import('./principal-info/entity-principal-info.component').then(c => c.EntityPrincipalInfoComponent),
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.PRINCIPAL_INFO'
            }
        },
        {
            path: 'contact',
            loadComponent: () => import('./contact/entity-contact.component').then(c => c.EntityContactComponent),
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.CONTACT'
            }
        },
        {
            path: 'invoice-data',
            loadComponent: () => import('./invoice-data/entity-invoice-data.component').then(c => c.EntityInvoiceDataComponent),
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.INVOICE_DATA'
            }
        },
        {
            path: 'surcharges',
            loadComponent: () => import('./surcharges/entity-general-data-surcharges.component').then(c => c.EntitySurchargesComponent),
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.SURCHARGES',
                roles: [UserRoles.OPR_MGR, UserRoles.ENT_MGR]
            }
        },
        {
            path: 'managed-entities',
            loadComponent: () => import('./managed-entities/entity-managed-entities.component').then(c => c.EntityManagedEntitiesComponent),
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.MANAGED_ENTITIES',
                roles: [UserRoles.OPR_MGR, UserRoles.ENT_MGR]
            }
        },
        {
            path: 'cookies',
            loadComponent: () => import('./cookies/entity-general-data-cookies.component').then(c => c.EntityCookiesComponent),
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.COOKIES',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        },
        {
            path: 'bank-accounts',
            loadComponent: () => import('./bank-account-list/entity-bank-account-list.component').then(c => c.EntityBankAccountListComponent),
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.BANK_ACCOUNTS',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class EntityGeneralDataRoutingModule { }
