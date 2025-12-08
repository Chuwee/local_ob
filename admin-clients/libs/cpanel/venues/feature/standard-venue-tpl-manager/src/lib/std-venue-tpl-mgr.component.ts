import {
    ContextNotificationComponent, DialogSize, EphemeralMessageService, graphicViewIcon,
    IconManagerService, MessageDialogService, treeViewIcon
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName, ObFile } from '@admin-clients/shared/data-access/models';
import { SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    StdVenueTplService, StdVenueTplsState, VENUE_MAP_SERVICE, VenueMapService
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import {
    VenueTemplate, VenueTemplatesService, VenueTemplateStatus, VenueTemplateType
} from '@admin-clients/shared/venues/data-access/venue-tpls';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import {
    booleanAttribute, ChangeDetectionStrategy, Component, computed, DestroyRef, effect, inject, input, OnInit, output, signal, viewChild
} from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatButtonToggleChange } from '@angular/material/button-toggle';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, type Observable, of } from 'rxjs';
import { filter, finalize, map, pairwise, shareReplay, startWith, switchMap, take, takeWhile, tap } from 'rxjs/operators';
import { VenueTemplateViewComponent } from './graphic-view/venue-template-view.component';
import { VenueTemplateLabelMgrComponent } from './label-mgr/venue-template-label-mgr.component';
import { VenueTemplateAction, VenueTemplateActionType } from './models/venue-template-action.model';
import { VenueTemplateEditorState } from './models/venue-template-editor-state.enum';
import { VenueTemplateEditorType } from './models/venue-template-editor-type.model';
import { VenueTemplateEditorView } from './models/venue-template-editor-view.enum';
import { VenueTemplateOriginOrderItem } from './models/venue-template-origin-order-item';
import { VenueTemplateRelocationButtonBarComponent } from './relocation/relocation-bottom-bar/venue-tpl-relocation-button-bar.component';
import { VenueTemplateRelocationMgrComponent } from './relocation/relocation-mgr/venue-tpl-relocation-mgr.component';
import { StandardVenueTemplateBaseService } from './services/standard-venue-template-base.service';
import { StandardVenueTemplateChangesService } from './services/standard-venue-template-changes.service';
import { StandardVenueTemplateFilterService } from './services/standard-venue-template-filter.service';
import { StandardVenueTemplatePartialChangesService } from './services/standard-venue-template-partial-changes.service';
import { StandardVenueTemplateSaveService } from './services/standard-venue-template-save.service';
import { StandardVenueTemplateSelectionService } from './services/standard-venue-template-selection.service';
import { StandardVenueTemplateState } from './state/standard-venue-template.state';
import { StdVenueTplMgrSummaryComponent } from './summary/std-venue-tpl-mgr-summary.component';
import { TemplateImageComponent } from './template-image/template-image.component';
import { VenueTemplateTreeComponent } from './tree-view/venue-template-tree.component';

type MgrSession = {
    id?: number;
    name?: string;
    venue_template?: VenueTemplate;
    event?: Partial<IdName>;
    updating_capacity?: boolean;
};

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ContextNotificationComponent,
        VenueTemplateLabelMgrComponent,
        VenueTemplateRelocationMgrComponent,
        VenueTemplateRelocationButtonBarComponent,
        TemplateImageComponent,
        VenueTemplateTreeComponent,
        VenueTemplateViewComponent,
        SharedUtilityDirectivesModule,
        StdVenueTplMgrSummaryComponent
    ],
    selector: 'app-std-venue-tpl-mgr',
    templateUrl: './std-venue-tpl-mgr.component.html',
    styleUrls: ['./std-venue-tpl-mgr.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        StdVenueTplsState,
        StdVenueTplService,
        StandardVenueTemplateState,
        StandardVenueTemplateBaseService,
        StandardVenueTemplateFilterService,
        StandardVenueTemplateSelectionService,
        StandardVenueTemplateChangesService,
        StandardVenueTemplatePartialChangesService,
        StandardVenueTemplateSaveService
    ]
})
export class StdVenueTplMgrComponent implements OnInit {
    readonly #destroyRef = inject(DestroyRef);
    readonly #breakpointObserver = inject(BreakpointObserver);
    readonly #venueTemplatesSrv = inject(VenueTemplatesService);
    readonly #stdVenueTplSrv = inject(StdVenueTplService);
    readonly #standardVenueTemplateSrv = inject(StandardVenueTemplateBaseService);
    readonly #standardVenueTemplateChangesSrv = inject(StandardVenueTemplateChangesService);
    readonly #standardVenueTemplateSaveSrv = inject(StandardVenueTemplateSaveService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #iconManagerSrv = inject(IconManagerService);
    readonly #venueMapSrv: VenueMapService = inject(VENUE_MAP_SERVICE, { optional: true }) || this.#stdVenueTplSrv;
    readonly #msgDialogService = inject(MessageDialogService);

    readonly #venueTemplate = computed(() => this.$session()?.venue_template || this.$inputVenueTemplate());

    private readonly _venueTemplateViewComponent = viewChild(VenueTemplateViewComponent);
    private readonly _venueTplRelocationMgrComp = viewChild(VenueTemplateRelocationMgrComponent);

    readonly venueTemplateEditorType = VenueTemplateEditorType;
    readonly venueTemplateEditorView = VenueTemplateEditorView;
    readonly normalVenueTemplateType = VenueTemplateType.normal;

    readonly $session = input(null as MgrSession, { alias: 'session' });
    readonly $inputVenueTemplate = input(null as VenueTemplate, { alias: 'venueTemplate' });
    readonly editorEnabled = input(false, { transform: booleanAttribute });
    readonly $editorType = input.required<VenueTemplateEditorType>({ alias: 'editorType' });
    readonly $isInUse = input(false, { alias: 'isInUse' });
    readonly $increaseDisabled = input(false, { alias: 'increaseDisabled' });
    readonly $canRelocate = input(false, { alias: 'canRelocate' });
    readonly $linkedSessions = input<{ id: number; name: string; color?: string }[]>(null, { alias: 'linkedSessions' });
    readonly $unrestrictedPack = input(false, { alias: 'unrestrictedPack' });
    readonly $relocateSeatsInfo = input<VenueTemplateOriginOrderItem[]>([], { alias: 'relocateSeatsInfo' });

    readonly gotoEditor = output();
    readonly userAction = output<VenueTemplateAction>();

    readonly $hasVisibleSeats = computed(() => this._venueTemplateViewComponent()?.$hasVisibleSeats());
    readonly $labelMgrWidth = toSignal(
        this.#breakpointObserver.observe([Breakpoints.XSmall, Breakpoints.Small, Breakpoints.Medium, Breakpoints.Large, Breakpoints.XLarge])
            .pipe(map(state =>
                (
                    state.breakpoints[Breakpoints.Medium] ? 220 :
                        state.breakpoints[Breakpoints.Large] ? 280 :
                            state.breakpoints[Breakpoints.XLarge] ? 340 :
                                180
                ) + 'px'
            ))
    );

    readonly $isNumbered = toSignal(this.#standardVenueTemplateSrv.getVenueItems$().pipe(map(items => items && !!items.seats?.size)));

    readonly $showContextError = signal(false);
    readonly isDirty$ = this.#standardVenueTemplateSaveSrv.isDirty$();
    readonly venueTemplate$ = this.#venueTemplatesSrv.venueTpl.get$();
    readonly currentView$ = this.#standardVenueTemplateSrv.getCurrentView$();
    readonly isInCapIncr$ = this.#standardVenueTemplateSrv.getCurrentState$()
        .pipe(
            map(state => state === VenueTemplateEditorState.capacityIncrease),
            shareReplay(1)
        );

    readonly isRelocating$ = this.#standardVenueTemplateSrv.getCurrentState$()
        .pipe(
            map(state => state === VenueTemplateEditorState.relocation),
            takeWhile(() => this.$canRelocate())
        );

    readonly relocationSeats$ = this.isRelocating$
        .pipe(
            filter(Boolean),
            switchMap(() => this._venueTplRelocationMgrComp()?.getSelectedDestinationSeats() ?? of({}))
        );

    readonly hideLabelMgr$ = booleanOrMerge([
        toObservable(this.$showContextError),
        this.isRelocating$
    ]);

    readonly loading$ = booleanOrMerge([
        this.#venueTemplatesSrv.venueTpl.inProgress$(),
        this.#venueMapSrv.isVenueMapLoading$(),
        this.#venueTemplatesSrv.isVenueTemplateBlockingReasonsLoading$(),
        this.#venueTemplatesSrv.isVenueTemplatePriceTypesLoading$(),
        this.#venueTemplatesSrv.isVenueTemplateQuotasLoading$(),
        this.#venueTemplatesSrv.isVenueTemplateGatesLoading$(),
        this.#venueTemplatesSrv.venueTplCustomTagGroups.loading$(),
        this.#venueTemplatesSrv.firstCustomTagGroupLabels.loading$(),
        this.#venueTemplatesSrv.secondCustomTagGroupLabels.loading$(),
        this.#venueTemplatesSrv.isVenueTemplateSaving$(),
        this.#venueMapSrv.isVenueMapSaving$()
    ]);

    constructor() {
        this.#iconManagerSrv.addIconDefinition(graphicViewIcon, treeViewIcon);
        effect(() => {
            if (this.#venueTemplate()) {
                this.startLoadProcess();
            }
        });
    }

    ngOnInit(): void {
        this.#venueTemplatesSrv.venueTpl.get$()
            .pipe(
                startWith(null as VenueTemplate),
                pairwise(),
                takeUntilDestroyed(this.#destroyRef)
            )
            .subscribe(([oldValue, newValue]) => {
                this.$showContextError.set(newValue && newValue.status === VenueTemplateStatus.error);
                const clearVenueTemplateData =
                    !!newValue &&
                    (!oldValue ||
                        newValue.id !== oldValue.id ||
                        oldValue.status !== VenueTemplateStatus.active);
                const loadVenueTemplateData = newValue && newValue.status === VenueTemplateStatus.active;
                if (!oldValue && newValue) {
                    this.#standardVenueTemplateSrv.setCurrentView(
                        (newValue.graphic && VenueTemplateEditorView.graphic) || VenueTemplateEditorView.tree
                    );
                }
                if (clearVenueTemplateData) {
                    this.#standardVenueTemplateSrv.clearVenueMapItems();
                    this.#venueTemplatesSrv.clearVenueTemplateData();
                    this.#stdVenueTplSrv.clearVenueTemplateData();
                }
                if (loadVenueTemplateData) {
                    this.loadVenueMap(newValue.id);
                }
            });
        this.#standardVenueTemplateSrv.action
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(action => this.userAction.emit(action));
    }

    fitGraphicViewIfRequired(): void {
        this._venueTemplateViewComponent()?.fitGraphicViewIfRequired();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        return combineLatest([this.saveVenueMap(), this.saveImage()]).pipe(
            take(1),
            tap(resultOk => {
                if (resultOk && !this.#venueMapSrv.isCapacityUpdateAsync) {
                    this.#ephemeralMessageService.showSaveSuccess();
                }
            })
        );
    }

    cancelChanges(forceReload = false, ignoreDirty = false): void {
        this.isDirty$.pipe(take(1)).subscribe(isDirty => this.resetView((isDirty && !ignoreDirty) || forceReload));
    }

    changeView(toggleChange: MatButtonToggleChange): void {
        this.#standardVenueTemplateSrv.setCurrentView(toggleChange.value);
    }

    enterCapacityIncreaseState(): void {
        this.#standardVenueTemplateSrv.setCurrentState$(VenueTemplateEditorState.capacityIncrease);
        this.#standardVenueTemplateSrv.setCurrentView(VenueTemplateEditorView.tree);
    }

    exportCapacity(): void {
        this.userAction.emit({ type: VenueTemplateActionType.exportCapacity });
    }

    onStartRelocation(): void {
        this.resetView(true);
        this.#standardVenueTemplateSrv.setCurrentState$(VenueTemplateEditorState.relocation);
        this.#msgDialogService.showInfo({
            size: DialogSize.MEDIUM,
            title: 'VENUE_TPLS.TITLES.KILL_SEATS',
            message: 'VENUE_TPLS.FORMS.INFOS.KILL_SEATS'
        });
    }

    private loadVenueMap(tplId = this.#venueTemplate()?.id): void {
        this.#venueMapSrv.loadVenueMap({
            tplId,
            eventId: this.$session()?.event.id,
            sessionId: this.$session()?.id,
            updatingCapacity: this.$session()?.updating_capacity
        });
    }

    private startLoadProcess(): void {
        this.#standardVenueTemplateSrv.setCurrentState$(VenueTemplateEditorState.main);
        if (this.#venueTemplate()) {
            combineLatest([this.#venueTemplatesSrv.venueTpl.get$(), this.#venueTemplatesSrv.venueTpl.inProgress$()])
                .pipe(
                    take(1),
                    filter(([venueTemplate, loading]) => !loading && (!venueTemplate || venueTemplate.id !== this.#venueTemplate().id))
                )
                .subscribe(() => this.#venueTemplatesSrv.venueTpl.load(this.#venueTemplate().id));
        }
    }

    private resetView(reloadVenueMap: boolean): void {
        if (reloadVenueMap) {
            this.loadVenueMap();
            this._venueTemplateViewComponent()?.reloadSvg();
        }
        this.#standardVenueTemplateChangesSrv.clearModifiedItems();
        this.#standardVenueTemplateSrv.setCurrentState$(VenueTemplateEditorState.main);
    }

    private saveVenueMap(): Observable<boolean> {
        return this.#standardVenueTemplateSaveSrv
            .getModifiedItemsToSave$()
            .pipe(
                switchMap(modifiedItems => {
                    if (modifiedItems?.seats?.length || modifiedItems?.nnzs?.length) {
                        return this.#venueMapSrv.updateVenueMap(
                            {
                                tplId: this.#venueTemplate()?.id,
                                eventId: this.$session()?.event.id,
                                sessionId: this.$session()?.id
                            },
                            modifiedItems.seats,
                            modifiedItems.nnzs
                        )
                            .pipe(
                                finalize(() => {
                                    this.#standardVenueTemplateChangesSrv.clearModifiedItems();
                                    this.loadVenueMap();
                                })
                            );
                    } else {
                        return of(true);
                    }
                }),
                take(1)
            );
    }

    private saveImage(): Observable<void> {
        return combineLatest([this.#venueTemplatesSrv.venueTpl.get$(), this.#standardVenueTemplateChangesSrv.getTemplateImage$()])
            .pipe(
                switchMap(([venueTemplate, obFile]) => {
                    const image = (obFile as ObFile)?.data || null;
                    if ((venueTemplate.image_url || null) !== image) {
                        return this.#venueTemplatesSrv
                            .updateVenueTemplate(this.#venueTemplate().id, { image })
                            .pipe(tap(() => this.#venueTemplatesSrv.venueTpl.load(this.#venueTemplate().id)));
                    } else {
                        return of(null);
                    }
                }),
                take(1)
            );
    }
}
