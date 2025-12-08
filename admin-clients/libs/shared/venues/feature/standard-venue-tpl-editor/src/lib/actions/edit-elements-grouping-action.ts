import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class EditElementsGroupingAction extends VenueTplEditorBaseSvgAction {

    private _viewId: number;
    private _prevSVG: string;
    private _prevModify: boolean;
    private _newSVG: string;
    private _prevSelectedIndexes: number[];
    private _newSelectedIndexes: number[];

    constructor(
        group: boolean,
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        combineLatest([
            this.domSrv.getSvgSvgElement$(),
            this.viewsSrv.getSvgData$(),
            this.selectionSrv.getSelection$()
        ])
            .pipe(take(1))
            .subscribe(([mainSVGElement, svgData, selection]) => {
                this._viewId = svgData.viewId;
                this._prevSVG = svgData.svg;
                this._prevModify = !!svgData.modify;
                this._prevSelectedIndexes = this.getElementsIndexes(mainSVGElement, selection.elements);
                const results = group ? [this.domSrv.groupElements(selection.elements)] : this.domSrv.ungroupElements(selection.elements);
                this._newSelectedIndexes = this.getElementsIndexes(mainSVGElement, results);
                this._newSVG = this.parseSVG(mainSVGElement);
                if (this.isReady) {
                    this.do(VenueTplEditorSvgTriggerType.DOMChange);
                }
            });
    }

    protected do(changer?: VenueTplEditorSvgTriggerType): void {
        this.viewsSrv.changeSvg(this._viewId, this._newSVG, { changer });
        this.selectByIndexes(this._newSelectedIndexes);
    }

    protected undo(): void {
        this.viewsSrv.changeSvg(this._viewId, this._prevSVG, { resultModify: this._prevModify });
        this.selectByIndexes(this._prevSelectedIndexes);
    }
}
