import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY } from 'rxjs';
import { filter, first, map, mergeMap } from 'rxjs/operators';

export const eventResolver: ResolveFn<Event> = (route: ActivatedRouteSnapshot) => {
    const eventsSrv = inject(EventsService);
    const entitiesSrv = inject(EntitiesBaseService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const id = route.paramMap.get('eventId');

    eventsSrv.event.clear();
    eventsSrv.event.load(id);
    return combineLatest([
        eventsSrv.event.inProgress$(),
        eventsSrv.event.get$(),
        eventsSrv.event.error$()
    ]).pipe(
        map(([loading, event, error]) => !loading && (event || error)),
        filter(Boolean),
        mergeMap(result => {
            if (result instanceof HttpErrorResponse) {
                router.navigate(['/events']);
                return EMPTY;
            } else {
                entitiesSrv.clearEntity();
                entitiesSrv.loadEntity(result.entity.id);
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], result.name);
                return entitiesSrv.getEntity$()
                    .pipe(
                        first(Boolean),
                        map(() => result)
                    );
            }
        }));
};
