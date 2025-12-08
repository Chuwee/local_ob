import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTplEditorBaseAction } from '../models/actions/venue-tpl-editor-base-action';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class EditSvgViewBoxAction extends VenueTplEditorBaseSvgAction {

    #viewId: number;
    #newSVG: string;
    #prevSVG: string;
    #prevModify: boolean;

    get itemId(): number {
        return this.#viewId;
    }

    constructor(
        { width, height }: { width?: number; height?: number },
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService
    ) {
        super(domSrv, viewsSrv, null);
        combineLatest([
            this.viewsSrv.getSvgData$(),
            this.domSrv.getSvgSvgElement$()
        ])
            .pipe(take(1))
            .subscribe(([svgData, mainSVGElement]) => {
                this.#viewId = svgData.viewId;
                this.#prevSVG = svgData.svg;
                this.#prevModify = !!svgData.modify;
                mainSVGElement.setAttribute('viewBox', `0 0 ${Math.abs(Math.round(width))} ${Math.abs(Math.round(height))}`);
                this.#newSVG = this.parseSVG(mainSVGElement);
                if (this.isReady) {
                    this.do();
                }
            });
    }

    canCombineWith(action: VenueTplEditorBaseAction): boolean {
        return action instanceof EditSvgViewBoxAction && this.itemId === action.itemId;
    }

    protected do(): void {
        this.viewsSrv.changeSvg(this.#viewId, this.#newSVG);
    }

    protected undo(): void {
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSVG, {  resultModify: this.#prevModify });
    }
}
