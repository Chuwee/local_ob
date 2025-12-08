import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { first, map } from 'rxjs/operators';

export const notificationsGuard: CanActivateFn = () => {
    const router = inject(Router);
    const auth = inject(AuthenticationService);

    return auth.getLoggedUser$()
        .pipe(
            first(user => !!user),
            map(user => user.entity.settings?.notifications.email.enabled),
            map(isPermitted => isPermitted ? true : router.parseUrl('/'))
        );

};
