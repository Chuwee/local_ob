import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { Circle, Point, SVG } from '@svgdotjs/svg.js';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { filter, map, shareReplay, take, withLatestFrom } from 'rxjs/operators';
import { Aisle, BlockChange, RowInfo, SeatWrapper, SeatWrapperWithDistance } from '../../../models/venue-tpl-editor-blocks.model';
import { EdSeat } from '../../../models/venue-tpl-editor-venue-map-items.model';
import { getDistanceBetween, linesCollide } from '../../../utils/geometry.utils';
import { VenueTplEditorBlocksSetupService } from '../../../venue-tpl-editor-blocks-setup.service';
import { VenueTplEditorDomService } from '../../../venue-tpl-editor-dom.service';
import { VenueTplEditorVenueMapService } from '../../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorSvgModifier } from '../venue-tpl-editor-svg-modifier';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule
    ],
    selector: 'app-venue-tpl-editor-blocks-setup',
    templateUrl: './venue-tpl-editor-blocks-setup.component.html',
    styleUrls: ['./venue-tpl-editor-blocks-setup.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorBlocksSetupComponent extends VenueTplEditorSvgModifier implements OnInit {

    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _blocksSetupSrv = inject(VenueTplEditorBlocksSetupService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);

    private readonly _svgSeatsWithCoords$: Observable<SeatWrapper[]> = combineLatest([
        this._domSrv.getSvgSvgElement$(),
        this._blocksSetupSrv.getSvgSeats$()
    ])
        .pipe(
            filter(sources => sources.every(Boolean)),
            map(([svgSvgElement, svgSeats]) => {
                const mainSvg = SVG(svgSvgElement as Element);
                return svgSeats.map(element => {
                    const rBox = new Circle(element).rbox(mainSvg);
                    const radius = rBox.width / 2;
                    return {
                        element,
                        radius,
                        x: rBox.x + radius,
                        y: rBox.y + radius
                    };
                });
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    private readonly _maxAislePointDistance$ = this._svgSeatsWithCoords$.pipe(
        map(elInfos =>
            Math.ceil(elInfos.map(i => i.radius).reduce((p, c) => p + c, 0) / elInfos.length * 8)
        ),
        shareReplay({ refCount: true, bufferSize: 1 })
    );

    private readonly _aisleCreation = new BehaviorSubject<{
        initPos: Point;
        endPos: Point;
        changed: boolean;
    }>(null);

    readonly viewBox$ = this._domSrv.getSvgSvgElementViewBox$();

    readonly aisleCreation$ = this._aisleCreation.asObservable();

    readonly selectedBlockChanges$ = this._blocksSetupSrv.getSelectedBlockChanges$();

    readonly rowInfos$: Observable<RowInfo[]> = combineLatest(
        [this._svgSeatsWithCoords$, this._blocksSetupSrv.getViewSeats$(), this._venueMapSrv.getVenueItems$()])
        .pipe(
            map(([seatElInfos, viewSeats, venueItems]) => {
                const seatWrappers: SeatWrapper[] = seatElInfos.map(seatElInfo => ({
                    ...seatElInfo,
                    seat: viewSeats.get(Number(seatElInfo.element.id))
                }));
                const rowIds = new Set<number>(seatWrappers.map(sw => sw.seat.rowId));
                return Array.from(rowIds)
                    .map(rowId => {
                        const rowSeatWrappers = this.reorganizeSeats(seatWrappers.filter(wrapper => wrapper.seat.rowId === rowId));
                        return {
                            row: venueItems.rows.get(rowId),
                            seatWrappers: rowSeatWrappers,
                            points: rowSeatWrappers.map(seatInfo => `${seatInfo.x},${seatInfo.y}`).join(' ')
                        };
                    });
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly aislesInfo$: Observable<Aisle[]> = this.rowInfos$.pipe(
        map(rowInfos => rowInfos.map(rowInfo => this.getRowBlockChanges(rowInfo))),
        withLatestFrom(this._maxAislePointDistance$),
        map(([rowsBlockChanges, maxAislePointDistance]) => this.generateAisles(rowsBlockChanges, maxAislePointDistance))
    );

    ngOnInit(): void {
        this._blocksSetupSrv.resetViewSeats();
        this._blocksSetupSrv.clearBlockChangesSelection();
    }

    aisleIsSelected(aisle: Aisle, selectedBlockChanges: BlockChange[]): boolean {
        return selectedBlockChanges?.length
            && aisle.blockChanges.every(aisleBlockChange => selectedBlockChanges.includes(aisleBlockChange));
    }

    blockChangeIsSelected(blockChange: BlockChange, selectedBlockChanges: BlockChange[]): boolean {
        return selectedBlockChanges?.length === 1 && selectedBlockChanges[0] === blockChange;
    }

    selectAisle(aisle: Aisle): void {
        this._aisleCreation.next(null);
        this._blocksSetupSrv.selectBlockChanges(aisle.blockChanges);
    }

    selectBlockChange(blockChange: BlockChange): void {
        this._aisleCreation.next(null);
        this._blocksSetupSrv.selectBlockChanges([blockChange]);
    }

    beginDragCreation(event: MouseEvent): void {
        this._blocksSetupSrv.clearBlockChangesSelection();
        this._domSrv.getWorkAreaCoordinates$()
            .pipe(take(1))
            .subscribe(coordinates => {
                const scale = coordinates?.scale || 1;
                this._aisleCreation.next({
                    initPos: new Point(event.offsetX / scale, event.offsetY / scale),
                    endPos: null,
                    changed: false
                });
            });
    }

    dragCreationMove(event: MouseEvent): void {
        if (this._aisleCreation.value) {
            this._domSrv.getWorkAreaCoordinates$()
                .pipe(take(1))
                .subscribe(coordinates => {
                    const scale = coordinates?.scale || 1;
                    this._aisleCreation.next({
                        initPos: this._aisleCreation.value.initPos,
                        endPos: new Point(event.offsetX / scale, event.offsetY / scale),
                        changed: true
                    });
                });
        }
    }

    endDragCreation(): void {
        if (this._aisleCreation.value) {
            if (this._aisleCreation.value.changed) {
                const initPos = this._aisleCreation.value.initPos;
                const endPos = this._aisleCreation.value.endPos;
                this.rowInfos$.pipe(take(1)).subscribe(rowInfos => {
                    rowInfos.forEach(rowInfo => {
                        const seatsToChange = rowInfo.seatWrappers.filter((seatWrapper, index) => {
                            if (index > 0) {
                                const nextSeatWrapper = rowInfo.seatWrappers[index - 1];
                                return linesCollide({ p1: initPos, p2: endPos }, { p1: seatWrapper, p2: nextSeatWrapper });
                            }
                            return false;
                        });
                        if (seatsToChange.length) {
                            let currentSeatsBlock: EdSeat[] = [];
                            const seatBlocks: EdSeat[][] = [currentSeatsBlock];
                            let currentRowBlock = rowInfo.seatWrappers[0].seat.rowBlock;
                            rowInfo.seatWrappers.forEach(seatWrapper => {
                                if (seatWrapper.seat.rowBlock === currentRowBlock && !seatsToChange.includes(seatWrapper)) {
                                    currentSeatsBlock.push(seatWrapper.seat);
                                } else {
                                    if (currentSeatsBlock.length) {
                                        currentSeatsBlock = [];
                                        seatBlocks.push(currentSeatsBlock);
                                    }
                                    currentSeatsBlock.push(seatWrapper.seat);
                                }
                                currentRowBlock = seatWrapper.seat.rowBlock;
                            });
                            this._blocksSetupSrv.changeSeatBlocks(seatBlocks);
                        }
                    });
                });
            }
            this._aisleCreation.next(null);
        }
    }

    mouseEnterOnDragGuard(event: MouseEvent): void {
        if (this._aisleCreation.value && event.buttons === 0) { // no button
            this._aisleCreation.next(null);
        }
    }

    // AISLE PROCESSING

    private reorganizeSeats(seatWrappers: SeatWrapper[]): SeatWrapper[] {
        if (seatWrappers.length > 1) {
            seatWrappers = this.sortByNearest(seatWrappers); // new world order
            let shorterOrder: SeatWrapper[];
            do {
                shorterOrder = this.searchShortestOrder(seatWrappers);
                seatWrappers = shorterOrder || seatWrappers;
            } while (shorterOrder);
        }
        return seatWrappers;
    }

    private sortByNearest(seatWrappers: SeatWrapper[]): SeatWrapper[] {
        let lastAddedSeat = seatWrappers[seatWrappers.length - 1];
        let pendingSeats = seatWrappers.filter(seatWrapper => seatWrapper !== lastAddedSeat);
        const result = [lastAddedSeat];
        while (result.length < seatWrappers.length) {
            const distances = pendingSeats.map(pendingSeat => getDistanceBetween(lastAddedSeat, pendingSeat));
            lastAddedSeat = pendingSeats[distances.indexOf(Math.min(...distances))];
            pendingSeats = pendingSeats.filter(pendingSeat => pendingSeat !== lastAddedSeat);
            result.push(lastAddedSeat);
        }
        return result.reverse();
    }

    private searchShortestOrder(seatWrappers: SeatWrapper[]): SeatWrapper[] {
        const pathsWithGlobalDistances = [this.getPathWithGlobalDistance(seatWrappers)];
        const distances = pathsWithGlobalDistances[0].wrappersWithDistance.map(wwd => wwd.distance);
        const maxIndex = distances.indexOf(Math.max(...distances)) + 1;
        if (maxIndex > 0 && maxIndex < seatWrappers.length - 1) {
            // first track inverse
            pathsWithGlobalDistances.push(
                this.getPathWithGlobalDistance([
                    ...seatWrappers.slice(0, maxIndex).reverse(),
                    ...seatWrappers.slice(maxIndex)
                ]),
                this.getPathWithGlobalDistance([
                    ...seatWrappers.slice(0, maxIndex).reverse(),
                    ...seatWrappers.slice(maxIndex).reverse()
                ]),
                this.getPathWithGlobalDistance([
                    ...seatWrappers.slice(0, maxIndex),
                    ...seatWrappers.slice(maxIndex).reverse()
                ])
            );
            let shorterPath = pathsWithGlobalDistances[0];
            let shortestIndex = 0;
            pathsWithGlobalDistances.forEach((path, index) => {
                if (Math.round(path.distance) < Math.round(shorterPath.distance)) {
                    shortestIndex = index;
                    shorterPath = path;
                }
            });
            if (shortestIndex !== 0) {
                return shorterPath.seatWrappers;
            } else {
                return null;
            }
        }
    }

    private getPathWithGlobalDistance(seatWrappers: SeatWrapper[]): {
        seatWrappers: SeatWrapper[];
        wrappersWithDistance: SeatWrapperWithDistance[];
        distance: number;
    } {
        const wrappersWithDistance = this.getWithDistances(seatWrappers);
        const distance = wrappersWithDistance.map(item => item.distance).reduce((p, c) => p + c, 0);
        return { seatWrappers, wrappersWithDistance, distance };
    }

    private getWithDistances(seatWrappers: SeatWrapper[]): SeatWrapperWithDistance[] {
        return seatWrappers.map((seatWrapper, index) => ({
            seatWrapper,
            distance: index + 1 < seatWrappers.length ? getDistanceBetween(seatWrapper, seatWrappers[index + 1]) : 0
        }));
    }

    // returns all block changes (pair of seats) from a row.
    private getRowBlockChanges(rowInfo: RowInfo): BlockChange[] {
        let prevSeatWrapper: SeatWrapper;
        const seatPairs: [SeatWrapper, SeatWrapper][] = [];
        rowInfo.seatWrappers.forEach(seatWrapper => {
            if (prevSeatWrapper && prevSeatWrapper.seat.rowBlock !== seatWrapper.seat.rowBlock) {
                seatPairs.push([prevSeatWrapper, seatWrapper]);
            }
            prevSeatWrapper = seatWrapper;
        });
        return seatPairs.map(seatWrappers => ({
            seatWrappers,
            row: rowInfo.row,
            x: (seatWrappers[1].x + seatWrappers[0].x) / 2,
            y: (seatWrappers[1].y + seatWrappers[0].y) / 2
        }));
    }

    // generate aisles with all available block changes, maxAislePointDistance is the maximum distance between 2 row block changes to
    // generate an aisle, if the distance is higher, they will be shown as distinct aisles.
    private generateAisles(rowsBlockChanges: BlockChange[][], maxAislePointDistance: number): Aisle[] {
        const aisles: Aisle[] = []; // accumulative aisles collection
        let prevRowAisles: Aisle[] = [];// prev row aisles.
        // this buckle checks the block changes in each row against the aisles of the previous row.
        rowsBlockChanges.forEach(rowBlockChanges => {
            if (rowBlockChanges?.length) {
                // checks which aisles from the previous row, can be continued with the current row block changes
                const { continuedAisles, orphanBlockChanges } = this.continueAisles(prevRowAisles, rowBlockChanges, maxAislePointDistance);
                // every block change in the current row that doesn't continue an aisle from the previous row, is converted to a new aisle
                const newAisles = orphanBlockChanges.map(blockChange => ({
                    blockChanges: [blockChange],
                    points: ''
                }));
                aisles.push(...newAisles);
                // previous row aisle become the continued ones with the new ones, not continued aisles are discarded
                prevRowAisles = [...continuedAisles, ...newAisles];
            } else { // row without aisles
                prevRowAisles = [];
            }
        });
        aisles.forEach(aisle => aisle.points = aisle.blockChanges.map(blockChange => blockChange.x + ',' + blockChange.y).join(' '));
        return aisles;
    }

    //Searches block changes that can continue the aisles of the previous row, returns a list of the aisles that have been continued,
    // and a list of block changes that have not been used.
    private continueAisles(aisles: Aisle[], blockChanges: BlockChange[], maxAislePointDistance: number): {
        continuedAisles: Aisle[];
        orphanBlockChanges: BlockChange[];
    } {
        const continuedAisles: Aisle[] = [];
        while (aisles.length && blockChanges.length) {
            const closestAisleBlockChanges = this.getClosestBlockChanges(aisles, blockChanges);
            if (closestAisleBlockChanges.distance < maxAislePointDistance) {
                const crossedAisle = continuedAisles.find(continuedAisle => {
                    const prevBlockChange = continuedAisle.blockChanges[continuedAisle.blockChanges.length - 2];
                    const nextBlockChange = continuedAisle.blockChanges[continuedAisle.blockChanges.length - 1];
                    return linesCollide(
                        { p1: prevBlockChange, p2: nextBlockChange },
                        { p1: closestAisleBlockChanges.row1BlockChange, p2: closestAisleBlockChanges.row2BlockChange }
                    );
                });
                if (!crossedAisle) {
                    closestAisleBlockChanges.aisle.blockChanges.push(closestAisleBlockChanges.row2BlockChange);
                    continuedAisles.push(closestAisleBlockChanges.aisle);
                    blockChanges = blockChanges.filter(blockChange => blockChange !== closestAisleBlockChanges.row2BlockChange);
                }
                aisles = aisles.filter(aisle => aisle !== closestAisleBlockChanges.aisle);
            } else {
                aisles = [];
            }
        }
        return { continuedAisles, orphanBlockChanges: blockChanges };
    }

    // Searches the closest block changes between the last changes of the previous row aisles, and the current row block changes
    private getClosestBlockChanges(aisles: Aisle[], rowBlockChanges: BlockChange[]): {
        aisle: Aisle; row1BlockChange: BlockChange; row2BlockChange: BlockChange; distance: number;
    } {
        let aisle: Aisle, row1BlockChange: BlockChange, row2BlockChange: BlockChange, distance: number;
        aisles.map(aisle => aisle.blockChanges[aisle.blockChanges.length - 1])
            .forEach(forRow1BlockChange => rowBlockChanges.forEach(forRow2BlockChange => {
                const forRowsDistance = getDistanceBetween(forRow1BlockChange, forRow2BlockChange);
                if (distance == null || forRowsDistance < distance) {
                    aisle = aisles.find(aisle => aisle.blockChanges.includes(forRow1BlockChange));
                    distance = forRowsDistance;
                    row1BlockChange = forRow1BlockChange;
                    row2BlockChange = forRow2BlockChange;
                }
            }));
        return { aisle, row1BlockChange, row2BlockChange, distance };
    }
}
