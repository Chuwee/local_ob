import { Metadata } from '@OneboxTM/utils-state';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ChannelPromotionsService } from '@admin-clients/cpanel-channels-promotions-data-access';
import { ChannelPromotionsListComponent } from '../list/channel-promotions-list.component';

@Component({
    selector: 'app-channel-promotions-container',
    templateUrl: './channel-promotions-container.component.html',
    styleUrls: ['./channel-promotions-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelPromotionsContainerComponent implements OnInit {

    sidebarWidth$: Observable<string>;
    isLoadingPromotion$: Observable<boolean>;
    promotionListMetadata$: Observable<Metadata>;
    @ViewChild(ChannelPromotionsListComponent) listComponent: ChannelPromotionsListComponent;

    constructor(
        private _breakpointObserver: BreakpointObserver,
        private _channelPromotionsService: ChannelPromotionsService
    ) { }

    ngOnInit(): void {
        this.sidebarWidth$ = this._breakpointObserver
            .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
            .pipe(
                map(result => result.matches ? '240px' : '290px')
            );
        this.isLoadingPromotion$ = this._channelPromotionsService.isPromotionInProgress$();
        this.promotionListMetadata$ = this._channelPromotionsService.getPromotionsListMetaData$();
    }

    newPromotion(): void {
        this.listComponent.openNewPromotionDialog();
    }

}
