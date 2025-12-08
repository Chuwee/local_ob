import { Metadata } from '@OneboxTM/utils-state';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { GetProductEventSessionsStockAndPricesResponse, GetSessionsStockRequest, ProductEventSessionStockAndPrices, ProductEventsService } from '@admin-clients/cpanel-products-my-products-events-data-access';
import { EmptyStateTinyComponent, EphemeralMessageService, ObMatDialogConfig, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats, IdName } from '@admin-clients/shared/data-access/models';
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
import { ProductEventSessionsStockFilterComponent } from './filter/product-event-sessions-stock-filter.component';
import { SetSessionsConfigDialogComponent } from './set-sessions-config-dialog/set-sessions-config-dialog.component';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-product-event-sessions-stock-and-prices',
    imports: [
        AsyncPipe, SearchablePaginatedSelectionModule, TranslatePipe, FlexLayoutModule, FlexModule, ReactiveFormsModule, MatRadioModule,
        MatTooltipModule, MatIconModule, MatCheckboxModule, EmptyStateTinyComponent, MatProgressSpinnerModule, MatButtonModule,
        DateTimePipe, ProductEventSessionsStockFilterComponent, FormContainerComponent, EllipsifyDirective
    ],
    templateUrl: './product-event-sessions-stock.component.html',
    styleUrls: ['./product-event-sessions-stock.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventStockComponent implements OnInit, OnDestroy {
    readonly #productEventsSrv = inject(ProductEventsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #ephemeralMessageSrv = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #sessionsSelectedOnly = new BehaviorSubject(false);

    #configSessionsFilter: GetSessionsStockRequest = {
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
        this.#productEventsSrv.productEvents.stock.loading$(),
        this.#productEventsSrv.productEvents.sessions.loading$()
    ]);

    readonly allConfigSessions$: Observable<GetProductEventSessionsStockAndPricesResponse> = combineLatest([
        this.#productEventsSrv.productEvents.stock.getData$().pipe(filter(Boolean)),
        this.#productEventsSrv.productEvents.stock.getMetadata$(),
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
        this.#productEventsSrv.productEvents.stock.clear();
    }

    mapSessionsToForm(sessions): ProductEventSessionStockAndPrices[] {
        return sessions?.map(session => ({
            id: session.id,
            name: session.name,
            start: session.dates.start,
            stock: session.stock,
            use_custom_stock: session.use_custom_stock,
            smartbooking: session.smart_booking?.type === 'SMART_BOOKING'
        }));
    }

    reloadSessionsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#configSessionsFilter = { ...this.#configSessionsFilter, limit, offset, q };
        this.loadSessionsList();
    }

    filterChangeHandler(filters: Partial<GetSessionsStockRequest>): void {
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
        this.#matDialog.open<SetSessionsConfigDialogComponent, { session: IdName }, { stock: number; use_custom_stock: boolean }>(
            SetSessionsConfigDialogComponent, new ObMatDialogConfig({ session })
        ).beforeClosed()
            .subscribe(resp => {
                if (resp) {
                    this.#productEventsSrv.productEvents.stock.updateSession(
                        this.productIdPath, this.eventIdPath, session.id, { stock: resp.stock, use_custom_stock: resp.use_custom_stock }
                    ).subscribe(() => {
                        this.loadSessionsList();
                        this.#ephemeralMessageSrv.showSaveSuccess();
                    });
                }
            });
    }

    selectAll(change?: MatCheckboxChange): void {
        this.#productEventsSrv.productEvents.stock.load(this.productIdPath, this.eventIdPath, {
            ...this.#configSessionsFilter,
            limit: undefined
        });
        this.#productEventsSrv.productEvents.stock.getData$().pipe(
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
        this.#productEventsSrv.productEvents.stock.load(this.productIdPath, this.eventIdPath, this.#configSessionsFilter);
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
