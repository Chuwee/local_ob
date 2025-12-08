import { Sector, VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { FlatTreeControl } from '@angular/cdk/tree';
import { MatTreeFlatDataSource, MatTreeFlattener } from '@angular/material/tree';
import { VenueTemplateItemWrapper } from '../models/tree/venue-template-item-wrapper.model';

export class VenueTemplateTreeDataManager {
    private readonly _matTreeFlattener = new MatTreeFlattener<VenueTemplateItemWrapper, { wrapper: VenueTemplateItemWrapper }>(
        (wrapper: VenueTemplateItemWrapper) => ({ wrapper }),
        (node: { wrapper: VenueTemplateItemWrapper }) => node.wrapper.level,
        (node: { wrapper: VenueTemplateItemWrapper }) => node.wrapper.hasChildren,
        (item: VenueTemplateItemWrapper) => item.children.filter(child => !child.hidden)
    );

    private _noIdElementsIncr = 0;

    readonly treeControl = new FlatTreeControl<{ wrapper: VenueTemplateItemWrapper }>(
        dataNode => this._matTreeFlattener.getLevel(dataNode),
        dataNode => this._matTreeFlattener.isExpandable(dataNode)
    );

    readonly dataSource = new MatTreeFlatDataSource(this.treeControl, this._matTreeFlattener);

    constructor() {
    }

    refreshTree(newData: VenueTemplateItemWrapper[] = null): void {
        const data = (newData || this.dataSource.data)?.filter(item => !item.hidden);
        const expandedNodes = new Map<string, VenueTemplateItemWrapper>();
        this.treeControl.dataNodes
            .filter(node => this.treeControl.isExpanded(node))
            .forEach(node => expandedNodes.set(this.getItemUniqueKey(node.wrapper), node.wrapper));
        this.dataSource.data = [];
        this.dataSource.data = data;
        this.treeControl.dataNodes.forEach(node => {
            if (expandedNodes.has(this.getItemUniqueKey(node.wrapper))) {
                this.treeControl.expand(node);
            }
        });
    }

    filterSectors(sectors: Sector[] = null): void {
        if (sectors) {
            const sectorIds = new Set<number>(sectors.map(sector => sector.id));
            this.treeControl.dataNodes
                .forEach(item => {
                    if (item.wrapper.item.itemType === VenueTemplateItemType.sector) {
                        if (!sectorIds.has(item.wrapper.item.id)) {
                            this.filterItem(item.wrapper, true);
                            this.treeControl.collapse(item);
                        } else {
                            this.filterItem(item.wrapper, false);
                        }
                    }
                });
        } else {
            this.treeControl.dataNodes
                .forEach(item => {
                    if (item.wrapper.item.itemType === VenueTemplateItemType.sector) {
                        this.filterItem(item.wrapper, false);
                    }
                });
        }
    }

    getNotFilteredSectors(): Sector[] {
        return this.treeControl.dataNodes
            .map(node => !node.wrapper.filtered && node.wrapper.item.itemType === VenueTemplateItemType.sector && node.wrapper.item)
            .filter(sector => !!sector);
    }

    getSectorIndex(id: number): number {
        return this.dataSource.data.indexOf(
            this.treeControl.dataNodes
                .find(node => node.wrapper.item.itemType === VenueTemplateItemType.sector && node.wrapper.item.id === id)
                ?.wrapper);
    }

    private filterItem(wrapper: VenueTemplateItemWrapper, filtered: boolean): void {
        wrapper.filtered = filtered;
        wrapper.children?.forEach(child => this.filterItem(child, filtered));
    }

    private getItemUniqueKey(wrapper: VenueTemplateItemWrapper): string {
        if (wrapper.item.itemType === VenueTemplateItemType.sector
            || wrapper.item.itemType === VenueTemplateItemType.row
            || wrapper.item.itemType === VenueTemplateItemType.seat
            || wrapper.item.itemType === VenueTemplateItemType.notNumberedZone) {
            return `${wrapper.item.itemType}-${wrapper.item.id}`;
        } else {
            // aisle, notNumbererdZoneStatusCounter, notNumbererdZoneBlockingReasonCounter, notNumbererdZoneSessionPackCounter
            // doesn't have id, but tree data requires a unique key.
            return `${wrapper.item.itemType}-${++this._noIdElementsIncr}`;
        }
    }
}
