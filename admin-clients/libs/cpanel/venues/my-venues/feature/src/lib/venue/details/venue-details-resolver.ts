import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';
import { VenueDetails, VenuesService } from '@admin-clients/cpanel/venues/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';

export const venueDetailsResolver: ResolveFn<VenueDetails> = (route: ActivatedRouteSnapshot) => {
    const venuesSrv = inject(VenuesService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);

    const venueId = parseInt(route.paramMap.get('venueId'));
    venuesSrv.loadVenue(venueId);

    return combineLatest([
        venuesSrv.getVenue$(),
        venuesSrv.getVenueError$()
    ]).pipe(
        first(([venue, error]) => !!venue || !!error),
        mergeMap(([venue, error]) => {
            if (error) {
                router.navigate(['/venues']);
                return EMPTY;
            }
            if (route?.data?.['breadcrumb']) {
                breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], venue.name);
            }
            return of(venue);
        })
    );
};
