/* eslint-disable @typescript-eslint/dot-notation */
import { LastPathGuardListenerDirective } from '@admin-clients/cpanel/common/utils';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ProductEvent, ProductEventsService } from '@admin-clients/cpanel-products-my-products-events-data-access';
import {
    DialogSize, EphemeralMessageService, ListFiltersService, MessageDialogService, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { LocalDateTimePipe } from '@admin-clients/shared/utility/pipes';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnInit, viewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatIconButton } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatSelectionList, MatListOption } from '@angular/material/list';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatTooltip } from '@angular/material/tooltip';
import { ActivatedRoute, NavigationEnd, Router } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import {
    combineLatest, debounceTime, filter, first, Observable, shareReplay, startWith, switchMap, tap
} from 'rxjs';
import { NewProductEventsDialogComponent } from '../create/new-product-events-dialog.component';
import { ProductEventsListFilterComponent } from './filter/product-events-list-filter.component';

@Component({
    selector: 'app-product-events-list',
    imports: [
        TranslatePipe, LastPathGuardListenerDirective, EllipsifyDirective, AsyncPipe,
        MatSelectionList, MatListOption, MatIcon, MatProgressSpinner, MatIconButton, MatTooltip,
        ProductEventsListFilterComponent, LocalDateTimePipe, MatDialogModule // TODO: remove MatDialogModule in Angular 19
    ],
    providers: [ListFiltersService],
    templateUrl: './product-events-list.component.html',
    styleUrls: ['./product-events-list.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEventsListComponent implements OnInit {
    readonly #productsSrv = inject(ProductsService);
    readonly #productEventsSrv = inject(ProductEventsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #msgDialogSrv = inject(MessageDialogService);
    readonly #matDialog = inject(MatDialog);
    readonly #router = inject(Router);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #cdRef = inject(ChangeDetectorRef);
    readonly #onDestroy = inject(DestroyRef);

    #allProductEvents: ProductEvent[];
    #productId: number;

    get #idPath(): string | undefined {
        return this.#activatedRoute.snapshot.children[0]?.params['eventId'];
    }

    get #innerPath(): string {
        return this.#activatedRoute.snapshot.children[0]?.children[0]?.routeConfig.path;
    }

    readonly listFilterComponent = viewChild<ProductEventsListFilterComponent>(ProductEventsListFilterComponent);
    readonly canWrite$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR, UserRoles.ENT_MGR, UserRoles.EVN_MGR]);
    readonly isLoading$ = this.#productEventsSrv.productEvents.list.loading$();

    selectedProductEventId: number;
    dateTimeFormats = DateTimeFormats;
    productEvents$: Observable<ProductEvent[]> = this.#productEventsSrv.productEvents.list.get$()
        .pipe(
            filter(Boolean),
            tap(eventsList => this.#allProductEvents = eventsList),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    ngOnInit(): void {
        this.handleSelectedEventChanges();
        this.handleRouterEventsChanges();
        this.handleProductEventsChangesForScroll();
        this.handleProductEventsChangesToNavigate();
        this.handleFiltersChanges();
    }

    openNewEventsDialog(): void {
        this.listFilterComponent().clear();
        this.#matDialog.open<NewProductEventsDialogComponent, null, number>(
            NewProductEventsDialogComponent, new ObMatDialogConfig()
        )
            .beforeClosed()
            .subscribe(saved => {
                if (saved) {
                    this.#ephemeralSrv.showSuccess({ msgKey: 'PRODUCT.EVENTS.ADD_EVENTS_SUCCESS' });
                    this.reloadEvents();
                }
            });
    }

    openDeleteEventDialog(): void {
        this.#eventsSrv.event.get$()
            .pipe(
                first(Boolean),
                switchMap(event => this.#msgDialogSrv.showWarn({
                    size: DialogSize.SMALL,
                    title: 'TITLES.DELETE_EVENT',
                    message: 'PRODUCT.EVENTS.DELETE_EVENT_WARNING',
                    messageParams: { name: event.name },
                    actionLabel: 'FORMS.ACTIONS.DELETE',
                    showCancelButton: true
                })),
                filter(Boolean),
                switchMap(() => {
                    const productId = this.#allProductEvents.at(0).product.id;
                    return this.#productEventsSrv.productEvents.event.delete(productId, Number(this.#idPath));
                })
            )
            .subscribe(() => {
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'PRODUCT.EVENTS.DELETE_PRODUCT_EVENT_SUCCESS'
                });
                this.reloadEvents();
            });
    }

    selectionChangeHandler(productEventId: number): void {
        if (!!productEventId && this.selectedProductEventId !== productEventId) {
            this.selectedProductEventId = productEventId;
            const path = this.currentPath();
            this.#router.navigate([path], { relativeTo: this.#activatedRoute });
        }
    }

    private reloadEvents(): void {
        this.#productsSrv.product.get$().pipe(first(Boolean)).subscribe(product => {
            this.#productEventsSrv.productEvents.list.load(product.product_id);
        });
    }

    private currentPath(): string {
        return this.#innerPath ?
            this.selectedProductEventId.toString() + '/' + this.#innerPath :
            this.selectedProductEventId.toString();
    }

    private handleSelectedEventChanges(): void {
        combineLatest([
            this.#eventsSrv.event.get$(),
            this.#eventsSrv.event.error$()
        ])
            .pipe(
                filter(([event, error]) => !!event || !!error),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(([event, error]) => {
                this.selectedProductEventId = error || !event ? null : event.id;
                this.#cdRef.markForCheck();
            });
    }

    private handleProductEventsChangesForScroll(): void {
        this.productEvents$
            .pipe(
                filter(productEvents => !!productEvents.length),
                debounceTime(500),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(() => {
                const event = this.selectedProductEventId;
                const element = document.getElementById('promotion-list-option-' + event);
                element?.scrollIntoView({ behavior: 'smooth', block: 'center' });
            });
    }

    private handleRouterEventsChanges(): void {
        this.#router.events.pipe(
            filter(event => event instanceof NavigationEnd),
            startWith(null as NavigationEnd),
            switchMap(() => this.productEvents$),
            filter(eventsList => !this.#idPath && !!eventsList.length),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(([firstEvent]) => {
            this.#router.navigate([firstEvent.event.id], { relativeTo: this.#activatedRoute });
        });
    }

    private handleProductEventsChangesToNavigate(): void {
        this.productEvents$
            .pipe(
                filter(eventsList =>
                    !!eventsList.length &&
                    this.#idPath &&
                    !eventsList.find(element => element.event.id.toString() === this.#idPath)),
                takeUntilDestroyed(this.#onDestroy)
            ).subscribe(([firstEvent]) => {
                this.#router.navigate([firstEvent.event.id], { relativeTo: this.#activatedRoute });
            });
    }

    private handleFiltersChanges(): void {
        this.#productsSrv.product.get$().pipe(first(Boolean)).subscribe(product => {
            this.#productId = product.product_id;
        });
        this.listFilterComponent().filterChange.subscribe(filters => {
            this.#productEventsSrv.productEvents.list.load(this.#productId, filters);
        });
    }
}
