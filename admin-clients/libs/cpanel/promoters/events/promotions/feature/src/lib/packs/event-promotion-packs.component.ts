import { Metadata } from '@OneboxTM/utils-state';
import { GetPacksRequest, PacksService } from '@admin-clients/cpanel/packs/my-packs/data-access';
import { EventPromotionsService } from '@admin-clients/cpanel/promoters/events/promotions/data-access';
import { SearchablePaginatedSelectionModule } from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { differenceWith, FormControlHandler, unionWith } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, EventEmitter, inject, input, OnInit
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';
import { MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable } from 'rxjs';
import {
    distinctUntilChanged, filter, map, scan, shareReplay, startWith, switchMap, take, withLatestFrom
} from 'rxjs/operators';
import {
    EventPromotionPacksListElement as PackElement
} from './event-promotion-packs-list-element.model';

const PAGE_SIZE = 10;

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MatLabel, MatTooltip, MatCheckbox, MatIcon, AsyncPipe, TranslatePipe, ReactiveFormsModule,
        SearchablePaginatedSelectionModule, MatButtonModule
    ],
    selector: 'app-event-promotion-packs',
    templateUrl: './event-promotion-packs.component.html',
    styleUrls: ['./event-promotion-packs.component.scss']
})
export class EventPromotionPacksComponent implements OnInit {
    readonly #eventPromotionsService = inject(EventPromotionsService);
    readonly #destroyRef = inject(DestroyRef);
    readonly #packsSrv = inject(PacksService);

    #isSelectAllChecked: boolean;
    #filters: GetPacksRequest = {
        limit: PAGE_SIZE,
        offset: 0
    };

    readonly dateTimeFormats = DateTimeFormats;
    readonly pageSize = PAGE_SIZE;
    readonly showSelectedOnlyClick = new EventEmitter<void>();

    readonly selectedOnly$ = this.showSelectedOnlyClick.pipe(
        scan((isSelectedOnlyMode: boolean) => !isSelectedOnlyMode, false),
        startWith(false),
        takeUntilDestroyed(),
        shareReplay(1)
    );

    readonly metadata$ = this.selectedOnly$.pipe(
        switchMap(isActive => isActive ?
            this.selectedPacks$.pipe(map(list => new Metadata({ total: list?.length, limit: 999, offset: 0 }))) :
            this.#packsSrv.packsList.getMetadata$()
        ),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly loading$ = this.#packsSrv.packsList.loading$();

    readonly totalPacks$ = this.#packsSrv.packsList.getMetadata$().pipe(map(metadata => metadata.total));
    readonly allPacks$ = this.#packsSrv.packsList.getData$().pipe(
        filter(Boolean),
        map((data => data.map(pack => ({
            id: pack.id,
            name: pack.name
        })))),
        shareReplay(1)
    );

    readonly hasSelectablePacks$ = this.allPacks$
        .pipe(
            map(packs => !!packs?.length),
            shareReplay(1)
        );

    readonly packsList$ = this.selectedOnly$.pipe(
        switchMap(isActive => isActive ? this.selectedPacks$ : this.allPacks$),
        shareReplay(1)
    );

    readonly $eventId = input<number, number>(null, {
        alias: 'eventId', transform: eventId => {
            this.#filters.eventId = eventId;
            return eventId;
        }
    });

    readonly $packsForm = input<FormGroup>(null, { alias: 'packsForm' });
    readonly $promotionId = input<number>(null, { alias: 'promotionId' });

    selectedPacks$: Observable<PackElement[]>;

    ngOnInit(): void {
        this.#eventPromotionsService.promotionPacks.load(this.$eventId(), this.$promotionId());

        this.selectedPacks$ = this.$packsForm().controls['packs'].valueChanges
            .pipe(
                filter(Boolean),
                takeUntilDestroyed(this.#destroyRef),
                shareReplay(1)
            );

        this.selectedPacks$.subscribe();

        this.#eventPromotionsService.promotionPacks.get$()
            .pipe(filter(Boolean), takeUntilDestroyed(this.#destroyRef))
            .subscribe(promPacks => {
                this.$packsForm().patchValue({
                    allow_entity_packs: promPacks.allow_entity_packs || false,
                    packs: promPacks.packs || []
                });
                this.$packsForm().markAsPristine();
                this.$packsForm().markAsUntouched();
            });

        this.$packsForm().get('allow_entity_packs').valueChanges
            .pipe(distinctUntilChanged(), takeUntilDestroyed(this.#destroyRef))
            .subscribe(enabled => {
                enabled ? this.$packsForm().get('packs').enable() : this.$packsForm().get('packs').disable();
            });

        combineLatest([
            this.#eventPromotionsService.promotionPacks.get$().pipe(filter(Boolean)),
            this.$packsForm().valueChanges // only used as a trigger
        ])
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(([promPacks]) => {
                FormControlHandler.checkAndRefreshDirtyState(
                    this.$packsForm().get('allow_entity_packs'),
                    promPacks.allow_entity_packs
                );
                FormControlHandler.checkAndRefreshDirtyState(
                    this.$packsForm().get('packs'),
                    promPacks.packs || []
                );
            });

        this.#packsSrv.getAllPacksData$()
            .pipe(
                filter(Boolean),
                map(packs => packs?.map(pack => ({
                    id: pack.id,
                    name: pack.name
                })))
            ).subscribe(packs => {
                if (this.#isSelectAllChecked) {
                    this.$packsForm().get('packs').patchValue(unionWith(this.$packsForm().get('packs').value, packs));
                } else {
                    this.$packsForm().get('packs').patchValue(differenceWith(this.$packsForm().get('packs').value, packs));
                }
                this.$packsForm().markAsTouched();
                this.$packsForm().markAsDirty();
            });
    }

    get selectedPacks(): number {
        return this.$packsForm().get('packs')?.value?.length || 0;
    }

    /**
     * selects all filtered packs
     */
    selectAll(change?: MatCheckboxChange): void {
        this.#isSelectAllChecked = change?.checked;
        this.#packsSrv.loadAllPacks({
            ...this.#filters,
            limit: undefined
        });
    }

    filterChangeHandler(filters: Partial<GetPacksRequest>): void {
        this.#filters = {
            ...this.#filters,
            ...filters
        };
        this.loadPacks();
    }

    loadPacks = (): void => {
        this.#packsSrv.packsList.load(this.#filters);

        // change to non-selected only view if table content loaded
        this.#packsSrv.packsList.getData$().pipe(
            withLatestFrom(this.selectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnly]) => {
            if (isSelectedOnly) {
                this.showSelectedOnlyClick.emit();
            }
        });
    };
}
