import { EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { EmptyStateComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { RouterOutlet } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { EventPromotionsListComponent } from '../list/event-promotions-list.component';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FlexLayoutModule,
        MaterialModule,
        RouterOutlet,
        EmptyStateComponent,
        TranslatePipe,
        CommonModule,
        EventPromotionsListComponent
    ],
    selector: 'app-event-promotions-container',
    templateUrl: './event-promotions-container.component.html',
    styleUrls: ['./event-promotions-container.component.scss']
})
export class EventPromotionsContainerComponent {
    private readonly _eventPromotionsService = inject(EventPromotionsService);

    @ViewChild(EventPromotionsListComponent) private readonly _listComponent: EventPromotionsListComponent;

    readonly sidebarWidth$ = inject(BreakpointObserver)
        .observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium])
        .pipe(
            map(result => result.matches ? '240px' : '290px')
        );

    readonly isLoadingPromotion$ = this._eventPromotionsService.promotion.loading$();
    readonly eventPromotionListMetadata$ = this._eventPromotionsService.promotionsList.getMetadata$();

    newPromotion(): void {
        this._listComponent.openNewPromotionDialog();
    }

}
