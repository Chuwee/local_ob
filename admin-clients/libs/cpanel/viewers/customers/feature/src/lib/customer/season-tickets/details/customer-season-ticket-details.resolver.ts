/* eslint-disable @typescript-eslint/dot-notation */
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { of } from 'rxjs';
import { tap } from 'rxjs/operators';

export const customerSeasonTicketDetailsResolver: ResolveFn<boolean> = (route: ActivatedRouteSnapshot) => {
    const breadcrumbSrv = inject(BreadcrumbsService);

    const id = decodeURIComponent(route.paramMap.get('orderItemId'));

    return of(true).pipe(
        tap(() => {
            breadcrumbSrv.addDynamicSegment('orderItemId', id);
        })
    );
};
