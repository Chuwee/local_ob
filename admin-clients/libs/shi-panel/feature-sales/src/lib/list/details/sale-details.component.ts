import { DetailOverlayData, DetailOverlayService } from '@OneboxTM/detail-overlay';
import {
    DialogSize, MessageDialogService, TimelineElement, TimelineElementStatus, VerticalTimelineComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AuthenticationService } from '@admin-clients/shi-panel/data-access-auth';
import { CustomScrollDirective } from '@admin-clients/shi-panel/utility-directives';
import { UserPermissions } from '@admin-clients/shi-panel/utility-models';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, switchMap } from 'rxjs';
import { SaleStatus } from '../../models/sale-status.enum';
import { saleTimelineStructure } from '../../models/sale-timeline.model';
import { Transition } from '../../models/sale-transition.model';
import { SalesService } from '../../sales.service';
import { SaleProductsComponent } from './sale-details-products/sale-details-products.component';

@Component({
    selector: 'app-sale-details',
    templateUrl: './sale-details.component.html',
    styleUrls: ['./sale-details.component.scss', '../sales-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule, MaterialModule, FlexLayoutModule, TranslatePipe, VerticalTimelineComponent, DateTimePipe,
        CustomScrollDirective, SaleProductsComponent
    ]
})
export class SaleDetailsComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #salesService = inject(SalesService);
    readonly #authService = inject(AuthenticationService);
    readonly #detailOverlayService = inject(DetailOverlayService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    readonly saleStatus = SaleStatus;
    readonly dateTimeFormats = DateTimeFormats;
    readonly hasWritePermissions$ = this.#authService.getLoggedUser$()
        .pipe(map(loggedUser => AuthenticationService.doesUserHaveSomePermission(loggedUser, [UserPermissions.salesWrite])));

    readonly isLoading$ = booleanOrMerge([
        this.#salesService.details.loading$(),
        this.#salesService.transitions.loading$()
    ]);

    readonly transitions$ = this.#salesService.transitions.get$().pipe(
        filter(Boolean),
        map(transitions => this.mapTransitions(transitions))
    );

    readonly data = inject(DetailOverlayData);
    readonly sale$ = this.#salesService.details.getData$().pipe(
        filter(Boolean),
        takeUntilDestroyed(this.#destroyRef)
    );

    ngOnInit(): void {
        this.#salesService.details.load(this.data.data);
        this.#salesService.transitions.load(this.data.data);
    }

    relaunchSale(id: number, type: string): void {
        this.#msgDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.RELAUNCH_SALE',
            message: 'SALES.RELAUNCH_SALE_WARNING',
            actionLabel: 'FORMS.ACTIONS.RELAUNCH',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() =>
                    (type === 'RELAUNCH') ? this.#salesService.details.relaunchSale(id) : this.#salesService.details.relaunchFulfill(id)
                )
            )
            .subscribe(() => {
                this.#detailOverlayService.close(true);
            });
    }

    private mapTransitions(transitions: Transition[]): TimelineElement[] {

        const timelineStructure = saleTimelineStructure;

        const timelineElements: TimelineElement[] = [];

        transitions.forEach(transition => {
            const timelineElement = {
                title: transition.action,
                date: transition.date,
                status: transition.status,
                description: transition.description ? transition.description : null
            };
            timelineElements.push(timelineElement);
        });

        timelineStructure.forEach(element => {
            const index = timelineElements.findIndex(e => e.title === element);
            if (index === -1 && (element !== 'SUPPLIER_SALE_UPDATE_REQUEST' && element !== 'SHI_SALE_UPDATE')) {
                timelineElements.push({ title: element, status: TimelineElementStatus.disabled });
            }
        });

        return timelineElements;

    }
}
