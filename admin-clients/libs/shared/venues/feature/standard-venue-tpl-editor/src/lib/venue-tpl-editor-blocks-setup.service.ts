import { inject, Injectable } from '@angular/core';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, take } from 'rxjs/operators';
import { SVGDefs } from './models/SVGDefs.enum';
import { BlockChange } from './models/venue-tpl-editor-blocks.model';
import { EdSeat } from './models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';
import { VenueTplEditorDomService } from './venue-tpl-editor-dom.service';
import { VenueTplEditorVenueMapService } from './venue-tpl-editor-venue-map.service';

@Injectable()
export class VenueTplEditorBlocksSetupService {

    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _venueTplEdState = inject(VenueTplEditorState);

    resetViewSeats(): void {
        combineLatest([
            this.getSvgSeats$(),
            this._venueMapSrv.getVenueItems$()
        ])
            .pipe(take(1))
            .subscribe(([svgSeats, venueItems]) => {
                const viewSeats = new Map<number, EdSeat>();
                svgSeats.map(svgSeat => Number(svgSeat.id))
                    .forEach(seatId => viewSeats.set(seatId, { ...venueItems.seats.get(seatId) }));
                this._venueTplEdState.blocksSetupViewSeats.setValue(viewSeats);
            });
    }

    getSvgSeats$(): Observable<Element[]> {
        return this._domSrv.getSvgSvgElement$()
            .pipe(
                filter(Boolean),
                map(svgElement =>
                    Array.from(svgElement.children)
                        .filter(c => c.tagName === SVGDefs.nodeTypes.seat && c.id && c.classList.contains(SVGDefs.classes.interactive))
                )
            );
    }

    getViewSeats$(): Observable<Map<number, EdSeat>> {
        return this._venueTplEdState.blocksSetupViewSeats.getValue$();
    }

    getModifiedSeats$(): Observable<EdSeat[]> {
        return combineLatest([
            this._venueMapSrv.getVenueItems$(),
            this.getViewSeats$()
        ])
            .pipe(
                map(([venueItems, viewSeats]) =>
                    Array.from(viewSeats?.values() || []).filter(seat => venueItems.seats.get(seat.id)?.rowBlock !== seat.rowBlock)
                )
            );
    }

    getSelectedBlockChanges$(): Observable<BlockChange[]> {
        return this._venueTplEdState.blocksSetupSelection.getValue$();
    }

    selectBlockChanges(blockChanges: BlockChange[]): void {
        this._venueTplEdState.blocksSetupSelection.setValue(blockChanges);
    }

    clearBlockChangesSelection(): void {
        this._venueTplEdState.blocksSetupSelection.setValue(null);
    }

    deleteAisle(): void {
        combineLatest([
            this.getSelectedBlockChanges$().pipe(filter(Boolean)),
            this._venueMapSrv.getVenueMap$(),
            this.getViewSeats$()
        ])
            .pipe(take(1))
            .subscribe(([blockChanges, venueMap, viewSeats]) => {
                blockChanges.forEach(blockChange => {
                    const rowSeats = venueMap.sectors.flatMap(sector => sector.rows)
                        .find(row => !!row.seats.find(seat => seat.id === blockChange.seatWrappers[0].seat.id))
                        .seats
                        .map(seat => viewSeats.get(seat.id));
                    const rowBlockToSet = blockChange.seatWrappers[0].seat.rowBlock;
                    const rowBlockToChange = blockChange.seatWrappers[1].seat.rowBlock;
                    rowSeats.filter(seat => seat.rowBlock === rowBlockToChange).forEach(seat => seat.rowBlock = rowBlockToSet);
                });
                this._venueTplEdState.blocksSetupViewSeats.setValue(viewSeats);
                this._venueTplEdState.blocksSetupSelection.setValue(null);
            });
    }

    changeSeatBlocks(seatsBlocks: EdSeat[][]): void {
        this.getViewSeats$()
            .pipe(take(1))
            .subscribe(viewSeats => {
                let rowBlock = 1;
                seatsBlocks.forEach(seatsBlock => {
                    seatsBlock.forEach(seat => seat.rowBlock = String(rowBlock));
                    rowBlock++;
                });
                this._venueTplEdState.blocksSetupViewSeats.setValue(viewSeats);
            });
    }
}
