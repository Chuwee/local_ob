import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { SeasonTicketPromotionsService } from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ResolveFn, Router } from '@angular/router';
import { combineLatest, defer } from 'rxjs';
import { finalize, first, map, tap } from 'rxjs/operators';

export const seasonTicketPromotionDetailsResolver: ResolveFn<boolean> = route => {
    const stSrv = inject(SeasonTicketsService);
    const stPromotionsSrv = inject(SeasonTicketPromotionsService);
    const breadcrumbsSrv = inject(BreadcrumbsService);
    const router = inject(Router);

    return defer(() => {
        const promotionId = Number(route.paramMap.get('promotionId'));
        stSrv.seasonTicket.get$()
            .pipe(first(Boolean))
            .subscribe(st => {
                stPromotionsSrv.loadPromotionWithDetails(st.id, promotionId);
            });
        return combineLatest([
            stSrv.seasonTicket.get$(),
            stPromotionsSrv.promotion.get$(),
            stPromotionsSrv.promotion.loading$(),
            stPromotionsSrv.promotion.error$()
        ]).pipe(
            first(([, , loading]) => !loading),
            tap(([, promotion]) => breadcrumbsSrv.addDynamicSegment(route.data['breadcrumb'], promotion?.name || 'LOADING')),
            map(([st, , , error]) => {
                if (error) {
                    router.navigate([`/season-tickets/${st.id}/promotions`]);
                    return false;
                } else {
                    return true;
                }
            }),
            finalize(() => stPromotionsSrv.promotion.cancelLoad())
        );
    });
};
