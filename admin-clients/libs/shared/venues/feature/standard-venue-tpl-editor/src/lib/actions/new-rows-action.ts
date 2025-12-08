import { combineLatest } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { EdRow } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class NewRowsAction extends VenueTplEditorBaseSvgAction {

    readonly #venueMapSrv: VenueTplEditorVenueMapService;
    readonly #rows: EdRow[];
    #prevSVG: string;
    #prevSVGModify: boolean;
    #newSVG: string;
    #viewId: number;
    #selectionIndexes: number[];

    constructor(
        rows: EdRow[],
        venueMapSrv: VenueTplEditorVenueMapService,
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService,
        elements: Element[]
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        this.#venueMapSrv = venueMapSrv;
        this.#rows = rows;
        combineLatest([
            this.viewsSrv.getViewData$(),
            this.viewsSrv.getSvgData$(),
            this.domSrv.getSvgSvgElement$()
        ])
            .pipe(
                take(1),
                tap(([viewData, svgData, mainSVGElement]) => {
                    this.#viewId = viewData.view.id;
                    this.#prevSVG = svgData.svg;
                    this.#prevSVGModify = !!svgData.modify;
                    const seats = this.#rows.flatMap(row => row.seats);
                    seats.forEach(seat => seat.view = viewData.view.id);
                    const children = Array.from(mainSVGElement.children) ;
                    this.#selectionIndexes = elements.map(element => children.indexOf(element));
                    this.#newSVG = this.parseSVG(mainSVGElement);
                    if (this.isReady) {
                        this.do();
                    }
                })
            )
            .subscribe();
    }

    protected do(): void {
        this.#venueMapSrv.addRows(this.#rows);
        this.viewsSrv.changeSvg(this.#viewId, this.#newSVG);
        this.selectByIndexes(this.#selectionIndexes);
    }

    protected undo(): void {
        this.#venueMapSrv.addRows(this.#rows, true);
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSVG, { resultModify: this.#prevSVGModify });
        this.selectionSrv.unselectAll();
    }
}
