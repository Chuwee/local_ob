import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ResolveFn, Router } from '@angular/router';
import { combineLatest, defer } from 'rxjs';
import { finalize, first, map, tap } from 'rxjs/operators';

export const eventPromotionDetailsResolver: ResolveFn<boolean> = route => {

    const eventSvc = inject(EventsService);
    const eventPromotionsSvc = inject(EventPromotionsService);
    const breadcrumbsSvc = inject(BreadcrumbsService);
    const router = inject(Router);

    return defer(() => {

        const promoId = Number(route.paramMap.get('promotionId'));

        eventSvc.event.get$().pipe(first(event => Boolean(event))).subscribe(event =>
            eventPromotionsSvc.loadPromotionWithDetails(event.id, promoId)
        );

        return combineLatest([
            eventSvc.event.get$(),
            eventPromotionsSvc.promotion.get$(),
            eventPromotionsSvc.promotion.loading$(),
            eventPromotionsSvc.promotion.error$()
        ])
            .pipe(
                first(([, , loading]) => !loading),
                tap(([, promotion]) => breadcrumbsSvc.addDynamicSegment(route.data['breadcrumb'], promotion?.name || 'LOADING')),
                map(([event, , , error]) => {
                    if (error) {
                        router.navigate([`/events/${event.id}/promotions`]);
                        return false;
                    } else {
                        return true;
                    }
                }),
                finalize(() => eventPromotionsSvc.promotion.cancelLoad())
            );
    });

};
