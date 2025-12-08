import { Platform } from '@angular/cdk/platform';
import { inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, CanDeactivateFn, Router } from '@angular/router';
import { first, map } from 'rxjs/operators';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { isHandset } from '@admin-clients/shared/utility/utils';
import { LoginComponent } from 'libs/shared/feature/login/src/lib/login.component';
import {
    DownloadMobileAppDialogComponent,
    DownloadMobileAppDialogInput,
    DownloadMobileAppDialogOutput
} from './dialog/download-app-dialog.component';

let shown = false;

export const downloadAppGuard: CanDeactivateFn<LoginComponent> = () => {
    const router = inject(Router);
    const route = inject(ActivatedRoute);
    const platform = inject(Platform);
    // eslint-disable-next-line @typescript-eslint/dot-notation
    const redirectQueryParam = route.snapshot.queryParams['redirect'];
    const hasRedirectQueryParam$ = inject(AuthenticationService).hasLoggedUserSomeRoles$([UserRoles.BI_USR])
        .pipe(
            first(),
            map(hasLoggedUserSomeRoles => hasLoggedUserSomeRoles && redirectQueryParam)
        );

    if (isHandset() && platform.IOS) {
        if (!shown) {
            const matDialog = inject(MatDialog);
            const dialogInput: DownloadMobileAppDialogInput = {
                hasRedirectQueryParam$,
                redirectText: 'BI.REPORTS.ACTIONS.GO_TO_BI_REPORTS'
            };

            const dialogConfig = new ObMatDialogConfig(dialogInput);
            matDialog.open<
                DownloadMobileAppDialogComponent,
                DownloadMobileAppDialogInput,
                DownloadMobileAppDialogOutput
            >(DownloadMobileAppDialogComponent, dialogConfig)
                .beforeClosed()
                .pipe(first())
                .subscribe(result => {
                    if (!result) return;
                    // eslint-disable-next-line @typescript-eslint/dot-notation
                    router.navigate([redirectQueryParam]);
                });
        }
        shown = true;
    }
    return true;
};
