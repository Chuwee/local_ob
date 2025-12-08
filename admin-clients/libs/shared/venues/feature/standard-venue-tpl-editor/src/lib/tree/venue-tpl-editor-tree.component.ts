import { DialogSize, MessageDialogService, ObDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ResizeObserverDirective, SharedUtilityDirectivesModule } from '@admin-clients/shared/utility/directives';
import { LocalNumberPipe } from '@admin-clients/shared/utility/pipes';
import { mapToTreeDataSource } from '@admin-clients/shared/utility/utils';
import { VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, ViewContainerRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, finalize, switchMap } from 'rxjs';
import { debounceTime, filter, map, shareReplay, take, tap } from 'rxjs/operators';
import { DeleteItemsAction } from '../actions/delete-items-action';
import { VenueTplEditorRowDialogComponent } from '../dialogs/row/venue-tpl-editor-row-dialog.component';
import { VenueTplEditorSectorDialogComponent } from '../dialogs/sector/venue-tpl-editor-sector-dialog.component';
import { VenueTplEdTreeItemWrapper } from '../models/tree/venue-tpl-editor-tree-item-wrapper.model';
import { CapacityEditionCapability } from '../models/venue-tpl-editor-capacity-edition-capability';
import { EditorMode } from '../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorSelection } from '../models/venue-tpl-editor-selection.model';
import { EdNotNumberedZone, EdRow, EdSeat } from '../models/venue-tpl-editor-venue-map-items.model';
import { IdGenerator } from '../utils/editor-id-generator.utils';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../venue-tpl-editor.service';
import { DeleteEmptyRowAction } from '../actions/delete-empty-row-action';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ScrollingModule,
        ReactiveFormsModule,
        FormsModule,
        ResizeObserverDirective,
        SharedUtilityDirectivesModule,
        LocalNumberPipe
    ],
    selector: 'app-venue-tpl-editor-tree',
    templateUrl: './venue-tpl-editor-tree.component.html',
    styleUrls: ['./venue-tpl-editor-tree.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorTreeComponent {
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _viewCont = inject(ViewContainerRef);
    private readonly _dialogSrv = inject(ObDialogService);
    private readonly _messageDialogSrv = inject(MessageDialogService);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _mapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _selectionSrv = inject(VenueTplEditorSelectionService);

    private readonly _filterInViewItems = new BehaviorSubject(false);

    private readonly _treeItemWrappers$ = this._mapSrv.getVenueMap$()
        .pipe(
            filter(Boolean),
            map(venueMap => venueMap?.sectors.filter(sector => !sector.delete) || []),
            map(sectors => VenueTplEdTreeItemWrapper.parseVenueMap(sectors)),
            takeUntilDestroyed(this._destroyRef),
            shareReplay({ bufferSize: 1, refCount: false })
        );

    private readonly _updatedTreeItemWrappers$ = combineLatest([
        this._treeItemWrappers$, this._viewsSrv.getViewData$(), this._filterInViewItems, this._selectionSrv.getSelection$()
    ])
        .pipe(
            tap(([treeWrappers, viewData, filterInViewItems, selection]) =>
                treeWrappers?.forEach(item => item.updateItemData(viewData?.view.id, filterInViewItems, selection.seats, selection.nnzs))
            ),
            map(([treeWrappers]) => treeWrappers),
            debounceTime(10),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly rowItemType = VenueTemplateItemType.row;
    readonly seatItemType = VenueTemplateItemType.seat;
    readonly aisleItemType = VenueTemplateItemType.aisle;
    readonly rootItem = VenueTplEdTreeItemWrapper.ROOT_ID;
    readonly baseEditorMode$ = this._editorSrv.modes.getEditorMode$().pipe(map(mode => mode === EditorMode.base));

    readonly filteringInViewItems$ = this._filterInViewItems.asObservable();

    readonly treeSource$ = this._updatedTreeItemWrappers$
        .pipe(mapToTreeDataSource<VenueTplEdTreeItemWrapper, VenueTplEdTreeItemWrapper, string>({
            trackBy: wrapper => wrapper.id,
            getLevel: wrapper => wrapper.level,
            isExpandable: wrapper => wrapper.expandable,
            getChildren: wrapper => wrapper.children,
            openRootNodesOnStart: true
        }));

    openNewSectorDialog(): void {
        this._dialogSrv.open(VenueTplEditorSectorDialogComponent, null, this._viewCont);
    }

    toggleFilterInViewItems(): void {
        this._filterInViewItems.next(!this._filterInViewItems.value);
    }

    selectNode(event: Event, itemWrappers: VenueTplEdTreeItemWrapper): void {
        event.stopPropagation();
        combineLatest([this._editorSrv.modes.getEditorMode$(), this._selectionSrv.getSelection$()])
            .pipe(
                take(1),
                filter(([mode]) => mode === EditorMode.base),
                map(([_, selection]) => selection)
            )
            .subscribe(selection => this.addNodesToSelection(selection, [itemWrappers]));
    }

    editNode(event: Event, itemWrapper: VenueTplEdTreeItemWrapper): void {
        event.stopPropagation();
        if (itemWrapper.item.itemType === VenueTemplateItemType.sector) {
            const sector = itemWrapper.item;
            this._dialogSrv.open(VenueTplEditorSectorDialogComponent, { ...sector }, this._viewCont);
        } else if (itemWrapper.item.itemType === VenueTemplateItemType.row) {
            const row = itemWrapper.item;
            this._dialogSrv.open(VenueTplEditorRowDialogComponent, { ...row }, this._viewCont);
        }
    }

    deleteNode(event: Event, itemWrapper: VenueTplEdTreeItemWrapper): void {
        event.stopPropagation();
        const item = itemWrapper.item;
        if (item.itemType === VenueTemplateItemType.sector) {
            this.deleteItems(
                item.rows.filter(row => !row.delete).flatMap(row => row.seats).filter(seat => !seat.delete),
                item.notNumberedZones.filter(nnz => !nnz.delete),
                'VENUE_TPL_EDITOR.DELETE_SECTOR_WARNING_TITLE',
                'VENUE_TPL_EDITOR.DELETE_SECTOR_WARNING',
                { sectorName: item.name },
                { sectorIds: [item.id] }
            );
        } else if (item.itemType === VenueTemplateItemType.row) {
            if (item.seats?.length && item.seats.every(seat => !seat.delete)) {
                this.deleteItems(
                    item.seats, null,
                    'VENUE_TPL_EDITOR.DELETE_ROW_WARNING_TITLE',
                    'VENUE_TPL_EDITOR.DELETE_ROW_WARNING',
                    { rowName: item.name },
                    { seatIds: item.seats.map(seat => seat.id) }
                );
            } else {
                this.#deleteEmptyRow(
                    item,
                    'VENUE_TPL_EDITOR.DELETE_ROW_WARNING_TITLE',
                    'VENUE_TPL_EDITOR.DELETE_ROW_WARNING',
                    { rowName: item.name }
                );
            }
        } else if (item.itemType === VenueTemplateItemType.seat) {
            this.deleteItems(
                [item], null,
                'VENUE_TPL_EDITOR.DELETE_SEAT_WARNING_TITLE',
                'VENUE_TPL_EDITOR.DELETE_SEAT_WARNING',
                { seatName: item.name },
                { seatIds: [item.id] }
            );
        } else if (item.itemType === VenueTemplateItemType.notNumberedZone) {
            this.deleteItems(
                null, [item],
                'VENUE_TPL_EDITOR.DELETE_ZONE_WARNING_TITLE',
                'VENUE_TPL_EDITOR.DELETE_ZONE_WARNING',
                { zoneName: item.name },
                { nnzIds: [item.id] }
            );
        } else {
            console.warn('trying to delete not controlled item', itemWrapper.item);
        }
    }

    private deleteItems(
        seats: EdSeat[], nnzs: EdNotNumberedZone[],
        deleteTitleKey: string, deleteMsgKey: string, deleteMsgParams: unknown,
        deleteLists: { sectorIds?: number[]; seatIds?: number[]; nnzIds?: number[]; elements?: SVGElement[] }
    ): void {
        combineLatest([this._viewsSrv.getSvgData$(), this._editorSrv.getCapacityEditionCapability$()])
            .pipe(
                take(1),
                filter(([svgData, editionCapability]) =>
                    (!seats?.length && !nnzs?.length)
                    ||
                    (this.contentIsAvailableInView(svgData.viewId, seats, nnzs) && this.contentCanBeDeleted(editionCapability, seats, nnzs))
                ),
                switchMap(() =>
                    this._messageDialogSrv.showWarn({
                        size: DialogSize.SMALL,
                        title: deleteTitleKey,
                        message: deleteMsgKey,
                        messageParams: deleteMsgParams,
                        actionLabel: 'FORMS.ACTIONS.DELETE'
                    })
                ),
                filter(Boolean),
                map(() => null),
                finalize(() => this._selectionSrv.unselectAll())
            )
            .subscribe(() =>
                this._editorSrv.history.enqueue(
                    new DeleteItemsAction(deleteLists, this._mapSrv, this._viewsSrv, this._domSrv, this._selectionSrv)
                )
            );
    }

    private contentIsAvailableInView(viewId: number, seats: EdSeat[], nnzList: EdNotNumberedZone[]): boolean {
        const itemsViews = new Set([
            ...(seats?.map(seat => seat.view) || []),
            ...(nnzList?.map(nnz => nnz.view) || [])
        ]);
        const result = itemsViews.size === 1 && Array.from(itemsViews.values())[0] === viewId;
        if (!result) {
            this._messageDialogSrv.showAlert({
                size: DialogSize.SMALL,
                message: 'VENUE_TPL_EDITOR.DELETE_CAPACITY_NOT_AVAILABLE_ERROR'
            });
        }
        return result;
    }

    private contentCanBeDeleted(capability: CapacityEditionCapability, seats: EdSeat[], nnzList: EdNotNumberedZone[]): boolean {
        let result = true;
        if (seats?.length || nnzList?.length) {
            if (capability === CapacityEditionCapability.increase) {
                result = (!seats?.length || seats.every(seat => IdGenerator.isTempId(seat.id)))
                    && (!nnzList?.length || nnzList.every(nnz => IdGenerator.isTempId(nnz.id)));
            } else {
                result = capability === CapacityEditionCapability.total;
            }
            if (!result) {
                this._messageDialogSrv
                    .showAlert({
                        size: DialogSize.SMALL,
                        title: 'VENUE_TPL_EDITOR.CAPACITY_DECREMENT_NOT_AVAILABLE_BY_SESSIONS_ERROR_TITLE',
                        message: 'VENUE_TPL_EDITOR.CAPACITY_DECREMENT_NOT_AVAILABLE_BY_SESSIONS_ERROR'
                    });
            }
        }
        return result;
    }

    #deleteEmptyRow(row: EdRow, deleteTitleKey: string, deleteMsgKey: string, deleteMsgParams: unknown): void {
        this._messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: deleteTitleKey,
            message: deleteMsgKey,
            messageParams: deleteMsgParams,
            actionLabel: 'FORMS.ACTIONS.DELETE'
        })
            .pipe(filter(Boolean))
            .subscribe(() => this._editorSrv.history.enqueue(new DeleteEmptyRowAction(row, this._mapSrv)));
    }

    private addNodesToSelection(selection: VenueTplEditorSelection, itemWrappers: VenueTplEdTreeItemWrapper[]): void {
        const nestedWrappers: VenueTplEdTreeItemWrapper[] = [];
        itemWrappers.forEach(itemWrapper => {
            if (!itemWrapper.disabled && itemWrapper.selectable) {
                if (itemWrapper.item.itemType === VenueTemplateItemType.sector) {
                    nestedWrappers.push(...itemWrapper.children);
                } else if (itemWrapper.item.itemType === VenueTemplateItemType.row) {
                    nestedWrappers.push(...itemWrapper.children);
                } else if (itemWrapper.item.itemType === VenueTemplateItemType.seat) {
                    nestedWrappers.push(itemWrapper);
                } else if (itemWrapper.item.itemType === VenueTemplateItemType.notNumberedZone) {
                    nestedWrappers.push(itemWrapper);
                }
            }
        });
        if (itemWrappers.some(w => w.item.itemType === VenueTemplateItemType.sector || w.item.itemType === VenueTemplateItemType.row)) {
            this.addNodesToSelection(selection, nestedWrappers);
        } else {
            const seatIds: number[] = nestedWrappers
                .filter(item => item.item.itemType === VenueTemplateItemType.seat)
                .map(wrapper => (wrapper.item as EdSeat).id);
            const nnzIds: number[] = nestedWrappers
                .filter(item => item.item.itemType === VenueTemplateItemType.notNumberedZone)
                .map(wrapper => (wrapper.item as EdNotNumberedZone).id);
            if (seatIds.every(seatId => selection.seats.has(seatId)) && nnzIds.every(nnzId => selection.nnzs.has(nnzId))) {
                this._selectionSrv.selectItems({ seatIds, nnzIds }, false);
            } else {
                this._selectionSrv.selectItems({ seatIds, nnzIds });
            }
        }
    }
}
