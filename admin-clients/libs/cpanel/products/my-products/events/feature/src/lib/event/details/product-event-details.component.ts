import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EventSessionsService } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { ProductEventsService, ProductEventStatus } from '@admin-clients/cpanel-products-my-products-events-data-access';
import {
    EphemeralMessageService, NavTabsMenuComponent
} from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, inject, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSlideToggle, MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, map, switchMap, take, tap } from 'rxjs';

@Component({
    selector: 'app-product-event-details',
    imports: [
        TranslatePipe, ReactiveFormsModule, NavTabsMenuComponent, AsyncPipe, RouterModule,
        MatSlideToggle, MatTooltip, MatProgressSpinner
    ],
    templateUrl: './product-event-details.component.html',
    styleUrls: ['./product-event-details.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventDetailsComponent implements OnInit, OnDestroy {
    readonly #productsSrv = inject(ProductsService);
    readonly #productEventsSrv = inject(ProductEventsService);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #eventSrv = inject(EventsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #fb = inject(FormBuilder);
    readonly #onDestroy = inject(DestroyRef);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);

    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);
    readonly isInProgress$ = booleanOrMerge([
        this.#productEventsSrv.productEvents.list.loading$(),
        this.#eventSrv.event.inProgress$()
    ]);

    readonly selectedProductEvent$ = combineLatest([
        this.#productEventsSrv.productEvents.list.get$(),
        this.#eventSrv.event.get$()
    ]).pipe(
        filter(([events, currentEvent]) => (events?.length > 0 && !!currentEvent)),
        map(([events, currentEvent]) => {
            const selectedEvent = events?.find(event => event.event.id === currentEvent.id);
            if (selectedEvent) {
                selectedEvent.event.status = currentEvent.status;
                this.form.controls.status.setValue(selectedEvent.status === ProductEventStatus.active);
            }
            return selectedEvent;
        })
    );

    readonly deliveryType$ = this.#productsSrv.product.delivery.get$()
        .pipe(map(delivery => delivery?.delivery_type));

    readonly $product = toSignal(this.#productsSrv.product.get$().pipe(take(1), filter(Boolean)));

    readonly canSetEventStatus$ = combineLatest([
        this.deliveryType$,
        this.#productEventsSrv.productEvents.deliveryPoints.get$(),
        this.#productEventsSrv.productEvents.sessions.deliveryPoints.getData$()
    ]).pipe(
        filter(data => data.every(Boolean)),
        map(([deliveryType, eventDeliveries, sessionsDeliveries]) =>
            deliveryType === 'PURCHASE' || deliveryType === 'FIXED_DATES'
            || !!eventDeliveries.length || !!sessionsDeliveries.length
        ),
        tap(canSetEventStatus => {
            if (canSetEventStatus) this.form.enable();
            else this.form.disable();
        })
    );

    readonly form = this.#fb.group({
        status: [false]
    });

    ngOnInit(): void {
        combineLatest([
            this.#productsSrv.product.get$(),
            this.#eventSrv.event.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([product, event]) => {
            if (product.product_type === 'SIMPLE' && product.stock_type === 'SESSION_BOUNDED') {
                this.#productEventsSrv.productEvents.stock.load(product.product_id, event.id, { limit: 10, offset: 0 });
            }
        });
    }

    ngOnDestroy(): void {
        this.#productEventsSrv.productEvents.sessions.clear();
        this.#eventSessionsSrv.sessionList.clear();
        this.#eventSrv.event.clear();
    }

    updateProductEventStatus({ checked }: MatSlideToggleChange): void {
        this.#productsSrv.product.get$().pipe(
            take(1),
            switchMap(product => this.#productEventsSrv.productEvents.event.update(
                product.product_id,
                this.eventIdPath,
                { status: checked ? ProductEventStatus.active : ProductEventStatus.inactive }
            ))
        ).subscribe({
            next: () => {
                this.#ephemeralMessageSrv.showSaveSuccess();
                this.reloadModels();
            },
            error: () => this.form.controls.status.setValue(!checked)
        });
    }

    get eventIdPath(): number | undefined {
        return parseInt(this.#activatedRoute.snapshot.params['eventId'], 10);
    }

    private reloadModels(): void {
        this.#productsSrv.product.get$().pipe(
            take(1),
            filter(Boolean)
        ).subscribe(product => {
            this.#productsSrv.product.delivery.load(product.product_id);
            this.#productEventsSrv.productEvents.list.load(product.product_id);
            this.#productEventsSrv.productEvents.sessions.load(product.product_id, this.eventIdPath);
            this.#productEventsSrv.productEvents.deliveryPoints.load(product.product_id, this.eventIdPath);
        });
        this.form.markAsPristine();
        this.form.markAsUntouched();
    }

}
