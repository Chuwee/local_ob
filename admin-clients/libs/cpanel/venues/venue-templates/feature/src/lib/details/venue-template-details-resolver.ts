import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { VenueTemplate, VenueTemplateScope, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';

export const venueTemplateDetailsResolver: ResolveFn<VenueTemplate> = (route: ActivatedRouteSnapshot) => {
    const venueTemplatesSrv = inject(VenueTemplatesService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    venueTemplatesSrv.venueTpl.load(Number(route.paramMap.get('venueTemplateId')));

    return combineLatest([
        venueTemplatesSrv.venueTpl.get$(),
        venueTemplatesSrv.venueTpl.error$()
    ]).pipe(
        first(rs => rs.some(r => r !== null)),
        mergeMap(([venueTemplate, error]) => {
            if (error || venueTemplate.scope !== VenueTemplateScope.archetype) {
                router.navigate(['/venue-templates']);
                return EMPTY;
            }
            if (route.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], venueTemplate.name);
            }
            return of(venueTemplate);
        })
    );
};
