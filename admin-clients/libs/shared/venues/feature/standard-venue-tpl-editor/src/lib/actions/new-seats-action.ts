import { combineLatest } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { EdSeat } from '../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class NewSeatsAction extends VenueTplEditorBaseSvgAction {

    readonly #seats: EdSeat[];
    readonly #venueMapSrv: VenueTplEditorVenueMapService;
    #viewId: number;
    #prevSVG: string;
    #prevSVGModify: boolean;
    #newSVG: string;
    #selectionIndexes: number[];

    constructor(
        seats: EdSeat[],
        venueMapSrv: VenueTplEditorVenueMapService,
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService,
        elements: Element[]
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        this.#seats = seats;
        this.#venueMapSrv = venueMapSrv;
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
                    const children = Array.from(mainSVGElement.children) ;
                    this.#selectionIndexes = elements.map(element => children.indexOf(element));
                    this.#newSVG = this.parseSVG(mainSVGElement);
                    if (this.isReady) {
                        this.do(VenueTplEditorSvgTriggerType.DOMChange);
                    }
                })
            )
            .subscribe();
    }

    protected do(changer?: VenueTplEditorSvgTriggerType): void {
        this.#venueMapSrv.addSeats(this.#seats);
        this.viewsSrv.changeSvg(this.#viewId, this.#newSVG, { changer });
        this.selectByIndexes(this.#selectionIndexes);
    }

    protected undo(): void {
        this.#venueMapSrv.addSeats(this.#seats, true);
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSVG, { resultModify: this.#prevSVGModify });
        this.selectionSrv.unselectAll();
    }
}
