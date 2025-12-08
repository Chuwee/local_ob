import { PromotionTpl, PromotionTplsService } from '@admin-clients/cpanel/promoters/promotion-templates/data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, ResolveFn, Router } from '@angular/router';
import { combineLatest, EMPTY, of } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

export const promotionTplDetailsResolver: ResolveFn<PromotionTpl> = (route: ActivatedRouteSnapshot) => {
    const promotionTplsSrv = inject(PromotionTplsService);
    const router = inject(Router);
    const breadcrumbsService = inject(BreadcrumbsService);

    const id = route.paramMap.get('promotionTemplateId');
    promotionTplsSrv.loadPromotionTemplate(Number(id));
    promotionTplsSrv.loadPromotionTplChannels(Number(id));
    promotionTplsSrv.loadPromotionTplChannelContents(Number(id));

    return combineLatest([
        promotionTplsSrv.getPromotionTemplate$(),
        promotionTplsSrv.getPromotionTplChannels$(),
        promotionTplsSrv.getPromotionTplChannelContents$(),
        combineLatest([
            promotionTplsSrv.getPromotionTemplateError$(),
            promotionTplsSrv.getPromotionTplChannelsError$(),
            promotionTplsSrv.getPromotionTplChannelContentsError$()
        ])
    ])
        .pipe(
            first(([promotionTpl, ch, cn, errors]) =>
                [promotionTpl, ch, cn].every(model => !!model) || errors.some(error => !!error)
            ),
            mergeMap(([promotionTpl, ch, cn, errors]) => {
                if (errors.some(error => !!error)) {
                    router.navigate(['/event-promotion-templates']);
                    return EMPTY;
                }
                if (route.data?.['breadcrumb']) {
                    breadcrumbsService.addDynamicSegment(route.data['breadcrumb'], promotionTpl.name);
                }

                return of(promotionTpl);
            })
        );
};
