import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { combineLatest, EMPTY } from 'rxjs';
import { filter, finalize, first, map, switchMap, tap } from 'rxjs/operators';

export const sessionDetailsResolver: ResolveFn<boolean> = route => {
    const sessionId = Number(route.paramMap.get('sessionId'));
    if (sessionId) {
        const eventsService = inject(EventsService);
        const sessionsService = inject(EventSessionsService);
        const breadcrumbsService = inject(BreadcrumbsService);
        return eventsService.event.get$().pipe(
            first(Boolean),
            tap(event => sessionsService.session.load(event.id, sessionId)),
            switchMap(() => combineLatest([sessionsService.session.get$(), sessionsService.session.error$()])),
            filter(([session, error]) => session?.id === sessionId || error !== null),
            tap(([session, error]) => breadcrumbsService.addDynamicSegment(!error && route.data['breadcrumb'] || '', session?.name)),
            map(([_, error]) => !error),
            finalize(() => sessionsService.session.cancelLoad())
        );
    } else {
        return EMPTY;
    }
};
