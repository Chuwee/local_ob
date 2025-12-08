import { DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { IdName } from '@admin-clients/shared/data-access/models';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import {
    NotNumberedZone, Sector, StdVenueTplService, VENUE_MAP_SERVICE, VenueMap, VenueMapService, VenueTemplateItemType
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplate, VenueTemplatesService, VenueTemplateStatus } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { CdkVirtualScrollViewport, ScrollingModule } from '@angular/cdk/scrolling';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnDestroy, OnInit, Optional, Output, ViewChild,
    ViewContainerRef
} from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule, UntypedFormBuilder, UntypedFormControl } from '@angular/forms';
import { MatAutocompleteTrigger } from '@angular/material/autocomplete';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuTrigger } from '@angular/material/menu';
import { DomSanitizer } from '@angular/platform-browser';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, debounceTime, distinctUntilChanged, Observable, Subject, switchMap } from 'rxjs';
import { catchError, filter, map, shareReplay, take, takeUntil, tap } from 'rxjs/operators';
import { NnzDialogComponent } from '../dialog/nnz-dialog/nnz-dialog.component';
import { SectorDialogComponent } from '../dialog/sector-dialog/sector-dialog.component';
import { VenueTemplateItemWrapperDataModel } from '../models/tree/venue-template-item-wrapper-data.model';
import { VenueTemplateItemWrapper } from '../models/tree/venue-template-item-wrapper.model';
import { VenueTemplateEditorState } from '../models/venue-template-editor-state.enum';
import { VenueTemplateEditorType } from '../models/venue-template-editor-type.model';
import { VenueTemplateEditorView } from '../models/venue-template-editor-view.enum';
import { VenueTemplateSectorDialogData } from '../models/venue-template-sector-dialog-data.model';
import { VenueTemplateZoneDialogData } from '../models/venue-template-zone-dialog-data.model';
import { SectorActionType, ZoneActionType } from '../models/venue-tpl-tree-dialog-type.enum';
import { StandardVenueTemplateBaseService } from '../services/standard-venue-template-base.service';
import { StandardVenueTemplateChangesService } from '../services/standard-venue-template-changes.service';
import { StandardVenueTemplateFilterService } from '../services/standard-venue-template-filter.service';
import { StandardVenueTemplateSaveService } from '../services/standard-venue-template-save.service';
import { StandardVenueTemplateSelectionService } from '../services/standard-venue-template-selection.service';
import { VenueTemplateTreeDataManager } from './venue-template-tree-data-manager';
import { VenueTemplateTreeService } from './venue-template-tree.service';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ScrollingModule,
        ReactiveFormsModule,
        FormsModule,
        LocalNumberPipe
    ],
    selector: 'app-venue-template-tree',
    templateUrl: './venue-template-tree.component.html',
    styleUrls: ['./venue-template-tree.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [VenueTemplateTreeService]
})
export class VenueTemplateTreeComponent implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _levelMargin = 30;
    private readonly _typesThatCanCreateNodes
        = [VenueTemplateEditorType.eventTemplate, VenueTemplateEditorType.promoterTemplate, VenueTemplateEditorType.venueTemplate];

    private readonly _typesThatCanEditSectors
        = [VenueTemplateEditorType.eventTemplate, VenueTemplateEditorType.promoterTemplate, VenueTemplateEditorType.venueTemplate,
        VenueTemplateEditorType.sessionTemplate, VenueTemplateEditorType.sessionPackTemplate,
        VenueTemplateEditorType.multiSessionTemplate];

    private readonly _typesThatCanEditNNZ
        = [VenueTemplateEditorType.eventTemplate, VenueTemplateEditorType.promoterTemplate, VenueTemplateEditorType.venueTemplate,
        VenueTemplateEditorType.sessionTemplate, VenueTemplateEditorType.sessionPackTemplate,
        VenueTemplateEditorType.multiSessionTemplate];

    @ViewChild(CdkVirtualScrollViewport, { static: true }) private _scrollViewport: CdkVirtualScrollViewport;
    @ViewChild(MatAutocompleteTrigger) private _autocompleteTrigger: MatAutocompleteTrigger;
    private _nonGraphicTemplate: boolean;
    private _venueTemplate: VenueTemplate;
    private _treeDataSource: VenueTemplateItemWrapper[];
    private _treeCrossData: VenueTemplateItemWrapperDataModel = {};
    private _sectorSearchKeyWord: string;
    private _isInUse = false;
    private _filterStateFunctions
        = new Map<VenueTemplateItemType,
            (itemWrapper: VenueTemplateItemWrapper, filteredItems: { seats: Set<number>; nnzs: Set<number> }) => void>();

    readonly LEVEL_LEFT_MARGINS = ['0', this._levelMargin + 'px', this._levelMargin * 2 + 'px', this._levelMargin * 3 + 'px'];
    readonly ARROW_DOWN = 'keyboard_arrow_down';
    readonly ARROW_RIGHT = 'keyboard_arrow_right';
    readonly ITEM_HEIGHT = 35;
    readonly MIN_BUFFER_HEIGHT = this.ITEM_HEIGHT * 4;
    readonly MAX_BUFFER_HEIGHT = this.MIN_BUFFER_HEIGHT * 2;
    readonly typesWithSelectAll
        = [VenueTemplateEditorType.eventTemplate, VenueTemplateEditorType.promoterTemplate, VenueTemplateEditorType.venueTemplate];

    @Input() editorType: VenueTemplateEditorType;
    @Input() session: { id?: number; event?: Partial<IdName>; updating_capacity?: boolean };
    @Output() cancelChanges = new EventEmitter<void>();
    treeDataManager: VenueTemplateTreeDataManager = new VenueTemplateTreeDataManager();
    canAddElements: boolean;
    venueTemplateStatus = VenueTemplateStatus;
    itemTypes = VenueTemplateItemType;
    loading$: Observable<boolean>;
    loaded = false;
    selectAllControl: UntypedFormControl;
    partiallySelected$: Observable<boolean>;
    sectorSearch: UntypedFormControl;
    sectorNames$: Observable<string[]>;
    inCapacityIncrease = false;
    isDirty$: Observable<boolean>;

    get venueTemplate(): VenueTemplate {
        return this._venueTemplate;
    }

    @Input()
    set venueTemplate(value: VenueTemplate) {
        const templateChange = !this._venueTemplate || !value || this._venueTemplate.id !== value.id;
        this.loaded = false;
        this._venueTemplate = value;
        this.updateItemCapabilities();
        if (templateChange) {
            this.treeDataManager.dataSource.data = [];
            this.sectorSearch?.setValue(null);
        }
    }

    get isInUse(): boolean {
        return this._isInUse;
    }

    @Input()
    set isInUse(value: boolean) {
        this._isInUse = value;
        this.updateItemCapabilities();
    }

    constructor(
        private _viewCont: ViewContainerRef,
        private _changeDetector: ChangeDetectorRef,
        private _sanitizer: DomSanitizer,
        private _translateSrv: TranslateService,
        private _matDialog: MatDialog,
        private _ephemeralMessageService: EphemeralMessageService,
        private _fb: UntypedFormBuilder,
        private _messageDialogSrv: MessageDialogService,
        private _venueTemplatesSrv: VenueTemplatesService,
        private _stdVenueTplSrv: StdVenueTplService,
        @Inject(VENUE_MAP_SERVICE) @Optional() private _venueMapSrv: VenueMapService,
        private _venueTemplateTreeSrv: VenueTemplateTreeService,
        private _standardVenueTemplateSrv: StandardVenueTemplateBaseService,
        private _standardVenueTemplateFilterSrv: StandardVenueTemplateFilterService,
        private _standardVenueTemplateSelectionSrv: StandardVenueTemplateSelectionService,
        private _standardVenueTemplateChangesSrv: StandardVenueTemplateChangesService,
        private _standardVenueTemplateSaveSrv: StandardVenueTemplateSaveService
    ) {
        this._venueMapSrv ??= this._stdVenueTplSrv;
        this._treeCrossData.locale = this._translateSrv.getCurrentLang();
        this.initFilterFunctions();
    }

    ngOnInit(): void {
        this.setScrollHeightCheck();
        // LOADING
        this.loading$ = booleanOrMerge([
            this._venueTemplatesSrv.venueTpl.inProgress$(),
            this._venueTemplatesSrv.isVenueTemplateSaving$(),
            this._venueMapSrv.isVenueMapLoading$(),
            this._stdVenueTplSrv.isSectorLoading$(),
            this._stdVenueTplSrv.isZoneLoading$(),
            this._venueTemplatesSrv.isVenueTemplateSaving$(),
            this._venueMapSrv.isVenueMapSaving$(),
            this._stdVenueTplSrv.isNnzSaving$(),
            this._stdVenueTplSrv.isSectorSaving$(),
            this._stdVenueTplSrv.isNnzCapacityIncreaseSaving$()

        ]);
        this.isDirty$ = this._standardVenueTemplateSaveSrv.isDirty$();
        this.sectorSearch = this._fb.control('');
        // VENUE MAP PARSING
        combineLatest([
            this._venueMapSrv.getVenueMap$(),
            this._standardVenueTemplateFilterSrv.getFilteredVenueItems$(),
            this._standardVenueTemplateFilterSrv.isFiltering$(),
            this._standardVenueTemplateChangesSrv.getModifiedItems$() // trigger
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([venueMap, filteredItems, isFiltering]) => this.createTreeContent(venueMap, filteredItems, isFiltering));
        // SELECTION UPDATE MECHANISMS
        combineLatest([
            this._standardVenueTemplateSelectionSrv.getSelectionQueue$(),
            this._standardVenueTemplateSelectionSrv.getSelectedVenueItems$()
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([selection, selectedVenueItems]) => {
                this._treeCrossData.selectedSeats = selectedVenueItems.seats;
                this._treeCrossData.selectedZones = selectedVenueItems.nnzs;
                const typesToRefresh: VenueTemplateItemType[] = [];
                const dependantTypesToRefresh: VenueTemplateItemType[] = [];
                if (!selection.items || selection.items.some(item => item.itemType === VenueTemplateItemType.seat)) {
                    typesToRefresh.push(VenueTemplateItemType.seat);
                    dependantTypesToRefresh.push(VenueTemplateItemType.row, VenueTemplateItemType.sector);
                }
                if (!selection.items || selection.items.some(item => item.itemType === VenueTemplateItemType.notNumberedZone)) {
                    typesToRefresh.push(VenueTemplateItemType.notNumberedZone);
                    if (dependantTypesToRefresh.indexOf(VenueTemplateItemType.sector) === -1) {
                        dependantTypesToRefresh.push(VenueTemplateItemType.sector);
                    }
                }
                VenueTemplateItemWrapper.updateItems(this._treeDataSource, typesToRefresh);
                dependantTypesToRefresh.forEach(type => VenueTemplateItemWrapper.updateItems(this._treeDataSource, [type]));
                this._changeDetector.markForCheck();
            });
        // NODE APPEARANCE PARAMETERS
        combineLatest([
            this._standardVenueTemplateSrv.getLabelGroups$(),
            this._standardVenueTemplateSrv.getSelectedLabelGroup$()
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([labelGroups, selectedLabelGroup]) => {
                VenueTemplateItemWrapper.indexStateLabelGroup(this._treeCrossData, labelGroups, this._sanitizer);
                VenueTemplateItemWrapper.indexBlockingReasons(this._treeCrossData, labelGroups);
                VenueTemplateItemWrapper.indexSelectedLabelGroup(this._treeCrossData, selectedLabelGroup);
                VenueTemplateItemWrapper.updateItems(
                    this._treeDataSource, [
                    VenueTemplateItemType.seat,
                    VenueTemplateItemType.notNumberedZone,
                    VenueTemplateItemType.notNumberedZoneStatusCounter,
                    VenueTemplateItemType.notNumberedZoneBlockingReasonCounter
                ]);
                this._changeDetector.markForCheck();
            });
        this.sectorNames$ = combineLatest([
            this._venueMapSrv.getVenueMap$(),
            this.sectorSearch.valueChanges,
            this._standardVenueTemplateFilterSrv.getFilteredVenueItems$()
        ])
            .pipe(
                map(([venueMap, keyword]) => [this.updateSectorsVisibility(venueMap, keyword), keyword]),
                tap(([sectors, _]) => {
                    this._standardVenueTemplateSelectionSrv.unselectAll();
                    this.treeDataManager.filterSectors(sectors);
                    if (sectors?.length) {
                        this._scrollViewport.scrollToIndex(this.treeDataManager.getSectorIndex(sectors[0].id));
                    }
                }),
                map(([sectors, keyword]) => {
                    if (keyword?.length) {
                        return sectors
                            ?.sort((sector1, sector2) => sector1.name.localeCompare(sector2.name))
                            ?.filter((sector, index) => index < 5).map(sector => sector.name);
                    } else {
                        return [];
                    }
                }),
                shareReplay(1)
            );
        this._standardVenueTemplateSrv.getCurrentView$()
            .pipe(
                filter(view => view === VenueTemplateEditorView.tree),
                takeUntil(this._onDestroy)
            )
            .subscribe(() => {
                this.sectorSearch.setValue(null, { emitEvent: false });
                this.treeDataManager.filterSectors();
            });
        this._standardVenueTemplateSrv.getCurrentState$()
            .pipe(
                map(state => state === VenueTemplateEditorState.capacityIncrease),
                takeUntil(this._onDestroy)
            )
            .subscribe(capacityIncrease => {
                this.inCapacityIncrease = capacityIncrease;
                this.updateItemCapabilities();
                this.refreshTreeData();
            });
        // select all logic
        this.selectAllControl = this._fb.control(false);
        this.selectAllControl.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this._standardVenueTemplateSelectionSrv.selectSectors(this.treeDataManager.getNotFilteredSectors()));
        this.partiallySelected$ = combineLatest([
            combineLatest([
                this._standardVenueTemplateFilterSrv.getFilteredVenueItems$(),
                this._standardVenueTemplateSrv.getVenueItems$().pipe(filter(items => !!items))
            ])
                .pipe(
                    map(([filteredItems, venueItems]) => {
                        let numElements = 0;
                        venueItems.seats?.forEach(seat => {
                            if (!filteredItems.seats?.has(seat.id)) {
                                numElements++;
                            }
                        });
                        venueItems.nnzs?.forEach(nnz => {
                            if (!filteredItems.nnzs?.has(nnz.id)) {
                                numElements++;
                            }
                        });
                        return numElements;
                    }),
                    distinctUntilChanged()
                ),
            this._standardVenueTemplateSelectionSrv.getSelectionQueue$()
                .pipe(
                    switchMap(() => this._standardVenueTemplateSelectionSrv.getSelectedVenueItems$()),
                    map(selectedItems => selectedItems.seats.size + selectedItems.nnzs.size),
                    distinctUntilChanged()
                )
        ])
            .pipe(
                map(([numElements, selectedItems]) => {
                    if (selectedItems > 0 && selectedItems < numElements) {
                        if (this.selectAllControl.value) {
                            this.selectAllControl.setValue(false, { emitEvent: false });
                        }
                        return true;
                    } else if (selectedItems === numElements) {
                        if (!this.selectAllControl.value) {
                            this.selectAllControl.setValue(true, { emitEvent: false });
                        }
                        return false;
                    } else if (selectedItems === 0) {
                        if (this.selectAllControl.value) {
                            this.selectAllControl.setValue(false, { emitEvent: false });
                        }
                        return false;
                    }
                })
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    expandNode(node: { wrapper: VenueTemplateItemWrapper }, wrapper: VenueTemplateItemWrapper): void {
        if (wrapper.hasChildren) {
            this.treeDataManager.treeControl.toggle(node);
        }
    }

    selectNode(event: MouseEvent, itemWrapper: VenueTemplateItemWrapper): void {
        VenueTemplateTreeComponent.stopEventPropagations(event);
        switch (itemWrapper.item.itemType) {
            case VenueTemplateItemType.sector:
                this._standardVenueTemplateSelectionSrv.selectSectors([itemWrapper.item]);
                break;
            case VenueTemplateItemType.row:
                this._standardVenueTemplateSelectionSrv.selectRow(itemWrapper.item);
                break;
            case VenueTemplateItemType.seat:
                this._standardVenueTemplateSelectionSrv.selectSeats([itemWrapper.item.id], true);
                break;
            case VenueTemplateItemType.notNumberedZone:
                this._standardVenueTemplateSelectionSrv.selectNNZs([itemWrapper.item.id], true);
                break;
        }
    }

    editNode(event: MouseEvent, itemWrapper: VenueTemplateItemWrapper, zoneMenuTrigger: MatMenuTrigger): void {
        VenueTemplateTreeComponent.stopEventPropagations(event);
        if (itemWrapper.item.itemType === VenueTemplateItemType.sector) {
            if (!this.inCapacityIncrease) {
                this.openSectorDialog(itemWrapper.item, SectorActionType.editName);
            } else {
                this.openSectorDialog(itemWrapper.item, SectorActionType.increaseEdit);
            }
        } else if (itemWrapper.item.itemType === VenueTemplateItemType.notNumberedZone) {
            if (this._nonGraphicTemplate) {
                if (!this._isInUse && !this.inCapacityIncrease) {
                    zoneMenuTrigger.openMenu();
                } else if (this._isInUse && this.inCapacityIncrease) {
                    this.openZoneDialog(itemWrapper.item, ZoneActionType.increaseEditCapacity);
                } else if (this._isInUse && !this.inCapacityIncrease) {
                    this.openZoneDialog(itemWrapper.item, ZoneActionType.editName);
                }
            } else {
                this.openZoneDialog(itemWrapper.item, ZoneActionType.editName);
            }
        }
    }

    //TODO: remove when anybody whines about performance
    getEditTooltipLiteral(itemWrapper: VenueTemplateItemWrapper): string {
        if (itemWrapper.item.itemType === VenueTemplateItemType.sector) {
            return this._translateSrv.instant('VENUE_TPL_MGR.ACTIONS.EDIT_SECTOR');
        } else if (this._nonGraphicTemplate) {
            if (!this._isInUse && !this.inCapacityIncrease) {
                return this._translateSrv.instant('VENUE_TPL_MGR.ACTIONS.EDIT_NNZ DONE');
            } else if (this._isInUse && this.inCapacityIncrease) {
                return this._translateSrv.instant('VENUE_TPL_MGR.ACTIONS.EDIT_NNZ_CAPACITY');
            } else if (this._isInUse && !this.inCapacityIncrease) {
                return this._translateSrv.instant('VENUE_TPL_MGR.ACTIONS.EDIT_NNZ_NAME');
            }
        } else {
            return this._translateSrv.instant('VENUE_TPL_MGR.ACTIONS.EDIT_NNZ_NAME');
        }
    }

    editZoneName(itemWrapper: VenueTemplateItemWrapper): void {
        this.openZoneDialog(itemWrapper.item as NotNumberedZone, ZoneActionType.editName);
    }

    editZoneCapacity(itemWrapper: VenueTemplateItemWrapper): void {
        this.openZoneDialog(itemWrapper.item as NotNumberedZone, ZoneActionType.editCapacity);
    }

    cloneNode(event: MouseEvent, itemWrapper: VenueTemplateItemWrapper): void {
        VenueTemplateTreeComponent.stopEventPropagations(event);
        if (!this.inCapacityIncrease) {
            if (itemWrapper.item.itemType === VenueTemplateItemType.sector) {
                this.openSectorDialog(itemWrapper.item, SectorActionType.clone);
            } else if (itemWrapper.item.itemType === VenueTemplateItemType.notNumberedZone) {
                this.openZoneDialog(itemWrapper.item, ZoneActionType.clone);
            }
        }
    }

    newSector(): void {
        if (!this.inCapacityIncrease) {
            this.openSectorDialog(null, SectorActionType.create);
        } else {
            this.openSectorDialog(null, SectorActionType.increaseCreate);
        }
    }

    newZone(): void {
        if (!this.inCapacityIncrease) {
            this.openZoneDialog(null, ZoneActionType.create);
        } else {
            this.openZoneDialog(null, ZoneActionType.increaseCreate);
        }
    }

    deleteNodeWarning(event: MouseEvent, itemWrapper: VenueTemplateItemWrapper): void {
        VenueTemplateTreeComponent.stopEventPropagations(event);
        this._messageDialogSrv
            .showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.WARNING',
                message: itemWrapper.item.itemType === VenueTemplateItemType.sector ?
                    'VENUE_TPLS.DELETE_SECTOR_WARNING' : 'VENUE_TPL_MGR.DIALOGS.DELETE_ZONE_WARNING',
                messageParams: itemWrapper.item.itemType === VenueTemplateItemType.sector ?
                    { sectorName: (itemWrapper.item).name } : { zoneName: (itemWrapper.item as NotNumberedZone).name },
                actionLabel: 'FORMS.ACTIONS.YES'
            })
            .subscribe(success => {
                if (success) {
                    if (itemWrapper.item.itemType === VenueTemplateItemType.sector) {
                        this.deleteSector(itemWrapper.item);
                    } else if (itemWrapper.item.itemType === VenueTemplateItemType.notNumberedZone) {
                        this.deleteZone(itemWrapper.item);
                    }
                }
            });
    }

    updateSectorsVisibility(venueMap: VenueMap, value: string): Sector[] {
        value = value?.length && value.toLowerCase() || null;
        this._sectorSearchKeyWord = value;
        return value && venueMap?.sectors.filter(sector => sector.name.toLowerCase().indexOf(value) !== -1) || venueMap?.sectors;
    }

    refreshKeyword(): void {
        this.sectorSearch.setValue(this.sectorSearch.value);
    }

    resetKeyword(): void {
        this.sectorSearch.setValue(null);
    }

    hideAutoComplete(): void {
        this._autocompleteTrigger.closePanel();
    }

    saveCapacityIncrease(): void {
        this._messageDialogSrv.showWarn({
            size: DialogSize.MEDIUM,
            title: 'TITLES.WARNING',
            message: 'VENUE_TPL_MGR.DIALOGS.CAPACITY_INCREASE_WARNING',
            actionLabel: 'VENUE_TPL_MGR.ACTIONS.CONFIRM_INCREASE'
        })
            .pipe(filter(Boolean))
            .subscribe(() => this._venueTemplateTreeSrv.commitIncrease()
                .pipe(
                    catchError(error => {
                        this._standardVenueTemplateSrv.setCurrentState$(VenueTemplateEditorState.main);
                        this.loadVenueMap();
                        this._standardVenueTemplateChangesSrv.clearModifiedItems();
                        throw error;
                    })
                )
                .subscribe(() => {
                    this._ephemeralMessageService.showSaveSuccess();
                    this._standardVenueTemplateSrv.setCurrentState$(VenueTemplateEditorState.main);
                    this.loadVenueMap();
                    this._standardVenueTemplateChangesSrv.clearModifiedItems();
                }));
    }

    cancel(): void {
        this.cancelChanges.emit();
    }

    private loadVenueMap(): void {
        this._venueMapSrv.loadVenueMap({
            tplId: this._venueTemplate.id,
            eventId: this.session?.event.id,
            sessionId: this.session?.id,
            updatingCapacity: this.session?.updating_capacity
        });
    }

    private refreshTreeData(dataSource: VenueTemplateItemWrapper[] = null): void {
        const oldscrollTop = this._scrollViewport.elementRef.nativeElement.scrollTop;
        this.treeDataManager.refreshTree(dataSource);
        setTimeout(() => this._scrollViewport.elementRef.nativeElement.scrollTop = oldscrollTop);
    }

    private updateItemCapabilities(): void {
        this._nonGraphicTemplate = this._venueTemplate
            && !this._venueTemplate.graphic
            && this._typesThatCanCreateNodes.indexOf(this.editorType) !== -1;

        this.canAddElements = this._nonGraphicTemplate && (!this._isInUse || this.inCapacityIncrease);
        this._treeCrossData.canEditSectors = this._typesThatCanEditSectors.indexOf(this.editorType) !== -1;
        this._treeCrossData.canCloneSectors = this._nonGraphicTemplate && !this._isInUse;
        this._treeCrossData.canDeleteSectors = this._nonGraphicTemplate && !this._isInUse;
        this._treeCrossData.canEditNNZ = this._typesThatCanEditNNZ.indexOf(this.editorType) !== -1;
        this._treeCrossData.canCloneNNZ = this._nonGraphicTemplate && !this._isInUse;
        this._treeCrossData.canDeleteNNZ = this._nonGraphicTemplate && !this._isInUse;
    }

    private recreateTreeContent(): void {
        combineLatest([
            this._venueMapSrv.getVenueMap$(),
            this._standardVenueTemplateFilterSrv.getFilteredVenueItems$(),
            this._standardVenueTemplateFilterSrv.isFiltering$()
        ])
            .pipe(take(1))
            .subscribe(([venueMap, filteredItems, isFiltering]) => this.createTreeContent(venueMap, filteredItems, isFiltering));
    }

    private createTreeContent(venueMap: VenueMap, filteredItems: { seats: Set<number>; nnzs: Set<number> }, isFiltering: boolean): void {
        this._treeDataSource = VenueTemplateItemWrapper.parseVenueMap(venueMap, this._treeCrossData);
        if (isFiltering && filteredItems) {
            filteredItems.seats = filteredItems.seats || new Set<number>();
            filteredItems.nnzs = filteredItems.nnzs || new Set<number>();
            this._treeDataSource.forEach(treeItem => this.updateItemFilterState(treeItem, filteredItems));
        }
        this.refreshTreeData(this._treeDataSource);
        this.loaded = venueMap !== null;
        this._changeDetector.markForCheck();
    }

    private initFilterFunctions(): void {
        this._filterStateFunctions.set(VenueTemplateItemType.seat,
            (treeItem: VenueTemplateItemWrapper, filteredItems: { seats: Set<number>; nnzs: Set<number> }) => {
                treeItem.hidden = filteredItems.seats.has(
                    treeItem.item.itemType === VenueTemplateItemType.seat && treeItem.item.id
                );
            });
        this._filterStateFunctions.set(VenueTemplateItemType.row,
            (treeItem: VenueTemplateItemWrapper, filteredItems: { seats: Set<number>; nnzs: Set<number> }) => {
                treeItem.hidden = treeItem.children
                    .filter(children => children.item.itemType === VenueTemplateItemType.seat)
                    .map(childItem => this.updateItemFilterState(childItem, filteredItems))
                    .every(itemIsHidden => itemIsHidden);
            });
        this._filterStateFunctions.set(VenueTemplateItemType.notNumberedZone,
            (treeItem: VenueTemplateItemWrapper, filteredItems: { seats: Set<number>; nnzs: Set<number> }) => {
                treeItem.hidden = filteredItems.nnzs?.has(
                    treeItem.item.itemType === VenueTemplateItemType.notNumberedZone && treeItem.item.id
                );
            });
        this._filterStateFunctions.set(VenueTemplateItemType.sector,
            (treeItem: VenueTemplateItemWrapper, filteredItems: { seats: Set<number>; nnzs: Set<number> }) => {
                treeItem.hidden = treeItem.children
                    .map(childItem => this.updateItemFilterState(childItem, filteredItems))
                    .every(itemIsHidden => itemIsHidden);
            });
        // no filter effect for filter insensitives items
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        const emptyFunc = (): void => { };
        this._filterStateFunctions.set(VenueTemplateItemType.aisle, emptyFunc);
        this._filterStateFunctions.set(VenueTemplateItemType.notNumberedZoneStatusCounter, emptyFunc);
        this._filterStateFunctions.set(VenueTemplateItemType.notNumberedZoneBlockingReasonCounter, emptyFunc);
        this._filterStateFunctions.set(VenueTemplateItemType.notNumberedZoneSessionPackCounter, emptyFunc);
    }

    private updateItemFilterState(treeItem: VenueTemplateItemWrapper, filteredItems: { seats: Set<number>; nnzs: Set<number> }): boolean {
        this._filterStateFunctions.get(treeItem.item.itemType)(treeItem, filteredItems);
        return treeItem.hidden;
    }

    private openSectorDialog(sector: Sector, action: SectorActionType): void {
        this._matDialog.open(SectorDialogComponent, new ObMatDialogConfig({
            action,
            venueTemplate: this._venueTemplate,
            sector
        } as VenueTemplateSectorDialogData, this._viewCont))
            .beforeClosed()
            .subscribe(newSectorId => {
                if (newSectorId) {
                    switch (action) {
                        case SectorActionType.create:
                            this._venueTemplateTreeSrv.loadAndUpdateSector(this._venueTemplate.id, newSectorId)
                                .subscribe(newSector => {
                                    const param = { sectorName: newSector.name };
                                    this._ephemeralMessageService.showSuccess({
                                        msgKey: 'VENUE_TPL_MGR.FORMS.FEEDBACK.ADD_SECTOR',
                                        msgParams: param
                                    });
                                    this.recreateTreeContent();
                                });
                            break;
                        case SectorActionType.clone:
                            this._venueTemplateTreeSrv.loadAndUpdateSector(this._venueTemplate.id, newSectorId)
                                .subscribe(newSector => {
                                    const param = { sectorName: newSector.name };
                                    this._ephemeralMessageService.showSuccess({
                                        msgKey: 'VENUE_TPL_MGR.FORMS.FEEDBACK.CLONE_SECTOR',
                                        msgParams: param
                                    });
                                    this.recreateTreeContent();
                                });
                            break;
                        case SectorActionType.editName:
                            this._venueTemplateTreeSrv.loadAndUpdateSector(this._venueTemplate.id, newSectorId)
                                .subscribe(newSector => {
                                    const param = { sectorName: newSector.name };
                                    this._ephemeralMessageService.showSuccess({
                                        msgKey: 'VENUE_TPL_MGR.FORMS.FEEDBACK.EDIT_SECTOR',
                                        msgParams: param
                                    });
                                    VenueTemplateItemWrapper.updateItems(this._treeDataSource, [VenueTemplateItemType.sector]);
                                    this._changeDetector.markForCheck();
                                });
                            break;
                        case SectorActionType.increaseCreate:
                            this._standardVenueTemplateSrv.updateVenueMapElements();
                            this.recreateTreeContent();
                            break;
                        case SectorActionType.increaseEdit:
                            VenueTemplateItemWrapper.updateItems(this._treeDataSource, [VenueTemplateItemType.sector]);
                            this._standardVenueTemplateSrv.updateVenueMapElements();
                            this._changeDetector.markForCheck();
                            break;
                    }
                }
            });
    }

    private openZoneDialog(notNumberedZone: NotNumberedZone, action: ZoneActionType): void {
        this._matDialog.open(NnzDialogComponent, new ObMatDialogConfig(
            {
                action,
                venueTemplate: this._venueTemplate,
                zone: notNumberedZone,
                capacityEditable: this.canAddElements
            } as VenueTemplateZoneDialogData,
            this._viewCont)
        )
            .beforeClosed()
            .subscribe(zoneId => {
                if (zoneId) {
                    switch (action) {
                        case ZoneActionType.create:
                            this._venueTemplateTreeSrv.loadAndAddZone(this._venueTemplate.id, zoneId)
                                .subscribe(newZone => {
                                    this.recreateTreeContent();
                                    this._ephemeralMessageService.showSuccess({
                                        msgKey: 'VENUE_TPL_MGR.FORMS.FEEDBACK.ADD_ZONE',
                                        msgParams: { zoneName: newZone.name }
                                    });
                                });
                            break;
                        case ZoneActionType.clone:
                            this._venueTemplateTreeSrv.loadAndAddZone(this._venueTemplate.id, zoneId)
                                .subscribe(newZone => {
                                    this.recreateTreeContent();
                                    this._ephemeralMessageService.showSuccess({
                                        msgKey: 'VENUE_TPL_MGR.FORMS.FEEDBACK.ADD_ZONE',
                                        msgParams: { zoneName: newZone.name }
                                    });
                                });
                            break;
                        case ZoneActionType.editName:
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'VENUE_TPL_MGR.FORMS.FEEDBACK.EDIT_ZONE',
                                msgParams: { zoneName: notNumberedZone.name }
                            });
                            this.recreateTreeContent();
                            break;
                        case ZoneActionType.editCapacity:
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'VENUE_TPL_MGR.FORMS.FEEDBACK.EDIT_ZONE',
                                msgParams: { zoneName: notNumberedZone.name }
                            });
                            this._venueTemplateTreeSrv.loadAndUpdateZone(this._venueTemplate.id, zoneId)
                                .subscribe(() => this.recreateTreeContent());
                            break;
                        case ZoneActionType.increaseCreate:
                            this._standardVenueTemplateSrv.updateVenueMapElements();
                            this.recreateTreeContent();
                            break;
                        case ZoneActionType.increaseEditCapacity:
                            VenueTemplateItemWrapper.updateItems(this._treeDataSource, [VenueTemplateItemType.notNumberedZone]);
                            this._standardVenueTemplateSrv.updateVenueMapElements();
                            this._changeDetector.markForCheck();
                            break;
                    }
                }
            });
    }

    private deleteSector(sector: Sector): void {
        this._venueTemplateTreeSrv.deleteSector(this._venueTemplate, sector)
            .subscribe(() => {
                this._ephemeralMessageService.showSuccess({
                    msgKey: 'VENUE_TPLS.DELETE_SECTOR_SUCCESS',
                    msgParams: { sectorName: sector.name }
                });
                this.recreateTreeContent();
            });
    }

    private static stopEventPropagations(event: MouseEvent): void {
        event.stopImmediatePropagation();
        event.stopPropagation();
    }

    private deleteZone(zone: NotNumberedZone): void {
        this._venueTemplateTreeSrv.deleteZone(this._venueTemplate, zone)
            .subscribe(() => {
                this._ephemeralMessageService.showSuccess({
                    msgKey: 'VENUE_TPLS.DELETE_ZONE_SUCCESS',
                    msgParams: { zoneName: zone.name }
                });
                this.recreateTreeContent();
            });
    }

    private setScrollHeightCheck(): void {
        const element = this._scrollViewport.elementRef.nativeElement;
        const checkViewportTrigger = new Subject<number>();
        checkViewportTrigger
            .pipe(
                distinctUntilChanged(),
                filter(h => !!h),
                debounceTime(1000),
                takeUntil(this._onDestroy)
            )
            .subscribe(() => this._scrollViewport.checkViewportSize());
        const resizeObserver = new ResizeObserver(() => checkViewportTrigger.next(element.clientHeight));
        resizeObserver.observe(this._scrollViewport.elementRef.nativeElement);
        this._onDestroy.subscribe(() => {
            resizeObserver.unobserve(element);
            resizeObserver.disconnect();
        });
    }
}
