import { combineLatest } from 'rxjs';
import { map, take } from 'rxjs/operators';
import { SVGDefs } from '../models/SVGDefs.enum';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class NewNnzAction extends VenueTplEditorBaseSvgAction {

    readonly #venueMapSrv: VenueTplEditorVenueMapService;
    readonly #nnzData: {
        id: number;
        name: string;
        sector: number;
        capacity: number;
    };

    #viewId: number;
    #prevSVG: string;
    #prevSVGModify: boolean;
    #newSVG: string;
    #prevSelection: number[];
    #zoneIndex: number[];

    constructor(
        nnzData: {
            id: number;
            name: string;
            sector: number;
            capacity: number;
        },
        venueMapSrv: VenueTplEditorVenueMapService,
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        this.#venueMapSrv = venueMapSrv;
        this.#nnzData = nnzData;
        combineLatest([
            this.viewsSrv.getViewData$(),
            this.viewsSrv.getSvgData$(),
            this.domSrv.getSvgSvgElement$(),
            this.selectionSrv.getSelection$().pipe(map(selection => selection.elements))
        ])
            .pipe(take(1))
            .subscribe(([viewData, svgData, mainSVGElement, elements]) => {
                this.#prevSelection = this.getElementsIndexes(mainSVGElement, elements);
                // prev svg data
                this.#prevSVG = svgData.svg;
                this.#prevSVGModify = !!svgData.modify;
                // nnz data
                this.#viewId = viewData.view.id;
                // svg changes
                const group = this.domSrv.groupElements(elements);
                group.id = this.#nnzData.id.toString();
                group.classList.add(SVGDefs.classes.interactive);
                this.#zoneIndex = this.getElementsIndexes(mainSVGElement, [group]);
                this.#newSVG = this.parseSVG(mainSVGElement);
                // change
                if (this.isReady) {
                    this.do();
                }
            });
    }

    protected do(): void {
        this.#venueMapSrv.addZone({ ...this.#nnzData, view: this.#viewId });
        this.viewsSrv.changeSvg(this.#viewId, this.#newSVG);
        this.selectByIndexes(this.#zoneIndex);
    }

    protected undo(): void {
        this.#venueMapSrv.addZone({ ...this.#nnzData, view: this.#viewId }, true);
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSVG, { resultModify: this.#prevSVGModify });
        this.selectByIndexes(this.#prevSelection);
    }
}
