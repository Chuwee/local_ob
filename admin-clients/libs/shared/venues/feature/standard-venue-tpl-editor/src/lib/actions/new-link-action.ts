import { combineLatest } from 'rxjs';
import { map, take, tap } from 'rxjs/operators';
import { SVGDefs } from '../models/SVGDefs.enum';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { IdGenerator } from '../utils/editor-id-generator.utils';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class NewLinkAction extends VenueTplEditorBaseSvgAction {

    #viewId: number;
    #prevSVG: string;
    #prevSVGModify: boolean;
    #newSVG: string;
    readonly #targetViewId: number;
    #linkId: number;
    #refId: string;
    #prevSelectedIndexes: number[];
    #newSelectedIndexes: number[];

    constructor(
        viewId: number,
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        this.#targetViewId = viewId;
        combineLatest([
            this.viewsSrv.getSvgData$(),
            this.domSrv.getSvgSvgElement$(),
            this.selectionSrv.getSelection$().pipe(map(selection => selection.elements))
        ])
            .pipe(
                take(1),
                tap(([svgData, mainSVGElement, elements]) => {
                    this.#prevSelectedIndexes = this.getElementsIndexes(mainSVGElement, elements);
                    this.#linkId = IdGenerator.getTempId();
                    this.#refId = IdGenerator.getAlphanumericTempId();
                    this.#viewId = svgData.viewId;
                    this.#prevSVG = svgData.svg;
                    this.#prevSVGModify = !!svgData.modify;
                    const group = this.domSrv.groupElements(elements);
                    group.id = this.#refId;
                    group.classList.add(SVGDefs.classes.interactive);
                    this.#newSelectedIndexes = this.getElementsIndexes(mainSVGElement, [group]);
                    this.#newSVG = this.parseSVG(mainSVGElement);
                    if (this.isReady) {
                        this.do(VenueTplEditorSvgTriggerType.DOMChange);
                    }
                })
            )
            .subscribe();
    }

    protected do(changer?: VenueTplEditorSvgTriggerType): void {
        this.viewsSrv.addLink(this.#linkId, this.#refId, this.#targetViewId);
        this.viewsSrv.changeSvg(this.#viewId, this.#newSVG, { changer });
        this.selectByIndexes(this.#newSelectedIndexes);
    }

    protected undo(): void {
        this.viewsSrv.addLink(this.#linkId, this.#refId, undefined, true);
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSVG, { resultModify: this.#prevSVGModify });
        this.selectByIndexes(this.#prevSelectedIndexes);
    }
}
