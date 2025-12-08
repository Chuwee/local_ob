import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { Metadata } from '@OneboxTM/utils-state';
import { GetPackSubItemsRequest, PacksService, PackSubItem, PutPackSubItems } from '@admin-clients/cpanel/packs/my-packs/data-access';
import {
    EventSessionsService, GetSessionsRequest,
    Session,
    SessionStatus, eventSessionsProviders
} from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import {
    DialogSize, SearchablePaginatedSelectionLoadEvent, SearchablePaginatedSelectionModule
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { atLeastOneRequiredInArray, booleanOrMerge, maxSelectedItems } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, DestroyRef, ElementRef,
    EventEmitter, OnDestroy, OnInit, inject
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { filter, map, Observable, shareReplay, startWith, switchMap, take, tap, withLatestFrom } from 'rxjs';
import { SUBITEMS_LIMIT } from '../pack-elements.component';
import { EditPackSubItemsDialogFilterComponent } from './filter/edit-pack-subitems-dialog-filter.component';

const PAGE_SIZE = 10;
export interface EditPackSubItemsDialogData {
    packId: number;
    eventId: number;
    itemId: number;
    venueTplId: number;
}
@Component({
    selector: 'app-edit-pack-subitems-dialog',
    imports: [
        TranslatePipe, ReactiveFormsModule, AsyncPipe, SearchablePaginatedSelectionModule,
        MatIcon, MatRadioButton, MatRadioGroup, MatError, MatProgressSpinner, MatIconButton, MatButton,
        MatDialogTitle, MatDialogContent, MatDialogActions, DateTimePipe, MatTooltip, EllipsifyDirective,
        EditPackSubItemsDialogFilterComponent
    ],
    templateUrl: './edit-pack-subitems-dialog.component.html',
    styleUrls: ['./edit-pack-subitems-dialog.component.scss'],
    providers: [eventSessionsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditPackSubItemsDialogComponent implements OnInit, OnDestroy {

    readonly #data = inject<EditPackSubItemsDialogData>(MAT_DIALOG_DATA);
    readonly #eventSessionsSrv = inject(EventSessionsService);
    readonly #packsSrv = inject(PacksService);
    readonly #fb = inject(FormBuilder);
    readonly #dialogRef = inject(MatDialogRef<EditPackSubItemsDialogComponent>);
    readonly #elemRef = inject(ElementRef);
    readonly #onDestroy = inject(DestroyRef);
    readonly showSelectedOnlyClick = new EventEmitter<boolean>();

    readonly dateTimeFormats = DateTimeFormats;
    #sessionsFilter: GetSessionsRequest = {
        limit: PAGE_SIZE,
        offset: 0,
        sort: 'name:asc',
        status: [SessionStatus.preview, SessionStatus.ready, SessionStatus.scheduled]
    };

    #subItemsFilter: GetPackSubItemsRequest = { limit: SUBITEMS_LIMIT };
    readonly showSelectedOnly$ = this.showSelectedOnlyClick.pipe(
        startWith(false),
        shareReplay(1)
    );

    readonly sessionsData$!: Observable<Session[]>;

    readonly sessionsMetadata$!: Observable<Metadata>;

    readonly totalSessions$ = this.#eventSessionsSrv.sessionList.getMetadata$()
        .pipe(map(metadata => metadata?.total || 0), takeUntilDestroyed(this.#onDestroy));

    readonly subItemsData$ = this.#packsSrv.packSubItems.getData$().pipe(
        filter(Boolean),
        takeUntilDestroyed(this.#onDestroy),
        tap(subItems => this.form.controls.selectAll.setValue(subItems.length === 0))
    );

    readonly subItemsMetadata$ = this.#packsSrv.packSubItems.getMetadata$().pipe(
        filter(Boolean),
        takeUntilDestroyed(this.#onDestroy)
    );

    readonly $totalSubItems = toSignal(this.subItemsMetadata$.pipe(map(metadata => metadata?.total || 0)), { initialValue: 0 });

    readonly isInProgress$ = booleanOrMerge([
        this.#eventSessionsSrv.sessionList.inProgress$(),
        this.#packsSrv.packSubItems.loading$()
    ]);

    readonly form = this.#fb.group({
        subItems: this.#fb.control<PackSubItem[]>([], [atLeastOneRequiredInArray(), maxSelectedItems(SUBITEMS_LIMIT)]),
        selectAll: [false, [Validators.required]]
    });

    get subItemsCtrl(): FormControl<PackSubItem[]> {
        return this.form.get('subItems') as FormControl<PackSubItem[]>;
    }

    get selectAllCtrl(): FormControl<boolean> {
        return this.form.get('selectAll') as FormControl<boolean>;
    }

    readonly pageSize = PAGE_SIZE;
    readonly subItemsLimit = SUBITEMS_LIMIT;

    constructor() {
        this.#loadAndGetSubitems();

        const allSessions$ = this.#eventSessionsSrv.sessionList.getData$().pipe(
            filter(Boolean),
            map(sessions => sessions.map<PackSubItem>(session => ({ id: session.id, name: session.name, start_date: session.start_date }))),
            takeUntilDestroyed(this.#onDestroy)
        );

        const selectedSessions$ = this.form.controls.subItems.valueChanges.pipe(takeUntilDestroyed(this.#onDestroy), shareReplay(1));
        selectedSessions$.subscribe(selected => {
            if (!selected?.length) {
                this.showSelectedOnlyClick.next(false);
            }
        });

        this.sessionsData$ = this.showSelectedOnly$.pipe(
            switchMap(isSelectedOnly => isSelectedOnly ? selectedSessions$ : allSessions$),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );

        this.sessionsMetadata$ = this.showSelectedOnly$.pipe(
            switchMap(isSelectedOnly =>
                isSelectedOnly ? selectedSessions$.pipe(
                    map(list => new Metadata({ total: list?.length || 0, limit: 999, offset: 0 }))
                ) : this.#eventSessionsSrv.sessionList.getMetadata$()
            ),
            takeUntilDestroyed(this.#onDestroy),
            shareReplay(1)
        );
    }

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.EXTRA_LARGE);
    }

    ngOnDestroy(): void {
        this.#eventSessionsSrv.sessionList.clear();
    }

    shouldDisableSubItem: (d: Session) => boolean = (d: Session) => {
        const item: PackSubItem = { id: d.id, name: d.name, start_date: d.start_date };
        const isItemAlreadySelected = this.form.controls.subItems.value.some(subItem => subItem.id === item.id);
        return this.form.controls.subItems.value.length >= SUBITEMS_LIMIT && !isItemAlreadySelected;
    };

    close(saved = false): void {
        this.#dialogRef.close(saved);
    }

    saveSubItems(): void {
        if (!!this.form.controls.selectAll.value) {
            this.#save({ sub_item_ids: [] });
        } else if (this.form.valid) {
            const newSubItemIds = this.form.value.subItems.map(subItem => subItem.id);
            this.#save({ sub_item_ids: newSubItemIds });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    clickShowSelected(): void {
        this.showSelectedOnly$.pipe(take(1)).subscribe((isSelected => this.showSelectedOnlyClick.emit(!isSelected)));
    }

    filterChangeHandler(filters: Partial<GetSessionsRequest>): void {
        this.#sessionsFilter = {
            ...this.#sessionsFilter,
            ...filters
        };
        this.loadSessionsList();
    }

    reloadSessionsList({ limit, q, offset }: SearchablePaginatedSelectionLoadEvent): void {
        this.#sessionsFilter = { ...this.#sessionsFilter, limit, offset, q };
        this.loadSessionsList();
    }

    loadSessionsList(): void {
        this.#eventSessionsSrv.sessionList.load(this.#data.eventId, { ...this.#sessionsFilter, venueTplId: this.#data.venueTplId });

        this.sessionsData$.pipe(
            withLatestFrom(this.showSelectedOnly$),
            take(1)
        ).subscribe(([, isSelectedOnly]) => this.showSelectedOnlyClick.emit(isSelectedOnly));
    }

    #save(reqBody: PutPackSubItems): void {
        this.#packsSrv.packSubItems.update(this.#data.packId, this.#data.itemId, reqBody).subscribe(() => {
            this.close(true);
        });
    }

    #loadAndGetSubitems(): void {
        this.#packsSrv.packSubItems.load(this.#data.packId, this.#data.itemId, this.#subItemsFilter);

        this.#packsSrv.packSubItems.getData$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(subItems => {
            this.form.controls.selectAll.setValue(subItems.length === 0);
            this.form.controls.subItems.patchValue(subItems);
            this.form.markAsPristine();
            this.form.markAsUntouched();
        });
    }

}
