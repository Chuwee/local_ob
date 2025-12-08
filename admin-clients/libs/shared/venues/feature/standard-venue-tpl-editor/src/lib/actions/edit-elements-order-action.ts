import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class EditElementsOrderAction extends VenueTplEditorBaseSvgAction {

    private _viewId: number;
    private _prevSVG: string;
    private _prevModify: boolean;
    private _newSVG: string;
    private _prevSelectedIndexes: number[];
    private _newSelectedIndexes: number[];

    constructor(
        changeType: 'bottom' | 'down' | 'up' | 'top',
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
                if (changeType === 'bottom') {
                    this.domSrv.sendToBack(selection.elements);
                } else if (changeType === 'down') {
                    this.domSrv.moveDown(selection.elements);
                } else if (changeType === 'up') {
                    this.domSrv.moveUp(selection.elements);
                } else {//if (changeType === 'top') {
                    this.domSrv.sendToFront(selection.elements);
                }
                this._newSelectedIndexes = this.getElementsIndexes(mainSVGElement, selection.elements);
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
