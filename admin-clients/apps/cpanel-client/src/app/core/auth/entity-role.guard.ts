import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';

export const entityRoleGuard: CanActivateFn = () => {
    const router = inject(Router);
    const auth = inject(AuthenticationService);

    return auth.getLoggedUser$()
        .pipe(
            map(user => {
                let redirectUrl = 'login';
                if (user) {
                    // DO NOT REARRANGE
                    if (user.entity.settings?.types.includes('INSURANCER')) {
                        redirectUrl = 'login';
                    }
                    if (AuthenticationService.isSomeRoleInUserRoles(user,
                        [UserRoles.CRM_MGR])) {
                        redirectUrl = 'customers';
                    }
                    if (AuthenticationService.isSomeRoleInUserRoles(user,
                        [UserRoles.CRM_DLIST])) {
                        redirectUrl = 'subscription-lists';
                    }
                    if (AuthenticationService.isSomeRoleInUserRoles(user,
                        [UserRoles.ENT_MGR])) {
                        redirectUrl = 'my-entity';
                    }
                    if (AuthenticationService.isSomeRoleInUserRoles(user,
                        [UserRoles.COL_MGR])) {
                        redirectUrl = 'collectives';
                    }
                    if (AuthenticationService.isSomeRoleInUserRoles(user,
                        [UserRoles.REC_MGR])) {
                        redirectUrl = 'venues';
                    }
                    if (AuthenticationService.isSomeRoleInUserRoles(user,
                        [UserRoles.SYS_MGR])) {
                        redirectUrl = 'operators';
                    }
                    if (AuthenticationService.isSomeRoleInUserRoles(user,
                        [UserRoles.ENT_ANS, UserRoles.OPR_ANS, UserRoles.CNL_SAC])) {
                        if (user.entity.settings?.types.includes('OPERATOR') &&
                            user.entity.settings?.types.includes('CHANNEL_ENTITY')) {
                            redirectUrl = 'transactions';
                        } else {
                            redirectUrl = 'tickets';
                        }
                    }
                    if (user.entity.settings?.types.includes('ENTITY') &&
                        user.entity.settings?.types.includes('VENUE_ENTITY') &&
                        AuthenticationService.isSomeRoleInUserRoles(user,
                            [UserRoles.OPR_MGR, UserRoles.EVN_MGR])) {
                        redirectUrl = 'promoter-venue-templates';
                    }
                    if (user.entity.settings?.types.includes('ENTITY') &&
                        user.entity.settings?.types.includes('CHANNEL_ENTITY') &&
                        AuthenticationService.isSomeRoleInUserRoles(user,
                            [UserRoles.CNL_MGR, UserRoles.OPR_MGR])) {
                        redirectUrl = 'sales-requests';
                    }
                    if (user.entity.settings?.types.includes('ENTITY') &&
                        user.entity.settings?.types.includes('EVENT_ENTITY') &&
                        AuthenticationService.isSomeRoleInUserRoles(user,
                            [UserRoles.OPR_MGR, UserRoles.EVN_MGR])) {
                        redirectUrl = 'events';
                    }
                    if (user.entity.settings?.types.includes('MULTI_PRODUCER') &&
                        AuthenticationService.isSomeRoleInUserRoles(user,
                            [UserRoles.OPR_MGR, UserRoles.EVN_MGR])) {
                        redirectUrl = 'events';
                    }
                    if (user.entity.settings?.types.includes('OPERATOR')) {
                        redirectUrl = 'events';
                    }
                    //TODO: uncomment when is needed (BI)
                    // if (handset && AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.BI_USR])) {
                    //     redirectUrl = `bi-reports?redirect=${redirectUrl}`;
                    // }
                    if (AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.PRD_ANS])) {
                        redirectUrl = 'my-user/register-data';
                    }
                }
                return router.parseUrl(redirectUrl);
            })
        );
};
