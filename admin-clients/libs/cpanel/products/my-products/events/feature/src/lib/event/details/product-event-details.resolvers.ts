import { Event, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService, SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { GetProductEventSessionsResponse, ProductEventsService } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const productEventDetailsResolver: ResolveFn<Event> = (route: ActivatedRouteSnapshot) => {
    const eventsSrv = inject(EventsService);
    const eventSessionsSrv = inject(EventSessionsService);
    const router = inject(Router);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const allRouteParams = Object.assign({}, ...route.pathFromRoot.map(path => path.params));
    const productId = allRouteParams.productId;
    const eventId = allRouteParams.eventId;

    eventsSrv.event.clear();
    eventsSrv.event.load(eventId);
    eventSessionsSrv.sessionList.load(
        Number(eventId),
        { limit: 10, status: [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled] }
    );

    return combineLatest([
        eventsSrv.event.get$(),
        eventsSrv.event.error$()
    ]).pipe(
        first(([event, eventError]) => !!(event || eventError)),
        mergeMap(([event, eventError]) => {
            if (eventError) {
                router.navigate(['/products', productId, 'events']);
                return EMPTY;
            } else {
                breadcrumbsSrv.addDynamicSegment(route.data?.['breadcrumb'], event.name);
                return of(event);
            }
        }));
};

export const productEventSessionsAndDeliveryResolver: ResolveFn<GetProductEventSessionsResponse> = (route: ActivatedRouteSnapshot) => {
    const productEventsSrv = inject(ProductEventsService);
    const router = inject(Router);
    const allRouteParams = Object.assign({}, ...route.pathFromRoot.map(path => path.params));
    const productId = allRouteParams.productId;
    const eventId = allRouteParams.eventId;

    productEventsSrv.productEvents.sessions.load(Number(productId), Number(eventId));

    return combineLatest([
        productEventsSrv.productEvents.sessions.get$(),
        productEventsSrv.productEvents.sessions.error$()
    ]).pipe(
        first(([sessions, sessionsError]) => !!(sessions || sessionsError)),
        mergeMap(([sessions, sessionsError]) => {
            if (sessionsError) {
                router.navigate(['/products', productId, 'events']);
                return EMPTY;
            } else {
                return of(sessions);
            }
        }));
};
