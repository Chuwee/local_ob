import { VenueTemplateViewLink } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { combineLatest, of } from 'rxjs';
import { take } from 'rxjs/operators';
import { SVGDefs } from '../models/SVGDefs.enum';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorImage } from '../models/venue-tpl-editor-image.model';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { EdNotNumberedZone, EdSeat, EdSector } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorImagesService } from '../venue-tpl-editor-images.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class DeleteItemsAction extends VenueTplEditorBaseSvgAction {

    #viewId: number;
    #prevSVG: string;
    #prevSVGModify: boolean;
    #newSVG: string;
    #sectors: EdSector[];
    #seats: EdSeat[];
    #nnzs: EdNotNumberedZone[];
    #links: VenueTemplateViewLink[];
    #images: VenueTplEditorImage[];
    #prevSelectedIndexes: number[];
    #nextSelectedIndexes: number[];

    constructor(
        deleteLists: {
            sectorIds?: number[];
            seatIds?: number[];
            nnzIds?: number[];
            elements?: SVGElement[];
        },
        private readonly _mapSrv: VenueTplEditorVenueMapService,
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService,
        private readonly _imagesSrv?: VenueTplEditorImagesService
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        deleteLists.sectorIds = deleteLists.sectorIds || [];
        deleteLists.seatIds = deleteLists.seatIds || [];
        deleteLists.nnzIds = deleteLists.nnzIds || [];
        deleteLists.elements = deleteLists.elements || [];
        combineLatest([
            this.viewsSrv.getViewData$(),
            this.viewsSrv.getSvgData$(),
            this.domSrv.getSvgSvgElement$(),
            this._mapSrv.getVenueItems$(),
            this._imagesSrv?.getImages$() || of([] as VenueTplEditorImage[]),
            this.selectionSrv.getSelection$()
        ])
            .pipe(take(1))
            .subscribe(([viewData, svgData, mainSVGElement, items, images, selection]) => {
                this.#viewId = svgData.viewId;
                this.#prevSVG = svgData.svg;
                this.#prevSVGModify = !!svgData.modify;
                // 0. Saves selected indexes
                this.#prevSelectedIndexes = [];
                this.#prevSelectedIndexes.push(...this.getElementsIndexes(mainSVGElement, selection.elements));
                const interactiveElements
                    = Array.from(mainSVGElement.children).filter(child => child.classList.contains(SVGDefs.classes.interactive));
                this.#prevSelectedIndexes.push(...this.getElementsIndexes(mainSVGElement,
                    Array.from(selection.seats)
                        .map(seatId => String(seatId))
                        .map(seatId => interactiveElements.find(child => child.id === seatId))
                ));
                this.#prevSelectedIndexes.push(...this.getElementsIndexes(mainSVGElement,
                    Array.from(selection.nnzs)
                        .map(nnzId => String(nnzId))
                        .map(nnzId => interactiveElements.find(child => child.id === nnzId))
                ));
                this.#nextSelectedIndexes = [];
                // 1. Non interactive elements remove from svg, before links removing because after deletion, a link is a normal element.
                deleteLists.elements
                    .filter(el => !el.classList.contains(SVGDefs.classes.interactive) || el.tagName === SVGDefs.nodeTypes.rowLabel)
                    .forEach(el => el.remove());
                // 2. Sectors memorize
                this.#sectors = deleteLists.sectorIds.map(sectorId => items.sectors.get(sectorId));
                // 3. Seats
                // memorize, plus sectors seats
                this.#seats = deleteLists.seatIds.map(seatId => items.seats.get(seatId));
                this.#seats.push(
                    ...this.#sectors
                        .flatMap(sector => sector.rows)
                        .filter(row => !row.delete)
                        .flatMap(row => row.seats)
                        .filter(seat => !seat.delete)
                        .filter(seat => !this.#seats.includes(seat))
                );
                // seats svg remove
                this.#seats.forEach(seat => {
                    const stringId = seat.id.toString();
                    Array.from(mainSVGElement.children)
                        .find(child => child.id === stringId && child.classList.contains(SVGDefs.classes.interactive))
                        ?.remove();
                });
                // 4. Not numbered zones
                this.#nnzs = deleteLists.nnzIds.map(nnzId => items.nnzs.get(nnzId));
                this.#nnzs.push(
                    ...this.#sectors
                        .flatMap(sector => sector.notNumberedZones)
                        .filter(nnz => !!nnz || !nnz.delete)
                        .filter(nnz => !this.#nnzs.includes(nnz))
                );
                this.#nextSelectedIndexes.push(
                    ...this.getElementsIndexes(
                        mainSVGElement,
                        this.#nnzs.map(nnz => this.convertToSimpleGroup(nnz.id.toString(), interactiveElements))
                    )
                );
                // 5. Links
                this.#links = deleteLists.elements.filter(el => el.id && el.classList.contains(SVGDefs.classes.interactive))
                    .map(element => viewData.links.find(link => link.ref_id === element.id));
                this.#nextSelectedIndexes.push(
                    ...this.getElementsIndexes(
                        mainSVGElement,
                        this.#links.map(link => this.convertToSimpleGroup(link.ref_id, interactiveElements))
                    )
                );
                // image deletion
                this.#images = deleteLists.elements
                    .filter(element => element instanceof SVGImageElement)
                    .map(imageElement => {
                        const imageUrl = imageElement.getAttribute('href') || imageElement.getAttribute('xlink:href');
                        return images.find(image => image.url === imageUrl);
                    })
                    .filter(Boolean);
                // 6. final svg parsing
                this.#newSVG = this.parseSVG(mainSVGElement);
                // 7. action commit
                if (this.isReady) {
                    this.do(VenueTplEditorSvgTriggerType.DOMChange);
                }
            });
    }

    protected do(svgChange?: VenueTplEditorSvgTriggerType): void {
        this.applyChanges(this.#newSVG, svgChange);
        this.selectByIndexes(this.#nextSelectedIndexes);
    }

    protected undo(): void {
        this.applyChanges(this.#prevSVG, VenueTplEditorSvgTriggerType.textChange, true, this.#prevSVGModify);
        this.selectByIndexes(this.#prevSelectedIndexes);
    }

    private convertToSimpleGroup(id: string, interactiveElements: Element[]): Element {
        const element = interactiveElements.find(child => child.id === id);
        if (element) {
            element.classList.remove(SVGDefs.classes.interactive);
            element.removeAttribute(SVGDefs.attributes.id);
        }
        return element;
    }

    private applyChanges(svg: string, changer: VenueTplEditorSvgTriggerType, undo = false, resultModify = true): void {
        if (this.#seats.length) {
            this._mapSrv.deleteSeats(this.#seats, undo);
        }
        if (this.#nnzs.length) {
            this._mapSrv.deleteNNZs(this.#nnzs, undo);
        }
        if (this.#sectors.length) {
            this.#sectors.forEach(sector => this._mapSrv.deleteSector(sector, undo));
        }
        if (this.#links.length) {
            this.#links.forEach(link => this.viewsSrv.deleteLink(link.id, undo));
        }
        if (this.#images.length) {
            this.#images.forEach(image => this._imagesSrv.deleteImage(image, undo));
        }
        this.viewsSrv.changeSvg(this.#viewId, svg, { changer, resultModify });
    }
}
