import { TicketsService } from '@admin-clients/cpanel-sales-data-access';
import { TicketDetail } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const ticketDetailsResolver: ResolveFn<TicketDetail> = (route: ActivatedRouteSnapshot) => {
    const ticketsSrv = inject(TicketsService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const translate = inject(TranslateService);

    const orderCodeAndTicketId = route.paramMap.get('orderCodeAndTicketId');
    let orderCode: string;
    let ticketId: string;

    if (orderCodeAndTicketId) {
        const values = orderCodeAndTicketId.split('-');
        orderCode = values[0];
        ticketId = values[1];
    } else {
        orderCode = route.paramMap.get('orderCode');
        ticketId = route.paramMap.get('ticketId');
    }

    ticketsSrv.ticketDetail.load(orderCode, ticketId);
    ticketsSrv.ticketDetail.stateHistory.load(orderCode, ticketId);

    return combineLatest([
        ticketsSrv.ticketDetail.get$(),
        ticketsSrv.ticketDetail.error$()
    ]).pipe(
        first(([ticket, error]) => ticket !== null || error !== null),
        mergeMap(([ticketDetail, error]) => {
            if (error) {
                router.navigate(['/tickets']);
                return EMPTY;
            }
            // If we have a nested component (transactions->tickets), we need to fill up the parent breadcrumb.
            // If we get here from url, otherwise will already be filled
            if (route?.data?.['previousBreadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['previousBreadcrumb'],
                    translate.instant('TITLES.ORDER', { orderCode }));
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'],
                    translate.instant('TITLES.TICKET', { ticketId: ticketDetail.id }));
            }
            return of(ticketDetail);
        })
    );

};
