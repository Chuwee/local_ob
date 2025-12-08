import { B2bSeat, B2bService } from '@admin-clients/cpanel/b2b/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const b2bPublishingDetailsResolver: ResolveFn<B2bSeat> = (route: ActivatedRouteSnapshot) => {
    const b2bSrv = inject(B2bService);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const translate = inject(TranslateService);

    const seatId = route.paramMap.get('seatId');
    b2bSrv.b2bSeat.load(Number(seatId));

    return combineLatest([
        b2bSrv.b2bSeat.loading$(),
        b2bSrv.b2bSeat.get$()
    ]).pipe(
        first(([loading, seat]) => !loading && seat !== null),
        mergeMap(([_, seatDetail]) => {
            if (route?.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'],
                    translate.instant('TITLES.ORDER', { orderCode: seatDetail.id }));
            }

            return of(seatDetail);
        })
    );
};
