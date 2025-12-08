/* eslint-disable @typescript-eslint/dot-notation */
import { NotificationDetail, NotificationsService } from '@admin-clients/cpanel/notifications/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const notificationDetailsResolver: ResolveFn<NotificationDetail> = (route: ActivatedRouteSnapshot) => {
    const notificationSrv = inject(NotificationsService);
    const router = inject(Router);
    const code = route.paramMap.get('code');
    const breadcrumbsSrv = inject(BreadcrumbsService);

    notificationSrv.loadNotificationDetail(code);

    return combineLatest([
        notificationSrv.getNotificationDetail$(),
        notificationSrv.getNotificationDetailError$()
    ]).pipe(
        first(([notification, error]) => notification !== null || error !== null),
        mergeMap(([notificationDetail, error]) => {
            if (error) {
                router.navigate(['/notifications']);
                return EMPTY;
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], notificationDetail.name);
            }
            return of(notificationDetail);
        })
    );
};
