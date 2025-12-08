import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { first, map } from 'rxjs/operators';

export const b2bGuard: CanActivateFn = () => {
    const router = inject(Router);
    const auth = inject(AuthenticationService);

    return auth.getLoggedUser$()
        .pipe(
            first(user => !!user),
            map(user => user.entity?.settings?.enable_B2B || AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR])),
            map(isPermitted => isPermitted ? true : router.parseUrl('/'))
        );
};
