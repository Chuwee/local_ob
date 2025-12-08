/* eslint-disable @typescript-eslint/dot-notation */
import { TicketTemplatesService, TicketTemplate } from '@admin-clients/cpanel-promoters-ticket-templates-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const ticketTemplateDetailsResolver: ResolveFn<TicketTemplate> = (route: ActivatedRouteSnapshot) => {
    const ticketsTemplateSrv = inject(TicketTemplatesService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const id = route.paramMap.get('ticketTemplateId');
    ticketsTemplateSrv.loadTicketTemplate(id);

    return combineLatest([
        ticketsTemplateSrv.getTicketTemplate$(),
        ticketsTemplateSrv.getTicketTemplateError$()
    ])
        .pipe(
            first(([ticketTemplate, error]) => ticketTemplate !== null || error !== null),
            mergeMap(([ticketTemplate, error]) => {
                if (error) {
                    router.navigate(['/ticket-template']);
                    return EMPTY;
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], ticketTemplate.name);
                }
                return of(ticketTemplate);
            })
        );

};
