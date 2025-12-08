import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { VenueTemplate, VenueTemplateScope, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const promoterVenueTemplateDetailsResolver: ResolveFn<VenueTemplate> = (route: ActivatedRouteSnapshot) => {
    const venueTemplatesSrv = inject(VenueTemplatesService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const tplId = Number(route.paramMap.get('venueTemplateId'));
    venueTemplatesSrv.venueTpl.load(tplId);
    return combineLatest([
        venueTemplatesSrv.venueTpl.get$(),
        venueTemplatesSrv.venueTpl.error$()
    ]).pipe(
        first(rs => rs.some(Boolean)),
        mergeMap(([venueTemplate, error]) => {
            if (error || venueTemplate.scope !== VenueTemplateScope.standard) {
                router.navigate(['/promoter-venue-templates']);
                return EMPTY;
            } else {
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], venueTemplate.name);
                return of(venueTemplate);
            }
        })
    );
};
