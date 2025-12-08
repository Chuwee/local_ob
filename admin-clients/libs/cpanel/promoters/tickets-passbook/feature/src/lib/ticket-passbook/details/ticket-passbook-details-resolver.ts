import { TicketPassbook, TicketsPassbookService } from '@admin-clients/cpanel-promoters-tickets-passbook-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const ticketPassbookDetailsResolver: ResolveFn<TicketPassbook> = (route: ActivatedRouteSnapshot) => {
    const ticketsPassbookSrv = inject(TicketsPassbookService);
    const entityService = inject(EntitiesBaseService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const id = route.paramMap.get('ticketPassbookId');
    const entityId = (route.queryParamMap.get('entity_id'));
    ticketsPassbookSrv.loadTicketPassbook(id, entityId);
    entityService.loadEntity(Number(entityId));

    return combineLatest([
        ticketsPassbookSrv.getTicketPassbook$(),
        ticketsPassbookSrv.getTicketPassbookError$()
    ])
        .pipe(
            first(([ticketPassbook, error]) => ticketPassbook !== null || error !== null),
            mergeMap(([ticketPassbook, error]) => {
                if (error) {
                    router.navigate(['/ticket-passbook']);
                    return EMPTY;
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], ticketPassbook.name);
                }
                return of(ticketPassbook);
            })
        );

};
