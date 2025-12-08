import { VenueTemplateItemType } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { EdNotNumberedZone, EdRow, EdSeat, EdSector, EdVenueTplItem } from '../venue-tpl-editor-venue-map-items.model';

export class VenueTplEdTreeItemWrapper {

    private _unfilteredChildren: VenueTplEdTreeItemWrapper[];

    static readonly ROOT_ID = 'root';

    item: EdVenueTplItem;
    id: string;
    label: string;
    level: number;
    expandable: boolean;
    children: VenueTplEdTreeItemWrapper[];
    disabled: boolean;
    selectable: boolean;
    selected: boolean;
    partiallySelected: boolean;
    totalSeats: number;
    editable: boolean;
    deletable: boolean;

    updateItemData: (currentViewId: number, filterInViewItems: boolean, selectedSeats: Set<number>, selectedZones: Set<number>) => void;

    constructor(source: EdVenueTplItem | EdSector[]) {
        if (Array.isArray(source)) {
            this.id = VenueTplEdTreeItemWrapper.ROOT_ID; // one root per parse
            this.item = {} as never;
            this.parseRoot(source);
        } else {
            // aisles has an invalid id initially, row parser resets the id correctly
            this.id = this.generateId(source.itemType, source.itemType !== VenueTemplateItemType.aisle && source.id);
            this.item = source;
            switch (source.itemType) {
                case VenueTemplateItemType.seat:
                    this.parseSeat();
                    break;
                case VenueTemplateItemType.row:
                    this.parseRow();
                    break;
                case VenueTemplateItemType.notNumberedZone:
                    this.parseNotNumberedZone();
                    break;
                case VenueTemplateItemType.aisle:
                    this.parseAisle();
                    break;
                case VenueTemplateItemType.sector:
                    this.parseSector();
                    break;
            }
        }
    }

    // STATIC METHODS

    static parseVenueMap(sectors: EdSector[]): VenueTplEdTreeItemWrapper[] {
        return [new VenueTplEdTreeItemWrapper(sectors)];
    }

    // ROOT

    private parseRoot(sectors: EdSector[]): void {
        this.level = 0;
        this.expandable = true;
        this.selectable = true;
        this._unfilteredChildren = sectors.map(sector => new VenueTplEdTreeItemWrapper(sector));
        this.updateItemData = this.updateRootData.bind(this);
    }

    private updateRootData(
        currentViewId: number, filterInViewItems: boolean, selectedSeats: Set<number>, selectedZones: Set<number>
    ): void {
        this._unfilteredChildren?.forEach(item => item.updateItemData(currentViewId, filterInViewItems, selectedSeats, selectedZones));
        if (filterInViewItems) {
            this.children = this._unfilteredChildren.filter(sector => !sector.disabled);
        } else {
            this.children = this._unfilteredChildren;
        }
        this.updateExpandableSelected();
    }

    // SECTOR

    private parseSector(): void {
        const sector: EdSector = this.item as EdSector;
        this.level = 1;
        this.deletable = true;
        this.editable = true;
        this.expandable = true;
        this.selectable = true;
        this.totalSeats =
            (
                sector.rows
                    ?.filter(row => !row.delete)
                    .flatMap(row => row.seats || [])
                    .filter(seat => !seat.delete)?.length
                ?? 0
            ) + (
                sector.notNumberedZones
                    ?.filter(nnz => !nnz.delete)
                    .map(nnz => nnz.capacity)
                    .reduce((p, c) => p + c, 0)
                ?? 0
            );
        this._unfilteredChildren = [];
        if (sector.rows && sector.rows.length > 0) {
            this._unfilteredChildren.push(
                ...sector.rows
                    .filter(item => !item.delete)
                    .sort((a, b) => a.order - b.order)
                    .map(row => new VenueTplEdTreeItemWrapper(row))
            );
        }
        if (sector.notNumberedZones && sector.notNumberedZones.length > 0) {
            this._unfilteredChildren.push(...sector.notNumberedZones
                .filter(item => !item.delete)
                .map(zone => new VenueTplEdTreeItemWrapper(zone))
            );
        }
        this.updateItemData = this.updateSectorData.bind(this);
    }

    private updateSectorData(
        currentViewId: number, filterInViewItems: boolean, selectedSeats: Set<number>, selectedZones: Set<number>
    ): void {
        this._unfilteredChildren?.forEach(item => item.updateItemData(currentViewId, filterInViewItems, selectedSeats, selectedZones));
        const sector = this.item as EdSector;
        this.label = sector.name;
        if (filterInViewItems) {
            this.children = this._unfilteredChildren.filter(item => !item.disabled);
        } else {
            this.children = this._unfilteredChildren;
        }
        this.disabled = this.children.every(child => child.disabled);
        this.updateExpandableSelected();
    }

    // row

    private parseRow(): void {
        const row: EdRow = this.item as EdRow;
        this.level = 2;
        this.totalSeats = row.seats?.filter(seat => !seat.delete).length || 0;
        this.editable = true;
        this.deletable = true;
        this.expandable = true;
        this.selectable = true;
        this._unfilteredChildren = [];
        if (row.seats && row.seats.length > 0) {
            let lastRowBlock = null as string;
            let aisleCounter = 0;
            row.seats
                ?.filter(item => !item.delete)
                .sort((a, b) => a.order - b.order)
                .forEach(seat => {
                    if (lastRowBlock === null) {
                        lastRowBlock = seat.rowBlock;
                    } else if (lastRowBlock !== seat.rowBlock) {
                        const aisleWrapper = new VenueTplEdTreeItemWrapper({ itemType: VenueTemplateItemType.aisle });
                        aisleWrapper.id = this.generateId(`${this.id}-${VenueTemplateItemType.aisle}`, aisleCounter++);
                        this._unfilteredChildren.push(aisleWrapper);
                        lastRowBlock = seat.rowBlock;
                    }
                    this._unfilteredChildren.push(new VenueTplEdTreeItemWrapper(seat));
                });
        }
        this.updateItemData = this.updateRowData.bind(this);
    }

    private updateRowData(
        currentViewId: number, filterInViewItems: boolean, selectedSeats: Set<number>, selectedZones: Set<number>
    ): void {
        this._unfilteredChildren?.forEach(item => item.updateItemData(currentViewId, filterInViewItems, selectedSeats, selectedZones));
        this.label = (this.item as EdRow).name;
        const rowSeats = this._unfilteredChildren?.filter(child => child.item.itemType !== VenueTemplateItemType.aisle) || [];
        this.children = this._unfilteredChildren;
        this.disabled = rowSeats?.every(child => child.disabled);
        this.updateExpandableSelected();
    }

    // isAisle

    private parseAisle(): void {
        this.level = 3;
        this.disabled = true;
        this.updateItemData = this.emptyUpdateFunction.bind(this);
    }

    // seat

    private parseSeat(): void {
        this.level = 3;
        this.expandable = false;
        this.deletable = true;
        this.selectable = true;
        this.updateItemData = this.updateSeatData.bind(this);
    }

    private updateSeatData(currentViewId: number, _: boolean, selectedSeats: Set<number>): void {
        const seat = this.item as EdSeat;
        this.label = seat.name;
        this.disabled = seat.view !== currentViewId;
        this.selected = !!selectedSeats?.has((this.item as EdSeat).id);
    }

    // not numbered zones

    private parseNotNumberedZone(): void {
        this.level = 2;
        this.expandable = false;
        this.editable = true;
        this.deletable = true;
        this.selectable = true;
        this.updateItemData = this.updateNotNumberedZoneData.bind(this);
    }

    private updateNotNumberedZoneData(
        currentViewId: number, filterInViewItems: boolean, selectedSeats: Set<number>, selectedZones: Set<number>
    ): void {
        const zone = this.item as EdNotNumberedZone;
        this.label = zone.name;
        this.totalSeats = zone.capacity;
        this.disabled = zone.view !== currentViewId;
        this.selected = !!selectedZones?.has((this.item as EdNotNumberedZone).id);
    }

    private generateId(prefix: string, id: number): string {
        return `${prefix}-${id}`;
    }

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    private emptyUpdateFunction(..._: never): void { }

    private updateExpandableSelected(): void {
        const selectableItems = this.children?.filter(item => item.selectable);
        this.selected = !!selectableItems?.length && selectableItems.every(child => child.selected);
        this.partiallySelected = !this.selected && selectableItems.some(child => child.selected || child.partiallySelected);
    }
}
