import { UserRoles } from '@admin-clients/cpanel/core/data-access';
import { roleGuard } from '@admin-clients/cpanel/core/utils';
import { LoginConfigComponent } from '@admin-clients/cpanel/shared/feature/login-config';
import { unsavedChangesGuard } from '@admin-clients/shared/core/features';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EntityCustomerContentAdminComponent } from './customer-content/customer-content-admin/entity-customer-content-admin.component';
import { EntityCustomerContentUserComponent } from './customer-content/customer-content-user/entity-customer-content-user.component';
import { EntityCustomerContentComponent } from './customer-content/entity-customer-content.component';
import { EntityCustomerTypesComponent } from './customer-types/entity-register-users-customer-types.component';
import { EntityEmailContentChangePasswordComponent } from './email-content/change-password/entity-email-content-change-password.component';
import { EntityCommunicationElementImagesComponent } from './email-content/images/entity-communication-element-images.component';
import { EntityEmailContentNewAccountComponent } from './email-content/new-account/entity-email-content-new-account.component';
import { EntityContentsComponent } from './entity-contents/entity-contents.component';
import { EntityFriendsFamilyComponent } from './friends-family/entity-register-users-friends-family.component';
import { EntityLoyaltyProgramComponent } from './loyalty-program/entity-register-users-loyalty-program.component';
import { EntityPayoutDataComponent } from './payout-data/entity-payout-data.component';
import { RegisterUsersComponent } from './register-users.component';

const routes: Routes = [{
    path: '',
    component: RegisterUsersComponent,
    children: [
        {
            path: '',
            pathMatch: 'full',
            redirectTo: 'customer-data'
        },
        {
            path: 'customer-data',
            component: EntityCustomerContentComponent,
            canActivate: [roleGuard],
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.CUSTOMER_DATA',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            },
            children: [
                {
                    path: '',
                    redirectTo: 'admin-panel',
                    pathMatch: 'full'
                },
                {
                    path: 'admin-panel',
                    component: EntityCustomerContentAdminComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'ENTITY.GENERAL_DATA.CUSTOMER_DATA',
                        roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
                    }
                },
                {
                    path: 'user-area',
                    component: EntityCustomerContentUserComponent,
                    canDeactivate: [unsavedChangesGuard()],
                    data: {
                        breadcrumb: 'ENTITY.GENERAL_DATA.CUSTOMER_DATA',
                        roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
                    }
                }
            ]
        },
        {
            path: 'payout-data',
            component: EntityPayoutDataComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.PAYOUT_DATA',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        },
        {
            path: 'login-config',
            component: LoginConfigComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.CUSTOMER_LOGIN',
                roles: [UserRoles.OPR_MGR]
            }
        },
        {
            path: 'loyalty-program',
            component: EntityLoyaltyProgramComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.LOYALTY_PROGRAM',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        },
        {
            path: 'customer-types',
            component: EntityCustomerTypesComponent,
            canDeactivate: [unsavedChangesGuard()],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.CUSTOMER_TYPES'
            }
        },
        {
            path: 'entity-contents',
            component: EntityContentsComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.ENTITY_CONTENTS',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        },
        {
            path: 'friends-family',
            component: EntityFriendsFamilyComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            pathMatch: 'full',
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.FRIENDS_AND_FAMILY',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        },
        {
            path: 'email-contents/images',
            component: EntityCommunicationElementImagesComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.EMAILS_COMMUNICATION.IMAGES.TITLE',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        },
        {
            path: 'email-contents/new-account',
            component: EntityEmailContentNewAccountComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.EMAILS_COMMUNICATION.NEW_ACCOUNT.TITLE',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        },
        {
            path: 'email-contents/change-password',
            component: EntityEmailContentChangePasswordComponent,
            canDeactivate: [unsavedChangesGuard()],
            canActivate: [roleGuard],
            data: {
                breadcrumb: 'ENTITY.GENERAL_DATA.EMAILS_COMMUNICATION.CHANGE_PASSWORD.TITLE',
                roles: [UserRoles.ENT_MGR, UserRoles.OPR_MGR]
            }
        }
    ]
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class RegisterUsersRoutingModule { }
