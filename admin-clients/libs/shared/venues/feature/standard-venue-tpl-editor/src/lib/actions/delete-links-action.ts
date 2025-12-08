import { combineLatest } from 'rxjs';
import { take } from 'rxjs/operators';
import { SVGDefs } from '../models/SVGDefs.enum';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { VenueTplEditorViewLink } from '../models/venue-tpl-editor-view-link.model';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class DeleteLinksAction extends VenueTplEditorBaseSvgAction {

    #viewLinksToDelete: VenueTplEditorViewLink[];
    #prevSvg: string;
    #prevModifiedSVG: boolean;
    #nextSvg: string;
    #viewId: number;
    #linkIndexes: number[];

    constructor(
        linkIds: number[],
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        combineLatest([
            this.viewsSrv.getViewData$(),
            this.viewsSrv.getSvgData$(),
            this.domSrv.getSvgSvgElement$()
        ])
            .pipe(take(1))
            .subscribe(([viewData, svgData, mainSVGElement]) => {
                this.#viewLinksToDelete = viewData.links.filter(link => linkIds.includes(link.id));
                this.#prevSvg = svgData.svg;
                this.#prevModifiedSVG = !!svgData.modify;
                const linksToDelete = new Set(this.#viewLinksToDelete.map(link => link.ref_id));
                const linkElements = Array.from(mainSVGElement.children)
                    .filter(child => child.classList.contains(SVGDefs.classes.interactive) && linksToDelete.has(child.id));
                linkElements.forEach(child => {
                    child.removeAttribute('id');
                    child.classList.remove(SVGDefs.classes.interactive);
                });
                this.#linkIndexes = this.getElementsIndexes(mainSVGElement, linkElements);
                this.#viewId = viewData.view.id;
                this.#nextSvg = this.parseSVG(mainSVGElement);
                if (this.isReady) {
                    this.do(VenueTplEditorSvgTriggerType.DOMChange, true);
                }
            });
    }

    protected do(changer?: VenueTplEditorSvgTriggerType, skipSelection = false): void {
        this.#viewLinksToDelete.forEach(link => this.viewsSrv.deleteLink(link.id));
        this.viewsSrv.changeSvg(this.#viewId, this.#nextSvg, { changer });
        if (!skipSelection) {
            this.selectByIndexes(this.#linkIndexes);
        }
    }

    protected undo(): void {
        this.#viewLinksToDelete.forEach(link => this.viewsSrv.deleteLink(link.id, true));
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSvg, { resultModify: this.#prevModifiedSVG });
        this.selectByIndexes(this.#linkIndexes);
    }
}
