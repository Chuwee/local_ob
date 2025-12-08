import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProductsDeliveryPointsService } from '@admin-clients/cpanel/products/delivery-points/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { GetSessionsRequest, SessionStatus } from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { SessionsSelectorFilterComponent } from '@admin-clients/cpanel/shared/feature/sessions-selector-filter';
import {
    GetProductEventSessionsDeliveryPointsResponse, ProductEventSessionDeliveryPoint, ProductEventsService,
    PutProductEventDeliveryPoint, PutProductEventSessionDeliveryPoints
} from '@admin-clients/cpanel-products-my-products-events-data-access';
import {
    EmptyStateTinyComponent, EphemeralMessageService, ObMatDialogConfig, SearchablePaginatedSelectionLoadEvent,
    SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { atLeastOneRequiredInArray, booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule, FlexModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxChange, MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ActivatedRoute } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, filter, first, map, Observable, shareReplay, take, tap, throwError } from 'rxjs';
import { ProductDeliveryPointsSelectorComponent } from './delivery-points-selector/product-delivery-points-selector.component';
import {
    SetSessionsDeliveryPointsDialogComponent
} from './set-sessions-delivery-points-dialog/set-sessions-delivery-points-dialog.component';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-product-event-details-delivery-points',
    imports: [
        AsyncPipe, SearchablePaginatedSelectionModule, TranslatePipe, FlexLayoutModule, FlexModule, ReactiveFormsModule, MatRadioModule,
        MatTooltipModule, MatIconModule, MatCheckboxModule, EmptyStateTinyComponent, MatProgressSpinnerModule, MatButtonModule,
        DateTimePipe, SessionsSelectorFilterComponent, FormContainerComponent, EllipsifyDirective,
        ProductDeliveryPointsSelectorComponent, SessionsSelectorFilterComponent
    ],
    templateUrl: './product-event-delivery-points.component.html',
    styleUrls: ['./product-event-delivery-points.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventDeliveryPointsComponent implements OnInit, OnDestroy {
    readonly #productEventsSrv = inject(ProductEventsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #productsSrv = inject(ProductsService);
    readonly #deliveryPointsSrv = inject(ProductsDeliveryPointsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #sessionsSelectedOnly = new BehaviorSubject(false);
    readonly #onDestroy = inject(DestroyRef);

    #selectedDeliveryPoints: IdName[] = [];
    #defaultStatusFilter = [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled];
    #configSessionsFilter: GetSessionsRequest = {
        limit: PAGE_SIZE,
        offset: 0,
        sort: 'start_date:asc',
        status: this.#defaultStatusFilter
    };

    readonly $eventDeliveryPoints = toSignal(this.#productEventsSrv.productEvents.deliveryPoints.get$().pipe(filter(Boolean)));
    readonly sessionsCtrl = this.#fb.control<ProductEventSessionDeliveryPoint[]>({ value: [], disabled: true });
    readonly configSessionsSelectedOnly$ = this.#sessionsSelectedOnly.asObservable();
    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]).pipe(
        tap(canWrite => {
            if (canWrite) {
                this.sessionsCtrl.enable();
                this.eventDeliveryPointsCtrl.enable();
            }
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly isInProgress$ = booleanOrMerge([
        this.#productEventsSrv.productEvents.sessions.deliveryPoints.loading$(),
        this.#productEventsSrv.productEvents.deliveryPoints.loading$()
    ]);

    readonly allConfigSessions$: Observable<GetProductEventSessionsDeliveryPointsResponse> = combineLatest([
        this.#productEventsSrv.productEvents.sessions.deliveryPoints.getData$().pipe(filter(Boolean)),
        this.#productEventsSrv.productEvents.sessions.deliveryPoints.getMetadata$(),
        this.configSessionsSelectedOnly$
    ]).pipe(
        filter(([data, metadata]) => !!data && !!metadata),
        map(([data, metadata, selectedOnly]) => {
            const response = { data, metadata };
            if (selectedOnly) {
                response.data = this.sessionsCtrl.value
                    .sort((a, b) => a.name.localeCompare(b.name))
                    .slice(this.#configSessionsFilter.offset, this.#configSessionsFilter.offset + this.pageSize);
                response.metadata = {
                    total: this.sessionsCtrl.value.length,
                    offset: this.#configSessionsFilter.offset,
                    limit: this.pageSize
                } as Metadata;
            }
            this.sessionsCtrl.markAsUntouched();
            this.sessionsCtrl.markAsPristine();
            return response;
        }),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly allConfigSessionsMetadata$ = this.allConfigSessions$.pipe(
        map(resp => resp?.metadata),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly allConfigSessionsData$ = this.allConfigSessions$.pipe(
        map(resp => this.mapSessionsToForm(resp.data)),
        shareReplay({ bufferSize: 1, refCount: true })
    );

    readonly pageSize = PAGE_SIZE;
    readonly dateTimeFormats = DateTimeFormats;
    readonly $sessionsFiltered = signal(false);

    readonly eventDeliveryPointsCtrl = this.#fb.control<IdName[]>(
        { value: [], disabled: true },
        { validators: atLeastOneRequiredInArray() }
    );

    readonly allProductDeliveryPoints$ = this.#deliveryPointsSrv.productsDeliveryPointsList.getData$()
        .pipe(
            filter(Boolean),
            map(deliveryPoints => deliveryPoints.map(deliveryPoint => ({ id: deliveryPoint.id, name: deliveryPoint.name }))),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    defaultEventDeliveryPoints: string;

    readonly $getEmptyStateTitle = computed(() => !this.$eventDeliveryPoints().length
        ? 'PRODUCT.EVENTS.DETAIL.SESSIONS.EMPTY_STATE.NO_DELIVERY_POINT.TITLE'
        : this.$sessionsFiltered()
            ? 'PRODUCT.VARIANTS.EMPTY_LIST_MESSAGE'
            : 'PRODUCT.EVENTS.DETAIL.SESSIONS.EMPTY_STATE.NO_SESSIONS.TITLE');

    readonly $getEmptyStateDesc = computed(() => !this.$eventDeliveryPoints().length
        ? 'PRODUCT.EVENTS.DETAIL.SESSIONS.EMPTY_STATE.NO_DELIVERY_POINT.DESCRIPTION'
        : this.$sessionsFiltered()
            ? ''
            : 'PRODUCT.EVENTS.DETAIL.SESSIONS.EMPTY_STATE.NO_SESSIONS.DESCRIPTION');

    ngOnInit(): void {
        combineLatest([
            this.#eventsSrv.event.get$(),
            this.#productsSrv.product.get$()
        ]).pipe(
            filter(resp => resp.every(Boolean)),
            takeUntilDestroyed(this.#onDestroy),
            tap(([event, product]) => {
                this.#productEventsSrv.productEvents.sessions.deliveryPoints.load(product.product_id, event.id, this.#configSessionsFilter);
                this.#productEventsSrv.productEvents.deliveryPoints.load(product.product_id, event.id);
            })).subscribe();

        this.handleEventDeliveryPointsChanges();
    }

    ngOnDestroy(): void {
        this.#productEventsSrv.productEvents.sessions.deliveryPoints.clear();
    }

    cancel(): void {
        this.loadSessionsList();
    }

    save(): void {
        this.save$().subscribe(() => this.loadSessionsList());
    }

    save$(): Observable<void> {
        if (this.eventDeliveryPointsCtrl.valid) {
            return this.getProductEventDeliveryPointsUpdateReq();
        } else {
            this.eventDeliveryPointsCtrl.markAllAsTouched();
            //SetValue in order to rerender child components with form fields in order to show input errors.
            this.eventDeliveryPointsCtrl.setValue(this.eventDeliveryPointsCtrl.getRawValue());
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'invalid product event detail form');
        }
    }

    mapSessionsToForm(sessions): ProductEventSessionDeliveryPoint[] {
        return sessions?.map(session => ({
            id: session.id,
            name: session.name,
            start: session.dates?.start || session.start,
            delivery_points: session.delivery_points,
            smartbooking: session.smart_booking?.type === 'SMART_BOOKING' || session.smartbooking
        }));
    }

    reloadSessionsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#configSessionsFilter = { ...this.#configSessionsFilter, limit, offset, q };
        this.loadSessionsList();
    }

    filterChangeHandler(filters: Partial<GetSessionsRequest>): void {
        this.#configSessionsFilter = {
            ...this.#configSessionsFilter,
            ...filters
        };
        this.loadSessionsList();
    }

    changeSessionsSelectedOnly(): void {
        if (!this.#sessionsSelectedOnly.value) {
            this.#configSessionsFilter.offset = 0;
        }
        this.#sessionsSelectedOnly.next(!this.#sessionsSelectedOnly.value);
    }

    openSetSessionsDeliveryPointsDialog(session?: IdName & { start: string; deliveryPoints: IdName[] }): void {
        this.#matDialog.open<SetSessionsDeliveryPointsDialogComponent, { session }, IdName[]>(
            SetSessionsDeliveryPointsDialogComponent, new ObMatDialogConfig({
                session
            })
        ).beforeClosed()
            .subscribe(selectedDeliveryPoints => {
                if (selectedDeliveryPoints?.length) {
                    this.#selectedDeliveryPoints = selectedDeliveryPoints;
                    this.getProductEventSessionsDeliveryPointsUpdateReq(session).pipe(
                        take(1)
                    ).subscribe(() => {
                        this.sessionsCtrl.setValue([]);
                        this.#sessionsSelectedOnly.next(false);
                        this.loadSessionsList();
                    });
                }
            });
    }

    selectAll(change?: MatCheckboxChange): void {
        this.#productEventsSrv.loadAllSessionDeliveries(this.productIdPath, this.eventIdPath, {
            ...this.#configSessionsFilter,
            limit: undefined
        });
        this.#productEventsSrv.getAllSessionDeliveriesData$().pipe(
            first(Boolean),
            map(allSessions => this.mapSessionsToForm(allSessions))
        ).subscribe(allSessions => {
            if (change?.checked) {
                this.sessionsCtrl.patchValue(allSessions);
            } else {
                this.sessionsCtrl.patchValue([]);
            }
            this.sessionsCtrl.markAsTouched();
            this.sessionsCtrl.markAsDirty();
        });
    }

    getDeliveryPointsNames(deliveryPoints: IdName[]): string {
        return deliveryPoints?.map(dp => dp.name).join(', ');
    }

    private loadSessionsList(): void {
        const isFiltered = !!this.#configSessionsFilter.q || !!this.#configSessionsFilter.initStartDate || !!this.#configSessionsFilter.finalStartDate || !!this.#configSessionsFilter.weekdays || !!this.#configSessionsFilter.status;
        this.$sessionsFiltered.set(isFiltered);
        this.#productEventsSrv.productEvents.sessions.deliveryPoints.load(this.productIdPath, this.eventIdPath, this.#configSessionsFilter);
        this.#productEventsSrv.productEvents.deliveryPoints.load(this.productIdPath, this.eventIdPath);
    }

    private handleEventDeliveryPointsChanges(): void {
        this.#productEventsSrv.productEvents.deliveryPoints.get$()
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(deliveryPoints => {
                this.eventDeliveryPointsCtrl.patchValue(deliveryPoints.map(dp => dp.delivery_point));
                this.defaultEventDeliveryPoints = deliveryPoints.map(dp => dp.delivery_point.name).join(', ');
            });
    }

    private getProductEventDeliveryPointsUpdateReq(): Observable<void> {
        const eventDeliveryPoints: PutProductEventDeliveryPoint[] = this.eventDeliveryPointsCtrl.value.map((dp, i) => ({
            delivery_point_id: dp.id,
            is_default: i === 0
        }));
        return this.#productEventsSrv.productEvents.deliveryPoints.update(this.productIdPath, this.eventIdPath, eventDeliveryPoints).pipe(
            tap(() => {
                this.#ephemeralMessageSrv.showSaveSuccess();
                this.eventDeliveryPointsCtrl.markAsPristine();
            })
        );
    }

    private getProductEventSessionsDeliveryPointsUpdateReq(singleSession = null): Observable<void> {
        const selection = singleSession ? [singleSession] : this.sessionsCtrl.value;
        const eventSessionsDeliveryPoints: PutProductEventSessionDeliveryPoints[] = selection.map(session => ({
            id: session.id,
            delivery_points: this.#selectedDeliveryPoints.map((dp, i) => ({
                delivery_point_id: dp.id,
                is_default: i === 0
            }))
        }));
        return this.#productEventsSrv.productEvents.sessions.deliveryPoints.update(this.productIdPath, this.eventIdPath, eventSessionsDeliveryPoints)
            .pipe(
                tap(() => {
                    this.#ephemeralMessageSrv.showSaveSuccess();
                    this.sessionsCtrl.setValue([]);
                })
            );
    }

    get eventIdPath(): number | undefined {
        const allRouteParams = Object.assign({}, ...this.#activatedRoute.snapshot.pathFromRoot.map(path => path.params));
        return parseInt(allRouteParams.eventId);
    }

    get productIdPath(): number | undefined {
        const allRouteParams = Object.assign({}, ...this.#activatedRoute.snapshot.pathFromRoot.map(path => path.params));
        return parseInt(allRouteParams.productId);
    }
}
