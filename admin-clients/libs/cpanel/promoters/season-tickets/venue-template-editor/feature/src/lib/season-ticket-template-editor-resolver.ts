import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { VenueTemplate, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { combineLatest, EMPTY, filter, map, mergeMap, of, switchMap } from 'rxjs';

export const seasonTicketTemplateEditorResolver: ResolveFn<VenueTemplate> = () => {
    const venueTemplatesSrv = inject(VenueTemplatesService);
    const seasonTicketSrv = inject(SeasonTicketsService);

    return seasonTicketSrv.seasonTicket.get$().pipe(
        switchMap(seasonTicket => {
            venueTemplatesSrv.venueTpl.load(seasonTicket.venue_templates[0].id);
            return combineLatest([
                venueTemplatesSrv.venueTpl.get$(),
                venueTemplatesSrv.venueTpl.error$()
            ]);
        }),
        map(([template, error]) => (template) || error),
        filter(Boolean),
        mergeMap(result => {
            if (result instanceof HttpErrorResponse) {
                return EMPTY;
            } else {
                return of(result);
            }
        })
    );
};
