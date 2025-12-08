import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class EditSvgAction extends VenueTplEditorBaseSvgAction {

    #viewId: number;
    #prevSVG: string;
    #prevModify: boolean;
    #newSVG: string;
    #selectedIndexes: number[];

    constructor(
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService,
        options?: {
            svg?: SVGSVGElement;
            changer?: VenueTplEditorSvgTriggerType; // text change if undefined
            elementsToSelect?: Element[];
        }
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        combineLatest([
            this.viewsSrv.getSvgData$(),
            this.domSrv.getSvgSvgElement$()
        ])
            .pipe(take(1))
            .subscribe(([svgData, mainSVG]) => {
                this.#selectedIndexes = this.getElementsIndexes(mainSVG, options?.elementsToSelect);
                this.#viewId = svgData.viewId;
                this.#prevSVG = svgData.svg;
                this.#prevModify = !!svgData.modify;
                this.#newSVG = this.parseSVG(options?.svg || mainSVG);
                if (this.isReady) {
                    this.do(options?.changer);
                }
            });
    }

    protected do(changer?: VenueTplEditorSvgTriggerType): void {
        this.viewsSrv.changeSvg(this.#viewId, this.#newSVG, { changer });
        this.selectByIndexes(this.#selectedIndexes);
    }

    protected undo(): void {
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSVG, { resultModify: this.#prevModify });
        this.selectByIndexes(this.#selectedIndexes);
    }
}
