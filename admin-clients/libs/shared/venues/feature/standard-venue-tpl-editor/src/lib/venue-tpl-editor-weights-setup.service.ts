import { inject, Injectable } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Box, Circle, G, Shape, SVG } from '@svgdotjs/svg.js';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, take } from 'rxjs/operators';
import { EditSeatsAction } from './actions/edit-seats-action';
import { SVGDefs } from './models/SVGDefs.enum';
import { EdSeat } from './models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorWeightsConfiguration } from './models/venue-tpl-editor-weights-configuration';
import { VenueTplEditorState } from './state/venue-tpl-editor.state';
import { maxWeightValue } from './utils/seat-weights.utils';
import { VenueTplEditorDomService } from './venue-tpl-editor-dom.service';
import { VenueTplEditorVenueMapService } from './venue-tpl-editor-venue-map.service';
import { VenueTplEditorService } from './venue-tpl-editor.service';

@Injectable()
export class VenueTplEditorWeightsSetupService {

    private readonly _venueTplEdState = inject(VenueTplEditorState);
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _domSanitizer = inject(DomSanitizer);

    resetViewSeats(): void {
        this._venueTplEdState.weightsSetupViewSeatsBoxes.setValue(null);
        this._venueTplEdState.weightsSetupViewSeats.setValue(null);
        combineLatest([
            this._domSrv.getSvgSvgElement$(),
            this._venueMapSrv.getVenueItems$()
        ])
            .pipe(take(1))
            .subscribe(([svgElement, venueItems]) => {
                const svg = SVG(svgElement);
                const boxes = new Map<number, Box>();
                const viewSeats = new Map<number, EdSeat>();
                Array.from(svgElement.children)
                    .filter(child => child.tagName === SVGDefs.nodeTypes.seat && child.classList.contains(SVGDefs.classes.interactive))
                    .forEach(svgSeat => {
                        const id = Number(svgSeat.id);
                        boxes.set(id, new Circle(svgSeat).rbox(svg));
                        viewSeats.set(id, { ...venueItems.seats.get(id) });
                    });
                this._venueTplEdState.weightsSetupViewSeatsBoxes.setValue(boxes);
                this._venueTplEdState.weightsSetupViewSeats.setValue(viewSeats);
            });
    }

    getViewSeats$(): Observable<Map<number, EdSeat>> {
        return this._venueTplEdState.weightsSetupViewSeats.getValue$();
    }

    getViewSeatBoxes$(): Observable<Map<number, Box>> {
        return this._venueTplEdState.weightsSetupViewSeatsBoxes.getValue$();
    }

    getViewSeatBox$(): Observable<Box> {
        return this._venueTplEdState.weightsSetupViewSeatsBoxes.getValue$()
            .pipe(
                filter(viewSeatBoxes => !!viewSeatBoxes?.size),
                map(viewSeatBoxes => {
                    let x = null as number, y = null as number, width = 0, height = 0;
                    Array.from(viewSeatBoxes.values()).forEach(box => {
                        x = Math.min(box.x, x ?? box.x);
                        y = Math.min(box.y, y ?? box.y);
                        width = Math.max(box.x + box.width, width);
                        height = Math.max(box.y + box.height, height);
                    });
                    width -= x;
                    height -= y;
                    return new Box(x, y, width, height);
                })
            );
    }

    getWeightsConfiguration$(): Observable<VenueTplEditorWeightsConfiguration> {
        return this._venueTplEdState.weightsConfiguration.getValue$();
    }

    setWeightsConfiguration(conf: VenueTplEditorWeightsConfiguration): void {
        this._venueTplEdState.weightsConfiguration.setValue(conf);
    }

    setSnakeConfiguration(): void {
        combineLatest([this._venueTplEdState.weightsSetupViewSeats.getValue$(), this._venueTplEdState.venueItems.getValue$()])
            .pipe(take(1))
            .subscribe(([viewSeats, venueItems]) => {
                const numSeats = viewSeats.size;
                const rowIds = Array.from(new Set(Array.from(viewSeats.values()).map(seat => seat.rowId)));
                const rows = rowIds
                    .map(rowId => venueItems.rows.get(rowId))
                    .sort((row1, row2) => row1.order - row2.order);
                let setSeats = 0;
                for (let i = 0; i < rows.length; i++) {
                    const row = rows[i];
                    const seatsToSet = i % 2 === 0 ? row.seats : [...row.seats].reverse();
                    seatsToSet.forEach(seat => {
                        const viewSeat = viewSeats.get(seat.id);
                        if (viewSeat) {
                            viewSeat.weight = Math.round(maxWeightValue - (setSeats * maxWeightValue / numSeats));
                            setSeats++;
                        }
                    });
                }
                this._venueTplEdState.weightsSetupViewSeats.setValue(viewSeats);
            });
    }

    getSeatsMask$(): Observable<SafeHtml> {
        return this.getSeatsCopy(this.createMaskCircle.bind(this));
    }

    getSeatsStrokes(): Observable<SafeHtml> {
        return this.getSeatsCopy(this.createStrokeCircle.bind(this));
    }

    commitConfiguration(): void {
        this._venueTplEdState.weightsSetupViewSeats.getValue$()
            .pipe(take(1))
            .subscribe(viewSeats =>
                this._editorSrv.history.enqueue(new EditSeatsAction(
                    Array.from(viewSeats.values()).map(seat => ({ id: seat.id, weight: seat.weight })),
                    this._venueMapSrv
                ))
            );
    }

    private getSeatsCopy(seatShapeCreator: (seat: EdSeat, box: Box) => Shape): Observable<SafeHtml> {
        return combineLatest([this.getViewSeats$(), this.getViewSeatBoxes$()])
            .pipe(
                filter(sources => sources.every(Boolean)),
                map(([seats, boxes]) => {
                    if (seats.size) {
                        const g = new G();
                        Array.from(seats.values()).forEach(seat => g.add(seatShapeCreator(seat, boxes.get(seat.id))));
                        return this._domSanitizer.bypassSecurityTrustHtml(g.node.innerHTML);
                    } else {
                        return null;
                    }
                })
            );
    }

    private createStrokeCircle(seat: EdSeat, box: Box): Circle {
        const circle = this.createCircle(seat, box);
        circle.fill({ opacity: 0 });
        circle.stroke({ color: '#000000', width: 1 });
        circle.attr('vector-effect', 'non-scaling-stroke');
        return circle;
    }

    private createMaskCircle(seat: EdSeat, box: Box): Circle {
        const circle = this.createCircle(seat, box);
        circle.fill('#FFFFFF');
        return circle;
    }

    private createCircle(seat: EdSeat, box: Box): Circle {
        const r = box.width / 2;
        return new Circle({ id: seat.id.toString(), r, cx: box.x + r, cy: box.y + r });
    }
}
