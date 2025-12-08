import { withLatestFrom } from 'rxjs/operators';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class NewShapeAction extends VenueTplEditorBaseSvgAction {

    #prevSVG: string;
    #prevSVGModify: boolean;
    #viewId: number;
    #newSVG: string;
    #shapeIndex: number[];

    constructor(
        private _shapeType: 'rect' | 'text',
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        this.domSrv.addShape(this._shapeType)
            .pipe(withLatestFrom(
                this.viewsSrv.getSvgData$(),
                this.domSrv.getSvgSvgElement$()
            ))
            .subscribe(([addedElement, svgData, mainSVGElement]) => {
                this.#viewId = svgData.viewId;
                this.#prevSVG = svgData.svg;
                this.#prevSVGModify = svgData.modify;
                this.#newSVG = this.parseSVG(mainSVGElement);
                this.#shapeIndex = this.getElementsIndexes(mainSVGElement, [addedElement]);
                if (this.isReady) {
                    this.do(VenueTplEditorSvgTriggerType.DOMChange);
                }
            });
    }

    protected do(changer?: VenueTplEditorSvgTriggerType): void {
        this.viewsSrv.changeSvg(this.#viewId, this.#newSVG, { changer });
        this.selectByIndexes(this.#shapeIndex);
    }

    protected undo(): void {
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSVG, { resultModify: this.#prevSVGModify });
        this.selectionSrv.unselectAll();
    }
}
