import { SeasonTicketPromotionsService } from '@admin-clients/cpanel/promoters/season-tickets/promotions/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { SeasonTicketPromotionsListComponent } from '../list/season-ticket-promotions-list.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        RouterOutlet,
        EmptyStateComponent,
        TranslatePipe,
        CommonModule,
        SeasonTicketPromotionsListComponent
    ],
    selector: 'app-season-ticket-promotions-container',
    templateUrl: './season-ticket-promotions-container.component.html',
    styleUrls: ['./season-ticket-promotions-container.component.scss']
})
export class SeasonTicketPromotionsContainerComponent {
    private readonly _stPromotionsSrv = inject(SeasonTicketPromotionsService);

    @ViewChild(SeasonTicketPromotionsListComponent) private readonly _listComponent: SeasonTicketPromotionsListComponent;

    readonly sidebarWidth$ = inject(BreakpointObserver)
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(
            map(result => result.matches ? '240px' : '290px')
        );

    readonly isLoadingPromotion$ = this._stPromotionsSrv.promotion.loading$();
    readonly seasonTicketPromotionListMetadata$ = this._stPromotionsSrv.promotionsList.getMetadata$();

    newPromotion(): void {
        this._listComponent.openNewPromotionDialog();
    }
}
