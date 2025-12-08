import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ProductsService } from '@admin-clients/cpanel/products/my-products/data-access';
import { Event, EventsService, EventStatus, GetEventsRequest } from '@admin-clients/cpanel/promoters/events/data-access';
import { PostProductEvent, ProductEventsService } from '@admin-clients/cpanel-products-my-products-events-data-access';
import {
    DialogSize, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { IdName } from '@admin-clients/shared/data-access/models';
import { atLeastOneRequiredInArray, booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, OnDestroy, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, filter, first, map, tap } from 'rxjs';

const PAGE_SIZE = 10;
const ALL_EVENTS_FILTER: GetEventsRequest = {
    limit: 999,
    offset: 0,
    status: [EventStatus.inProgramming, EventStatus.planned, EventStatus.ready]
};
@Component({
    selector: 'app-new-product-events-dialog',
    imports: [
        TranslatePipe, ReactiveFormsModule, AsyncPipe, FlexLayoutModule, SearchablePaginatedSelectionModule,
        MatIcon, MatRadioButton, MatRadioGroup, MatCheckbox, MatError, MatProgressSpinner, MatIconButton, MatButton,
        MatDialogTitle, MatDialogContent, MatDialogActions
    ],
    templateUrl: './new-product-events-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewProductEventsDialogComponent implements OnInit, OnDestroy {

    readonly #productsSrv = inject(ProductsService);
    readonly #productEventsSrv = inject(ProductEventsService);
    readonly #eventsSrv = inject(EventsService);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<NewProductEventsDialogComponent>);
    readonly #elemRef = inject(ElementRef);
    readonly #onDestroy = inject(DestroyRef);

    #productId: number;
    #selection = [];
    #filter: GetEventsRequest = {
        limit: PAGE_SIZE,
        offset: 0,
        sort: 'name:asc',
        status: [EventStatus.inProgramming, EventStatus.planned, EventStatus.ready]
    };

    readonly metadata$ = this.#eventsSrv.eventsList.getMetadata$();
    readonly selectedEvents$ = this.#productEventsSrv.productEvents.list.get$()
        .pipe(
            filter(Boolean),
            tap(selectedEvents => {
                this.#selection = selectedEvents.map(event => event.event.id);
                this.form.controls.events.patchValue(selectedEvents.map(event => event.event));
                this.form.markAsPristine();
            })
        );

    readonly eventsData$ = this.#eventsSrv.eventsList.getData$().pipe(
        filter(values => values?.every(Boolean)),
        map(events => events.map(event => ({ id: event.id, name: event.name })))
    );

    readonly #$productCurrency = toSignal(this.#productsSrv.product.get$().pipe(map(product => product.currency_code)));

    readonly isInProgress$ = booleanOrMerge([
        this.#eventsSrv.eventsList.loading$(),
        this.#productEventsSrv.productEvents.list.loading$()
    ]);

    readonly form = this.#fb.group({
        events: this.#fb.control<IdName[]>([], atLeastOneRequiredInArray()),
        selectAll: [null as boolean, [Validators.required]]
    });

    readonly pageSize = PAGE_SIZE;
    readonly isGroupCheckBox: boolean;

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.LARGE);
        this.#productEventsSrv.productEvents.list.get$().pipe(
            takeUntilDestroyed(this.#onDestroy),
            filter(Boolean)
        ).subscribe(events => events.length && this.form.controls.selectAll.patchValue(false));

        this.form.controls.selectAll.valueChanges.pipe(takeUntilDestroyed(this.#onDestroy)).subscribe(value => {
            if (value) {
                this.#eventsSrv.eventsList.clear();
                this.#filter = { ...ALL_EVENTS_FILTER, currency: this.#$productCurrency() };
                this.loadEventsList();
            }
        });
    }

    ngOnDestroy(): void {
        this.#eventsSrv.eventsList.clear();
    }

    close(saved = false): void {
        this.#dialogRef.close(saved);
    }

    addProductEvents(): void {
        if (this.form.valid || this.form.value.selectAll) {
            combineLatest([
                this.#eventsSrv.eventsList.getData$().pipe(first(Boolean)),
                this.#productEventsSrv.productEvents.list.get$().pipe(first(Boolean))
            ])
                .subscribe(([events, productEvents]) => {
                    const includedEventIds = productEvents.map(prodEvn => prodEvn.event.id);
                    const eventsToAdd = this.form.value.selectAll ? events : this.form.value.events;
                    const newEventIds = eventsToAdd.filter(event => !includedEventIds.includes(event.id)).map(event => event.id);
                    if (newEventIds.length) {
                        this.save({ event_ids: newEventIds });
                    } else {
                        this.form.controls.events.setErrors({ atLeastOneRequired: true });
                        this.form.controls.events.markAsTouched();
                    }
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    reloadEventsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#filter = { ...this.#filter, limit, offset, q, currency: this.#$productCurrency() };
        this.loadEventsList();
    }

    isEventDisabled: (d: Event) => boolean = (d: Event) => this.#selection.includes(d.id);

    selectAllInGroup(eventsGroupNotDisabled: Event[]): void {
        this.form.controls.events.patchValue([
            ...this.form.value.events,
            ...eventsGroupNotDisabled
        ]);
    }

    diselectAllInGroup(eventsGroupNotDisabled: Event[]): void {
        const eventsToRemove = this.form.value.events.filter(event => !eventsGroupNotDisabled
            .map(event => event.id).includes(event.id));
        this.form.get('events').patchValue(eventsToRemove);
    }

    handleRestrictedSelectionGroupChange(checked: boolean): void {
        this.#eventsSrv.eventsList.getData$().subscribe(events => {
            const eventsGroupNotDisabled = events?.filter(event => !this.isEventDisabled(event)) || [];
            if (checked) this.selectAllInGroup(eventsGroupNotDisabled);
            else this.diselectAllInGroup(eventsGroupNotDisabled);
        });
    }

    private loadEventsList(): void {
        this.#productsSrv.product.get$().pipe(first(Boolean)).subscribe(product => {
            this.#productId = product.product_id;
            this.#filter.entityId = product.entity.id;
            this.#eventsSrv.eventsList.load(this.#filter);
        });
    }

    private save(reqBody: PostProductEvent): void {
        this.#productEventsSrv.productEvents.event.create(this.#productId, reqBody).subscribe(() => {
            this.close(true);
        });
    }

}
