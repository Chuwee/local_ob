import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { GetProductEventSessionsStockAndPricesResponse, GetSessionsPriceRequest, ProductEventSessionStockAndPrices, ProductEventsService } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { EmptyStateTinyComponent, EphemeralMessageService, ObMatDialogConfig, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
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
import { BehaviorSubject, combineLatest, filter, first, map, Observable, shareReplay, tap } from 'rxjs';
import { ProductEventSessionsPricesFilterComponent } from './filter/product-event-sessions-prices-filter.component';
import { SetSessionsPricesConfigDialogComponent } from './set-sessions-config-dialog/set-sessions-config-prices-dialog.component';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-product-event-sessions-prices',
    imports: [
        AsyncPipe, SearchablePaginatedSelectionModule, TranslatePipe, FlexLayoutModule, FlexModule, ReactiveFormsModule, MatRadioModule,
        MatTooltipModule, MatIconModule, MatCheckboxModule, EmptyStateTinyComponent, MatProgressSpinnerModule, MatButtonModule,
        DateTimePipe, ProductEventSessionsPricesFilterComponent, FormContainerComponent, EllipsifyDirective
    ],
    templateUrl: './product-event-sessions-prices.component.html',
    styleUrls: ['./product-event-sessions-prices.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventPricesComponent implements OnInit, OnDestroy {
    readonly #productEventsSrv = inject(ProductEventsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #sessionsSelectedOnly = new BehaviorSubject(false);

    #configSessionsFilter: GetSessionsPriceRequest = {
        limit: PAGE_SIZE,
        offset: 0
    };

    readonly sessionsCtrl = this.#fb.control<ProductEventSessionStockAndPrices[]>({ value: [], disabled: true });
    readonly configSessionsSelectedOnly$ = this.#sessionsSelectedOnly.asObservable();
    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]).pipe(
        tap(canWrite => {
            if (canWrite) {
                this.sessionsCtrl.enable();
            }
        }),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    readonly isInProgress$ = booleanOrMerge([
        this.#productEventsSrv.productEvents.price.loading$(),
        this.#productEventsSrv.productEvents.sessions.loading$()
    ]);

    readonly allConfigSessions$: Observable<GetProductEventSessionsStockAndPricesResponse> = combineLatest([
        this.#productEventsSrv.productEvents.price.getData$().pipe(filter(Boolean)),
        this.#productEventsSrv.productEvents.price.getMetadata$(),
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

    ngOnInit(): void {
        this.loadSessionsList();
    }

    ngOnDestroy(): void {
        this.#productEventsSrv.productEvents.price.clear();
    }

    //TODO: Fix when we have variants
    mapSessionsToForm(sessions): ProductEventSessionStockAndPrices[] {
        return sessions?.map(session => ({
            id: session.id,
            name: session.name,
            start: session.dates.start,
            price: session.variants[0].price,
            use_custom_price: session.variants[0].use_custom_price,
            variant_id: session.variants[0].id,
            smartbooking: session.smart_booking?.type === 'SMART_BOOKING'
        }));
    }

    reloadSessionsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#configSessionsFilter = { ...this.#configSessionsFilter, limit, offset, q };
        this.loadSessionsList();
    }

    filterChangeHandler(filters: Partial<GetSessionsPriceRequest>): void {
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

    openSetSessionsConfigDialog(session: ProductEventSessionStockAndPrices): void {
        this.#matDialog.open<SetSessionsPricesConfigDialogComponent, { session: ProductEventSessionStockAndPrices },
            { variants: { id: number; price: number; use_custom_price: boolean }[] }>(
                SetSessionsPricesConfigDialogComponent, new ObMatDialogConfig({ session })
            ).beforeClosed()
            .subscribe(resp => {
                if (resp) {
                    //TODO: Fix when we have variants
                    const req = {
                        variants: [{
                            id: resp.variants[0].id,
                            price: resp.variants[0].price,
                            use_custom_price: resp.variants[0].use_custom_price
                        }]
                    };
                    this.#productEventsSrv.productEvents.price.updateSession(
                        this.productIdPath, this.eventIdPath, session.id, req
                    ).subscribe(() => {
                        this.loadSessionsList();
                        this.#ephemeralMessageSrv.showSuccess({ msgKey: 'PRODUCT.EVENTS.DETAIL.SESSIONS_PRICE.SAVE_SUCCESS' });
                    });
                }
            });
    }

    selectAll(change?: MatCheckboxChange): void {
        this.#productEventsSrv.productEvents.price.load(this.productIdPath, this.eventIdPath, {
            ...this.#configSessionsFilter,
            limit: undefined
        });
        this.#productEventsSrv.productEvents.price.getData$().pipe(
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

    private loadSessionsList(): void {
        const isFiltered = !!this.#configSessionsFilter.q || !!this.#configSessionsFilter.initStartDate || !!this.#configSessionsFilter.finalStartDate || !!this.#configSessionsFilter.weekdays || !!this.#configSessionsFilter.status;
        this.$sessionsFiltered.set(isFiltered);
        this.#productEventsSrv.productEvents.price.load(this.productIdPath, this.eventIdPath, this.#configSessionsFilter);
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
