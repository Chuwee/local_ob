import { DialogSize, MessageDialogConfig, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { Circle, G, Rect } from '@svgdotjs/svg.js';
import { bufferCount, concat, Observable, of, Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { SVGDefs } from './models/SVGDefs.enum';
import { VenueTplEditorConsistencyCheckResult } from './models/venue-tpl-editor-consistency-check-result';
import { venueTplEditorSeatMatrixConfInitValue } from './models/venue-tpl-editor-seat-matrix-conf.model';
import { EdNotNumberedZone, EdSeat, EdVenueMapMaps } from './models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorViewData } from './models/venue-tpl-editor-view-data.model';
import { VenueTplEditorViewLink } from './models/venue-tpl-editor-view-link.model';

export class VenueTplEditorTplCorrectionsManager {

    readonly #msgDialogSrv: MessageDialogService;
    readonly #svg: SVGSVGElement;
    readonly #inUse: boolean;
    readonly #venueItems: EdVenueMapMaps;

    #viewBox: { width: number; height: number };
    #newRectSize: number;
    #margin: number;
    #svgModified: boolean;

    readonly #results = new Subject<{
        svgModified?: boolean;
        linksToDelete?: VenueTplEditorViewLink[];
        zonesToDelete?: EdNotNumberedZone[];
        seatsToDelete?: EdSeat[];
        svgRefreshRequired?: boolean;
    }>();

    readonly results$ = this.#results.asObservable();

    constructor({ viewData, viewSvg, viewBox, venueItems, inUse }:
        {
            viewData: VenueTplEditorViewData;
            viewSvg: SVGSVGElement;
            viewBox: number[];
            venueItems: EdVenueMapMaps;
            inUse: boolean;
        },
        msgDialogSrv: MessageDialogService
    ) {
        this.#svg = viewSvg;
        this.#viewBox = { width: viewBox[2], height: viewBox[3] };
        this.#venueItems = venueItems;
        this.#inUse = inUse;
        this.#msgDialogSrv = msgDialogSrv;
        this.#processInconsistencies(this.#checkViewConsistency(viewData, venueItems));
    }

    // CHECKS

    #checkViewConsistency(
        viewData: VenueTplEditorViewData, items: EdVenueMapMaps
    ): VenueTplEditorConsistencyCheckResult {
        const result: VenueTplEditorConsistencyCheckResult = { result: 'ok' };
        // interactive elements indexing
        const interactiveElements = Array.from(this.#svg.children).filter(e => e.id && e.classList.contains(SVGDefs.classes.interactive));
        let ko = false;
        ko = this.#linksWithoutShapeCheck(interactiveElements, viewData, result) || ko;
        ko = this.#linksWithoutModelCheck(interactiveElements, viewData, result) || ko;
        ko = this.#zonesWithoutShapeCheck(interactiveElements, viewData, items, result) || ko;
        ko = this.#zonesWithoutModelCheck(interactiveElements, items, result) || ko;
        ko = this.#seatsWithoutShapeCheck(interactiveElements, viewData, items, result) || ko;
        ko = this.#seatsWithoutModelCheck(interactiveElements, items, result) || ko;
        result.result = ko ? 'ko' : 'ok';
        return result;
    }

    #linksWithoutShapeCheck(
        interactiveElements: Element[], viewData: VenueTplEditorViewData,
        result: VenueTplEditorConsistencyCheckResult
    ): boolean {
        const linkElements = interactiveElements.filter(e => e.id !== String(Number(e.id)));
        const linkElementIds = new Set(linkElements.map(e => e.id));
        result.linksWithoutShape = viewData.links.filter(link => !link.delete && !linkElementIds.has(link.ref_id));
        return result.linksWithoutShape.length > 0;
    }

    #linksWithoutModelCheck(
        interactiveElements: Element[], viewData: VenueTplEditorViewData,
        result: VenueTplEditorConsistencyCheckResult
    ): boolean {
        const viewLinks = new Set(viewData.links.filter(link => !link.delete).map(link => link.ref_id));
        result.linksWithoutModel = interactiveElements.filter(e => e.id !== String(Number(e.id)))
            .filter(linkElement => !viewLinks.has(linkElement.id))
            .map(linkElement => linkElement as SVGElement);
        return result.linksWithoutModel.length > 0;
    }

    #zonesWithoutShapeCheck(
        interactiveElements: Element[], viewData: VenueTplEditorViewData, items: EdVenueMapMaps,
        result: VenueTplEditorConsistencyCheckResult
    ): boolean {
        const nnzElements = interactiveElements.filter(e => e.tagName === SVGDefs.nodeTypes.nnz && e.id === String(Number(e.id)));
        const nnzElementIds = new Set(nnzElements.map(e => Number(e.id)));
        result.zonesWithoutShape = Array.from(items.nnzs.values())
            .filter(nnz => !nnz.delete && nnz.view === viewData.view.id)
            .filter(nnz => !nnzElementIds.has(nnz.id));
        return result.zonesWithoutShape.length > 0;
    }

    #seatsWithoutShapeCheck(
        interactiveElements: Element[], viewData: VenueTplEditorViewData, items: EdVenueMapMaps,
        result: VenueTplEditorConsistencyCheckResult
    ): boolean {
        const seatElements = interactiveElements.filter(e => e.tagName === SVGDefs.nodeTypes.seat && e.id === String(Number(e.id)));
        const seatElementIds = new Set(seatElements.map(e => Number(e.id)));
        result.seatsWithoutShape = Array.from(items.seats.values())
            .filter(seat => !seat.delete && seat.view === viewData.view.id)
            .filter(seat => !seatElementIds.has(seat.id));
        return result.seatsWithoutShape.length > 0;
    }

    #zonesWithoutModelCheck(
        interactiveElements: Element[], items: EdVenueMapMaps,
        result: VenueTplEditorConsistencyCheckResult
    ): boolean {
        result.zonesWithoutModel = interactiveElements
            .filter(e => e.tagName === SVGDefs.nodeTypes.nnz && e.id === String(Number(e.id)))
            .filter(e => !items.nnzs?.has(Number(e.id)) || items.nnzs.get(Number(e.id)).delete)
            .map(linkElement => linkElement as SVGElement);
        return result.zonesWithoutModel.length > 0;
    }

    #seatsWithoutModelCheck(
        interactiveElements: Element[], items: EdVenueMapMaps,
        result: VenueTplEditorConsistencyCheckResult
    ): boolean {
        result.seatsWithoutModel = interactiveElements
            .filter(e => e.tagName === SVGDefs.nodeTypes.seat && e.id === String(Number(e.id)))
            .filter(e => !items.seats?.has(Number(e.id)) || items.seats.get(Number(e.id)).delete)
            .map(linkElement => linkElement as SVGElement);
        return result.seatsWithoutModel.length > 0;
    }

    // INCONSISTENCIES PROCESSING

    #processInconsistencies(consistencyCheckResult: VenueTplEditorConsistencyCheckResult): void {
        const actions = [
            this.#processLinksWithoutShape(consistencyCheckResult.linksWithoutShape),
            this.#processLinksWithoutModel(consistencyCheckResult.linksWithoutModel),
            this.#processZonesWithoutShape(consistencyCheckResult.zonesWithoutShape),
            this.#processZonesWithoutModel(consistencyCheckResult.zonesWithoutModel),
            this.#processSeatsWithoutShape(consistencyCheckResult.seatsWithoutShape),
            this.#processSeatsWithoutModel(consistencyCheckResult.seatsWithoutModel)
        ];
        concat(...actions)
            .pipe(bufferCount(actions.length))
            .subscribe(() => {
                this.#results.next({ svgRefreshRequired: this.#svgModified });
                this.#results.complete();
            });
    }

    // LINKS FIX USER DECISIONS, DIALOGS

    #processLinksWithoutShape(links: VenueTplEditorViewLink[]): Observable<void> {
        if (links?.length) {
            console.warn('links without shape detected', links);
            return this.#msgDialogSrv.showWarn({
                size: DialogSize.LARGE,
                title: 'VENUE_TPL_EDITOR.FORMS.INFOS.LINKS_WITHOUT_SHAPE_WARNING_TITLE',
                message: 'VENUE_TPL_EDITOR.FORMS.INFOS.LINKS_WITHOUT_SHAPE_WARNING',
                actionLabel: 'VENUE_TPL_EDITOR.ACTIONS.CREATE_LINKS',
                showSecondaryButton: true,
                secondaryActionLabel: 'VENUE_TPL_EDITOR.ACTIONS.DELETE_LINKS'
            })
                .pipe(map(result => {
                    if (result === 'secondary') {
                        this.#results.next({ linksToDelete: links });
                    } else if (result) {
                        this.#recreateInteractiveShapes(links.map(link => link.ref_id));
                    }
                    return null;
                }));
        } else {
            return of(null);
        }
    }

    #processLinksWithoutModel(shapes: SVGElement[]): Observable<void> {
        return this.#processShapesWithoutModel(
            shapes,
            {
                title: 'VENUE_TPL_EDITOR.FORMS.INFOS.LINKS_WITHOUT_MODEL_ERROR_TITLE',
                message: 'VENUE_TPL_EDITOR.FORMS.INFOS.LINKS_WITHOUT_MODEL_ERROR'
            }
        );
    }

    // ZONES FIX USER DECISIONS, DIALOGS

    #processZonesWithoutShape(zones: EdNotNumberedZone[]): Observable<void> {
        if (zones?.length) {
            console.warn('zones without shape detected', zones);
            return this.#msgDialogSrv.showWarn({
                size: this.#inUse ? DialogSize.MEDIUM : DialogSize.LARGE,
                title: 'VENUE_TPL_EDITOR.FORMS.INFOS.ZONES_WITHOUT_SHAPE_WARNING_TITLE',
                message: 'VENUE_TPL_EDITOR.FORMS.INFOS.ZONES_WITHOUT_SHAPE_WARNING',
                actionLabel: 'VENUE_TPL_EDITOR.ACTIONS.CREATE_ZONES',
                showSecondaryButton: !this.#inUse,
                secondaryActionLabel: !this.#inUse ? 'VENUE_TPL_EDITOR.ACTIONS.DELETE_ZONES' : undefined
            })
                .pipe(map(result => {
                    if (result === 'secondary') {
                        this.#results.next({ zonesToDelete: zones });
                    } else if (result) {
                        this.#recreateInteractiveShapes(zones.map(zone => String(zone.id)));
                    }
                    return null;
                }));
        } else {
            return of(null);
        }
    }

    #processZonesWithoutModel(shapes: SVGElement[]): Observable<void> {
        return this.#processShapesWithoutModel(
            shapes,
            {
                title: 'VENUE_TPL_EDITOR.FORMS.INFOS.ZONES_WITHOUT_MODEL_ERROR_TITLE',
                message: 'VENUE_TPL_EDITOR.FORMS.INFOS.ZONES_WITHOUT_MODEL_ERROR'
            }
        );
    }

    // SEATS FIX USER DECISIONS, DIALOGS

    #processSeatsWithoutShape(seats: EdSeat[]): Observable<void> {
        if (seats?.length) {
            console.warn('seats without shape detected', seats);
            return this.#msgDialogSrv.showWarn({
                size: this.#inUse ? DialogSize.MEDIUM : DialogSize.LARGE,
                title: 'VENUE_TPL_EDITOR.FORMS.INFOS.SEATS_WITHOUT_SHAPE_WARNING_TITLE',
                message: 'VENUE_TPL_EDITOR.FORMS.INFOS.SEATS_WITHOUT_SHAPE_WARNING',
                actionLabel: 'VENUE_TPL_EDITOR.ACTIONS.CREATE_SEATS',
                showSecondaryButton: !this.#inUse,
                secondaryActionLabel: !this.#inUse ? 'VENUE_TPL_EDITOR.ACTIONS.DELETE_SEATS' : undefined
            })
                .pipe(map(result => {
                    if (result === 'secondary') {
                        this.#results.next({ seatsToDelete: seats });
                    } else if (result) {
                        this.#recreateSeats(seats);
                    }
                    return null;
                }));
        } else {
            return of(null);
        }
    }

    #processSeatsWithoutModel(seatsShapes: SVGElement[]): Observable<void> {
        if (seatsShapes?.length) {
            console.warn('seats without model detected', seatsShapes);
            seatsShapes.forEach(shape => shape.parentNode.removeChild(shape));
            this.#addSVGModifiedResult();
            // Yes, it's a "fake" warning, it doesn't give any choice, app warns about what has been done, without giving any choice
            this.#msgDialogSrv.showWarn({
                size: DialogSize.MEDIUM,
                title: 'VENUE_TPL_EDITOR.FORMS.INFOS.SEATS_WITHOUT_MODEL_ERROR_TITLE',
                message: 'VENUE_TPL_EDITOR.FORMS.INFOS.SEATS_WITHOUT_MODEL_ERROR',
                showCancelButton: false
            });
        }
        return of(null);
    }

    // FIX ACTIONS

    #processShapesWithoutModel(shapes: SVGElement[], dialogConf: MessageDialogConfig): Observable<void> {
        if (shapes?.length) {
            console.warn('links or nnzs without model detected', shapes);
            shapes.forEach(shape => {
                shape.removeAttribute(SVGDefs.attributes.id);
                shape.classList.remove(SVGDefs.classes.interactive);
            });
            this.#addSVGModifiedResult();
            // Yes, it's a "fake" warning, it doesn't give any choice, app warns about what has been done, without giving any choice
            this.#msgDialogSrv.showWarn({
                ...dialogConf,
                size: DialogSize.MEDIUM,
                showCancelButton: false
            });
        }
        return of(null);
    }

    #recreateInteractiveShapes(ids: string[]): void {
        ids.forEach((id, index) => {
            this.#initRelativeSizes();
            const rect = new Rect();
            rect.width(this.#newRectSize);
            rect.height(this.#newRectSize);
            rect.fill(SVGDefs.colors.defaultFill);
            const group = new G();
            group.add(rect);
            group.move(this.#viewBox.width, this.#margin + (index * (this.#newRectSize + this.#margin)));
            group.addClass(SVGDefs.classes.interactive);
            group.id(id);
            this.#svg.appendChild(group.node);
        });
        this.#updateViewBox(
            this.#viewBox.width + this.#newRectSize + this.#margin,
            Math.max(this.#viewBox.height, this.#margin + (this.#newRectSize + this.#margin) * ids.length)
        );
        this.#addSVGModifiedResult();
    }

    #recreateSeats(seats: EdSeat[]): void {
        this.#initRelativeSizes();
        const seatRadius = this.#defineSeatRadius();
        // This sets a distance between circles equal to the diameter of these,
        // but the distance includes the radius of each circle because it's measured from the center of the circles,
        // then, the result is equal to the radius of the first circle, plus the diameter (2 x radius), plus the radius of the second circle
        // seatRadius + seatRadius * 2 + seatRadius = 4 * seatRadius
        const seatDistance = 4 * seatRadius;
        const initPx = this.#viewBox.width + seatRadius;
        const initPy = this.#margin + seatRadius;
        let seatIndex = 0;
        let rowIndex = 0;
        let prevSeat: EdSeat;
        let circle: Circle;
        let maxSeatIndex = 0;
        this.#getSortedSeats(seats).forEach(seat => {
            if (prevSeat && seat.rowId !== prevSeat.rowId) {
                seatIndex = 0;
                rowIndex++;
            } else {
                maxSeatIndex = Math.max(maxSeatIndex, seatIndex);
            }
            circle = new Circle({
                id: String(seat.id),
                r: seatRadius,
                cx: initPx + seatIndex * seatDistance,
                cy: initPy + rowIndex * seatDistance
            });
            circle.addClass(SVGDefs.classes.interactive);
            this.#svg.appendChild(circle.node);
            seatIndex++;
            prevSeat = seat;
        });
        this.#updateViewBox(
            initPx + maxSeatIndex * seatDistance + seatRadius + this.#margin,
            Math.max(this.#viewBox.height, initPy + rowIndex * seatDistance + seatRadius + this.#margin)
        );
        this.#addSVGModifiedResult();
    }

    #addSVGModifiedResult(): void {
        this.#svgModified = true;
        this.#results.next({ svgModified: true });
    }

    #updateViewBox(width: number, height: number): void {
        this.#viewBox = { width, height };
        this.#svg.viewBox.baseVal.width = width;
        this.#svg.viewBox.baseVal.height = height;
    }

    #defineSeatRadius(): number {
        const sampleSeatElement = Array.from(this.#svg.children).find(shape =>
            shape.tagName === SVGDefs.nodeTypes.seat
            && shape.classList.contains(SVGDefs.classes.interactive)
            && shape.id === String(Number(shape.id))
        ) as SVGCircleElement;
        return sampleSeatElement?.r.baseVal.value ?
            sampleSeatElement.r.baseVal.value
            : (venueTplEditorSeatMatrixConfInitValue.matrix.seatsSize / 2);
    }

    #getSortedSeats(seats: EdSeat[]): EdSeat[] {
        return seats.concat().sort((a, b) => {
            const aRow = this.#venueItems.rows.get(a.rowId);
            const bRow = this.#venueItems.rows.get(b.rowId);
            const aSector = this.#venueItems.sectors.get(aRow.sector);
            const bSector = this.#venueItems.sectors.get(bRow.sector);
            if (aSector !== bSector) {
                return aSector.order - bSector.order;
            } else if (aRow !== bRow) {
                return aRow.order - bRow.order;
            } else {
                return a.order - b.order;
            }
        });
    }

    #initRelativeSizes(): void {
        if (!this.#newRectSize) {
            this.#newRectSize = Math.round(Math.min(this.#viewBox.width, this.#viewBox.height) / 5);
            this.#margin = Math.round(this.#newRectSize / 5);
        }
    }
}
