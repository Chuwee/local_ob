import { Metadata } from '@OneboxTM/utils-state';
import { EntityZoneTemplate } from '@admin-clients/cpanel/organizations/entities/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService,
    ObMatDialogConfig, SearchablePaginatedSelectionComponent, SearchablePaginatedSelectionModule,
    TableMoreActionsButtonDirective
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { EllipsifyDirective } from '@admin-clients/shared/utility/directives';
import {
    ElementsInfoFilterRequest, VenueTemplateElementInfo, VenueTemplateElementInfoAction
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { BooleanInput, coerceBooleanProperty } from '@angular/cdk/coercion';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import {
    ChangeDetectionStrategy, Component, computed, DestroyRef, inject, Input, input, OnDestroy, Signal, viewChild
} from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule, NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { TransitionCheckState } from '@angular/material/checkbox';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import {
    BehaviorSubject, combineLatest, filter, map, Observable, switchMap, take
} from 'rxjs';
import { audit, shareReplay, startWith, tap, withLatestFrom } from 'rxjs/operators';
import { EditElementInfoDialogComponent } from './edit-element-info/edit-element-info-dialog.component';
import { ElementsInfoFilterComponent } from './filter/elements-info-filter.component';
import { ELEMENTS_INFO_SERVICE } from './models/elements-info-service.model';

const DEFAULT_FILTER: PageableFilter = { limit: 20 };
@Component({
    selector: 'app-elements-info',
    templateUrl: './elements-info.component.html',
    styleUrls: ['./elements-info.component.scss'],
    imports: [
        TranslatePipe, SearchablePaginatedSelectionModule, MaterialModule, AsyncPipe, EllipsifyDirective,
        MatDialogModule, FormsModule, ReactiveFormsModule, ElementsInfoFilterComponent, MatTooltipModule, TableMoreActionsButtonDirective
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ElementsInfoComponent implements OnDestroy {
    readonly #elementsInfoService = inject(ELEMENTS_INFO_SERVICE);
    readonly #matDialog = inject(MatDialog);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #destroyRef = inject(DestroyRef);

    // Inputs and configuration
    readonly $entityId = input.required<number>({ alias: 'entityId' });
    readonly $languages = input.required<string[]>({ alias: 'languages' });
    readonly $interactiveVenue = input.required<boolean>({ alias: 'interactiveVenue' });

    readonly pageSize = DEFAULT_FILTER.limit;
    readonly checkState = TransitionCheckState;

    readonly #columns = ['active', 'name', 'type', 'templates_zones', 'content', 'actions'];
    readonly #statusColumns = ['active', 'name', 'type', 'templates_zones', 'content', 'status', 'actions'];

    #filters: ElementsInfoFilterRequest = DEFAULT_FILTER;
    #id: number;
    #status: boolean;
    #delete: boolean;

    readonly selectedInfoViewsControl = inject(NonNullableFormBuilder).control<VenueTemplateElementInfo[]>([]);
    columns: string[] = this.#columns;

    @Input() set id(id: number) {
        this.#id = id;
        this.selectedInfoViewsControl.patchValue([]);
        this.allSelectedState$.next(false);
        if (this.#id) {
            this.loadElementsInfo({ offset: 0 });
        }
    }

    @Input() set refreshChanges(refreshChanges: unknown) {
        this.loadElementsInfo({ offset: 0 });
    }

    @Input() set status(value: BooleanInput) {
        this.#status = coerceBooleanProperty(value);
        this.columns = this.#status ? this.#statusColumns : this.#columns;
    }

    @Input() set delete(value: BooleanInput) {
        this.#delete = coerceBooleanProperty(value);
    }

    get id(): number { return this.#id; }
    get status(): boolean { return this.#status; }
    get delete(): boolean { return this.#delete; }

    // Private data sources
    readonly #allInfoViews$ = this.#initAllInfoViews$();
    readonly #selectedInfoViews$ = this.#initSelectedInfoViews$();
    readonly #availableApiInfoViewsCount$ = this.#initApiMetadataInfoViewsCount$();
    readonly #actionSelectionData$ = this.#initActionSelectionData$();

    // States
    readonly showSelectedOnlyState$ = new BehaviorSubject(false);
    readonly allSelectedState$ = new BehaviorSubject(false);
    readonly reqInProgress$ = this.#elementsInfoService.venueTplsElementInfo.inProgress$();
    readonly $checkboxAllSelectedState = this.#$initCheckState();
    readonly isHandsetOrTablet$ = this.#breakpointObserver.observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    // Public data sources
    readonly elementsInfoList$ = this.#initElementsInfoList$();
    readonly metadata$ = this.#initMetadata$();

    // Signal counters
    readonly $selectedInfoViewsCount = toSignal(this.#selectedInfoViews$.pipe(map(selected => selected.length)), { initialValue: 0 });
    readonly $availableInfoViewsCount = toSignal(this.metadata$.pipe(map(meta => meta.total)), { initialValue: 0 });
    readonly $initialApiInfoViewsCount = toSignal(this.#availableApiInfoViewsCount$.pipe(take(1)), { initialValue: 0 });

    // View children
    readonly listWrapperComponent = viewChild(SearchablePaginatedSelectionComponent);
    readonly filterButtonComponent = viewChild(ElementsInfoFilterComponent);

    constructor() {
        this.#initAllSelectedSubscriptions();
    }

    ngOnDestroy(): void {
        this.#elementsInfoService.venueTplsElementInfo.clear();
        this.#elementsInfoService.venueTplElementInfo.clear();
        this.allSelectedState$.complete();
        this.showSelectedOnlyState$.complete();
        this.selectedInfoViewsControl.reset();
    }

    toggleShowSelectedInfoViews(): void {
        const showSelectedOnly = !this.showSelectedOnlyState$.getValue();

        this.showSelectedOnlyState$.next(showSelectedOnly);

        if (showSelectedOnly) {
            // List wrapper component reset filters only resets input search, we need to reset our filters too,
            this.#filters = DEFAULT_FILTER;
            this.listWrapperComponent()?.resetFilters();
            // filter button component reset and emit to load
            this.filterButtonComponent()?.clear();
        }
    }

    changeAllSelectedState(state: boolean): void {
        this.allSelectedState$.next(state);
    }

    loadElementsInfo(filters?: Partial<ElementsInfoFilterRequest>): void {
        if (filters) {
            this.#filters = { ...this.#filters, ...filters };
        }

        this.#elementsInfoService.venueTplsElementInfo.load(this.#id, this.#filters);
    }

    loadAllElementsInfo(): void {
        this.#elementsInfoService.venueTplsElementInfo.loadAll(this.#id, this.#filters);

        this.elementsInfoList$.pipe(
            audit(() => this.reqInProgress$.pipe(filter(inProgress => !inProgress))),
            take(1),
            takeUntilDestroyed(this.#destroyRef)
        ).subscribe(() => this.loadElementsInfo());
    }

    recoverInheritanceElementsInfo(elementInfo: VenueTemplateElementInfo): void {
        this.openRecoverInheritanceElementInfoDialog(elementInfo);
    }

    editInfoView(elementsInfo: VenueTemplateElementInfo): void {
        this.#openEditViewDialog([elementsInfo]);
    }

    editMultipleInfoView(): void {
        this.#actionSelectionData$.pipe(take(1)).subscribe(data =>
            this.#openEditViewDialog(
                data.allSelected ? [] : data.selected,
                data.allSelected ? data.total : data.selected.length,
                data.allSelected
            )
        );
    }

    deleteMultipleInfoView(): void {
        this.#actionSelectionData$.pipe(take(1)).subscribe(data =>
            this.#openDeleteElementsInfoDialog(
                data.allSelected ? [] : data.selected.map(e => e.element?.id).filter((e): e is string => !!e),
                data.allSelected ? data.total : data.selected.length,
                data.allSelected
            )
        );
    }

    changeStatus(event: { checked: boolean }, elementInfo: VenueTemplateElementInfo): void {
        this.#elementsInfoService.venueTplElementInfo.changeStatus(
            this.#id,
            elementInfo.id,
            elementInfo.type,
            event.checked ? 'ENABLED' : 'DISABLED'
        ).subscribe(() => {
            this.#ephemeralMessageService.showSaveSuccess();
            this.selectedInfoViewsControl.patchValue([]);
            this.loadElementsInfo();
        });
    }

    changeMultipleStatus(enabled = false): void {
        this.#actionSelectionData$.pipe(take(1)).subscribe(data =>
            this.#elementsInfoService.venueTplElementInfo
                .updateMultiple(this.#id, data.selected, { status: enabled ? 'ENABLED' : 'DISABLED' }, data.allSelected, this.#filters)
                .subscribe(() => {
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.selectedInfoViewsControl.patchValue([]);
                    this.loadElementsInfo();
                })
        );
    }

    getTemplateZonesValue(templateZones: EntityZoneTemplate[]): string {
        const concatTemplates = templateZones?.map(template => template.name).join(', ');
        return concatTemplates;
    }

    /**
     * Sets up subscriptions that keep the "select all" state consistent:
     * - Loads all items if "select all" is activated but data is incomplete.
     * - Syncs checkbox and selection control when backend operations finish.
     */
    #initAllSelectedSubscriptions(): void {
        // Load missing elements if "select all" is active but not all are fetched
        this.allSelectedState$
            .pipe(
                filter(allSelected => allSelected),
                switchMap(() => combineLatest([this.#allInfoViews$, this.#availableApiInfoViewsCount$])
                    .pipe(take(1), filter(([allViews, total]) => allViews.length !== total))
                ),
                takeUntilDestroyed()
            )
            .subscribe(() => this.loadAllElementsInfo());

        // Keep selection control synced with checkbox state, avoiding race conditions
        this.allSelectedState$
            .pipe(
                audit(() => this.reqInProgress$.pipe(filter(inProgress => !inProgress))),
                withLatestFrom(this.#allInfoViews$),
                takeUntilDestroyed()
            )
            .subscribe(([allSelected, allViews]) => this.selectedInfoViewsControl.patchValue(allSelected ? allViews : []));
    }

    #openEditViewDialog(elementsInfos: VenueTemplateElementInfo[], viewsSelected = 1, allSelected = false): void {
        let openEditViewDialog$: Observable<boolean>;

        const data = {
            id: this.id,
            status: this.status,
            currentAction: VenueTemplateElementInfoAction.createSingle,
            elementsInfos,
            elementsInfoService: this.#elementsInfoService,
            filters: this.#filters,
            interactiveVenue: this.$interactiveVenue(),
            languages: this.$languages(),
            isFromParent: false,
            entityId: this.$entityId()
        };

        if (viewsSelected > 1 || allSelected) {
            data.currentAction = elementsInfos?.length ? VenueTemplateElementInfoAction.editMulti : VenueTemplateElementInfoAction.editAll;
            openEditViewDialog$ = this.#messageDialogService.showWarn({
                size: DialogSize.MEDIUM,
                title: 'VENUE_TPLS.ELEMENTS_INFO.MULTI_SELECTION_WARNING.TITLE',
                message: 'VENUE_TPLS.ELEMENTS_INFO.MULTI_SELECTION_WARNING.MESSAGE',
                actionLabel: 'VENUE_TPLS.ELEMENTS_INFO.MULTI_SELECTION_WARNING.ACTION'
            }).pipe(
                filter(Boolean),
                switchMap(
                    () => this.#matDialog.open(EditElementInfoDialogComponent, new ObMatDialogConfig(
                        data, undefined, DialogSize.LATERAL
                    )).beforeClosed()
                )
            );
        } else {
            data.currentAction = elementsInfos[0]?.element?.id
                ? VenueTemplateElementInfoAction.editSingle
                : VenueTemplateElementInfoAction.createSingle;
            if (data.currentAction === VenueTemplateElementInfoAction.createSingle
                && elementsInfos[0].element && !elementsInfos[0].element.id) {
                data.isFromParent = true;
            }
            openEditViewDialog$ = this.#matDialog.open(EditElementInfoDialogComponent, new ObMatDialogConfig(
                data, undefined, DialogSize.LATERAL
            )).beforeClosed();
        }

        openEditViewDialog$.pipe(filter(Boolean)).subscribe(() => {
            this.selectedInfoViewsControl.patchValue([]);
            this.loadElementsInfo();
        });
    }

    #openDeleteElementsInfoDialog(elementInfos: string[], elementsSelected = 1, allSelected = false): void {
        const messageMultiple = `${elementsSelected > 1 ? '_MULTIPLE' : ''}${elementInfos.length === 0 ? '_ALL' : ''}`;
        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_ELEMENTS_INFO',
            message: `VENUE_TPLS.ELEMENTS_INFO.DELETE_WARNING${messageMultiple}`,
            messageParams: { number: elementsSelected },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this.#elementsInfoService.venueTplElementInfo.delete(this.id, elementInfos, allSelected, this.#filters))
            )
            .subscribe(() => {
                this.#ephemeralMessageService.showSuccess({
                    msgKey: `VENUE_TPLS.ELEMENTS_INFO.DELETE_ELEMENTS_INFO_SUCCESS${messageMultiple}`,
                    msgParams: { number: elementsSelected }
                });

                this.selectedInfoViewsControl.patchValue([]);
                this.loadElementsInfo();
            });
    }

    private openRecoverInheritanceElementInfoDialog(elementInfo: VenueTemplateElementInfo): void {
        this.#messageDialogService.showWarn({
            size: DialogSize.MEDIUM,
            title: 'TITLES.RECOVER_ELEMENT_INFO',
            message: `VENUE_TPLS.ELEMENTS_INFO.RECOVER_WARNING`,
            actionLabel: 'FORMS.ACTIONS.RECOVER',
            showCancelButton: true
        })
            .pipe(
                filter(Boolean),
                switchMap(() =>
                    this.#elementsInfoService.venueTplElementInfo.recoverInheritance(this.#id, elementInfo.id, elementInfo.type)
                )
            )
            .subscribe(() => {
                this.#ephemeralMessageService.showSuccess({
                    msgKey: `VENUE_TPLS.ELEMENTS_INFO.RECOVER_ELEMENTS_INFO_SUCCESS`
                });

                this.selectedInfoViewsControl.patchValue([]);
                this.loadElementsInfo();
            });
    }

    // Private initializers
    #initAllInfoViews$(): Observable<VenueTemplateElementInfo[]> {
        return this.#elementsInfoService.venueTplsElementInfo.getData$()
            .pipe(
                filter(Boolean),
                tap(viewsInfo => {
                    // Update selected values
                    this.selectedInfoViewsControl.value.forEach((selectedView, index) => {
                        const updatedView = viewsInfo.find(viewsInfo => selectedView.id === viewsInfo.id);
                        if (updatedView) {
                            this.selectedInfoViewsControl.value[index] = updatedView;
                        }
                    });
                }),
                shareReplay({ refCount: true, bufferSize: 1 })
            );
    }

    #initSelectedInfoViews$(): Observable<VenueTemplateElementInfo[]> {
        return this.selectedInfoViewsControl.valueChanges.pipe(
            startWith(this.selectedInfoViewsControl.value),
            tap(selected => {
                if (selected.length === 0) this.showSelectedOnlyState$.next(false);
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );
    }

    #initApiMetadataInfoViewsCount$(): Observable<number> {
        return this.#elementsInfoService.venueTplsElementInfo.getMetadata$().pipe(
            filter(Boolean),
            map(metadata => metadata.total)
        );
    }

    /** Combines available and selected views depending on the "show selected only" flag. */
    #initElementsInfoList$(): Observable<VenueTemplateElementInfo[]> {
        return combineLatest([this.#allInfoViews$, this.showSelectedOnlyState$]).pipe(
            withLatestFrom(this.#selectedInfoViews$),
            map(([[allViews, selectedOnly], selected]) => selectedOnly ? selected : allViews)
        );
    }

    /**
     * Returns metadata adjusted to the current view mode.
     * Uses API metadata normally, but when "show selected only" is active,
     * builds synthetic metadata limited to the selected items.
     */
    #initMetadata$(): Observable<Metadata> {
        return this.showSelectedOnlyState$.pipe(
            switchMap(isActive =>
                isActive
                    ? this.#selectedInfoViews$.pipe(take(1), map(list => new Metadata({ total: list?.length ?? 0, limit: 999, offset: 0 })))
                    : this.#elementsInfoService.venueTplsElementInfo.getMetadata$().pipe(filter(Boolean))
            ),
            shareReplay({ refCount: true, bufferSize: 1 })
        );
    }

    #initActionSelectionData$(): Observable<{ selected: VenueTemplateElementInfo[]; total: number; allSelected: boolean }> {
        return combineLatest([this.#selectedInfoViews$, this.#availableApiInfoViewsCount$]).pipe(
            map(([selected, total]) => ({
                selected,
                total,
                allSelected: selected.length === total
            })),
            shareReplay({ refCount: true, bufferSize: 1 })
        );
    }

    #$initCheckState(): Signal<TransitionCheckState> {
        return computed(() => {
            const selected = this.$selectedInfoViewsCount();
            const total = this.$availableInfoViewsCount();
            if (selected === total) { return TransitionCheckState.Checked; }
            if (selected > 0 && selected < total) { return TransitionCheckState.Indeterminate; }
            return TransitionCheckState.Unchecked;
        });
    }
}
