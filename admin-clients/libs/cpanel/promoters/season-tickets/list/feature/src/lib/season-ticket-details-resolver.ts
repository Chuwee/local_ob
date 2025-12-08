import { SeasonTicket, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY } from 'rxjs';
import { first, map, mergeMap } from 'rxjs/operators';

export const seasonTicketDetailsResolver: ResolveFn<SeasonTicket> = (route: ActivatedRouteSnapshot) => {
    const seasonTicketsSrv = inject(SeasonTicketsService);
    const entitiesSrv = inject(EntitiesBaseService);
    const router = inject(Router);
    const breadcrumbSrv = inject(BreadcrumbsService);

    const id = route.paramMap.get('seasonTicketId');
    seasonTicketsSrv.seasonTicket.clear();
    seasonTicketsSrv.seasonTicket.load(id);
    seasonTicketsSrv.seasonTicketStatus.clear();
    seasonTicketsSrv.seasonTicketStatus.load(id);

    return combineLatest([
        seasonTicketsSrv.seasonTicket.get$(),
        seasonTicketsSrv.seasonTicket.error$(),
        seasonTicketsSrv.seasonTicketStatus.get$(),
        seasonTicketsSrv.seasonTicketStatus.error$()
    ]).pipe(
        first(([seasonTicket, errorSeasonTicket, seasonTicketStatus, errorSeasonTicketStatus]) =>
            (seasonTicket !== null && seasonTicketStatus !== null) ||
            errorSeasonTicket !== null ||
            errorSeasonTicketStatus !== null
        ),
        mergeMap(([seasonTicket, errorSeasonTicket, _, errorSeasonTicketStatus]) => {
            if (errorSeasonTicket || errorSeasonTicketStatus) {
                router.navigate(['/season-tickets']);
                return EMPTY;
            }

            entitiesSrv.loadEntity(seasonTicket.entity.id);

            if (route.data?.['breadcrumb']) {
                breadcrumbSrv.addDynamicSegment(route.data['breadcrumb'], seasonTicket.name);
            }

            return entitiesSrv.getEntity$().pipe(
                first(entity => !!entity),
                map(() => seasonTicket)
            );
        })
    );
};
