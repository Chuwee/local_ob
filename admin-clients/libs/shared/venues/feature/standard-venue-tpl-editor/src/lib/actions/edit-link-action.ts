import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { IdGenerator } from '../utils/editor-id-generator.utils';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../venue-tpl-editor.service';

export class EditLinkAction extends VenueTplEditorBaseSvgAction {

    private _newLinkId: number;
    private _newLinkRef: string;

    private _svgViewId: number;
    private _svgViewPrevModify: boolean;
    private _prevSvg: string;
    private _newSvg: string;
    private _linkElementIndex: number[];

    constructor(
        private _oldLinkId: number,
        private _viewId: number,
        private _editorSrv: VenueTplEditorService,
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        combineLatest([
            this.viewsSrv.getViewDatas$(),
            this.viewsSrv.getSvgData$(),
            this.domSrv.getSvgSvgElement$(),
            this.selectionSrv.getSelection$()
        ])
            .pipe(take(1))
            .subscribe(([viewDatas, svgData, mainSVGElement, selection]) => {
                this._linkElementIndex = this.getElementsIndexes(mainSVGElement, selection.elements);
                this._newLinkId = IdGenerator.getTempId();
                this._newLinkRef = IdGenerator.getAlphanumericTempId();
                this._prevSvg = svgData.svg;
                this._svgViewPrevModify = !!svgData.modify;
                const oldLink = viewDatas.flatMap(vd => vd.links).find(link => link.id === this._oldLinkId);
                const linkViewData = viewDatas.find(viewData => viewData.links.includes(oldLink));
                this._svgViewId = linkViewData.view.id;
                const element = Array.from(mainSVGElement.children).find(child => child.id === oldLink.ref_id);
                element.id = this._newLinkRef;
                this._newSvg = this.parseSVG(mainSVGElement);
                if (this.isReady) {
                    this.do(VenueTplEditorSvgTriggerType.DOMChange);
                }
            });
    }

    protected do(svgTriggerType = VenueTplEditorSvgTriggerType.textChange): void {
        this.viewsSrv.editLinkView(this._oldLinkId, this._newLinkId, this._newLinkRef, this._viewId);
        this.viewsSrv.changeSvg(this._svgViewId, this._newSvg, { changer: svgTriggerType });
        this.selectByIndexes(this._linkElementIndex);
    }

    protected undo(): void {
        this.viewsSrv.editLinkView(this._oldLinkId, this._newLinkId, null, null, true);
        this.viewsSrv.changeSvg(this._svgViewId, this._prevSvg, { resultModify: this._svgViewPrevModify });
        this.selectByIndexes(this._linkElementIndex);
    }
}
