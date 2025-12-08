import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY } from 'rxjs';
import { filter, finalize, first, map, startWith, switchMap, take, tap } from 'rxjs/operators';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';

export const eventTemplateEditorResolver: ResolveFn<boolean> = (route: ActivatedRouteSnapshot) => {
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const eventSrv = inject(EventsService);
    const venueTemplatesSrv = inject(VenueTemplatesService);
    const venueTemplateId = Number(route.paramMap.get('venueTemplateId'));
    if (venueTemplateId) {
        venueTemplatesSrv.venueTpl.clear();
        venueTemplatesSrv.venueTpl.load(venueTemplateId);
        return venueTemplatesSrv.venueTpl.inProgress$().pipe(
            filter(loading => !loading),
            switchMap(() => combineLatest([venueTemplatesSrv.venueTpl.get$(), venueTemplatesSrv.venueTpl.error$()])),
            first(([tpl, error]) => tpl?.id === venueTemplateId || !!error),
            tap(([tpl, error]) => breadcrumbsSrv.addDynamicSegment(!error && route.data['breadcrumb'], tpl?.name)),
            map(([_, error]) => !error),
            finalize(() => venueTemplatesSrv.venueTpl.cancelLoad())
        );
    } else {
        combineLatest([
            eventSrv.event.get$().pipe(startWith(null)),
            venueTemplatesSrv.venueTpl.get$().pipe(startWith(null))
        ])
            .pipe(take(1))
            .subscribe(([event, template]) =>
                router.navigate([
                    '/events',
                    event?.id,
                    event && 'venue-templates',
                    event && template?.id
                ].filter(Boolean))
            );
        return EMPTY;
    }

};
