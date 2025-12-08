import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelPromotionsService } from '@admin-clients/cpanel-channels-promotions-data-access';
import { BreadcrumbsService } from '@admin-clients/shared/common/ui/components';
import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { filter, first, withLatestFrom } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class ChannelPromotionDetailsResolverService {

    constructor(
        private _channelsService: ChannelsService,
        private _channelPromotionsSrv: ChannelPromotionsService,
        private _breadcrumbsService: BreadcrumbsService
    ) { }

    resolve(route: ActivatedRouteSnapshot): Observable<number> | Observable<never> {
        const promotionId = Number(route.paramMap.get('promotionId'));
        this._breadcrumbsService.addDynamicSegment(route.data['breadcrumb'], 'LOADING');
        this._channelPromotionsSrv.clearPromotionDetails();
        this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                withLatestFrom(this._channelPromotionsSrv.getPromotion$()),
                filter(([, promotion]) => !promotion || promotion.id !== promotionId)
            )
            .subscribe(([channel]) => {
                this._channelPromotionsSrv.loadPromotionDetails(channel.id, promotionId);
            });

        this._channelPromotionsSrv.getPromotion$()
            .pipe(first(promotion => promotion?.id === promotionId))
            .subscribe(promotion =>
                this._breadcrumbsService.addDynamicSegment(route.data['breadcrumb'], promotion.name)
            );

        return of(promotionId);
    }
}
