import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthenticationService } from './authentication.service';
import { UserRoles } from './user-roles.model';

/**
 * Determine if the user has permission to manage multiple entities and redirects in negative case.
 */
export const multipleEntityGuard: CanActivateFn = () => {
    const router = inject(Router);
    const auth = inject(AuthenticationService);

    return combineLatest([
        auth.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.OPR_ANS, UserRoles.ENT_MGR, UserRoles.SYS_MGR]),
        auth.hasLoggedUserSomeEntityType$(['ENTITY_ADMIN', 'OPERATOR', 'SUPER_OPERATOR'])])
        .pipe(
            map(([hasSomeRole, isSomeEntity]) => (hasSomeRole && isSomeEntity)),
            map(isPermitted => isPermitted || router.parseUrl('/'))
        );

};
