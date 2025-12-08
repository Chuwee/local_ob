import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { combineLatest, EMPTY } from 'rxjs';
import { filter, finalize, first, map, switchMap, tap } from 'rxjs/operators';

export const eventVenueTemplateResolver: ResolveFn<boolean> = route => {
    const venueTemplatesSrv = inject(VenueTemplatesService);
    const breadcrumbsSrv = inject(BreadcrumbsService);
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
        return EMPTY;
    }
};
