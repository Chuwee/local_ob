import { first } from 'rxjs';
import { debounceTime } from 'rxjs/operators';
import { SVGParseError, VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../../venue-tpl-editor-views.service';
import { VenueTplEditorBaseAction } from './venue-tpl-editor-base-action';

export abstract class VenueTplEditorBaseSvgAction extends VenueTplEditorBaseAction {

    protected constructor(
        protected readonly domSrv: VenueTplEditorDomService,
        protected readonly viewsSrv: VenueTplEditorViewsService,
        protected readonly selectionSrv: VenueTplEditorSelectionService
    ) {
        super();
    }

    protected parseSVG(svg: SVGSVGElement): string {
        const result = this.domSrv.parseSVG(svg);
        if (result.svg) {
            this.setReadyStatus();
            return result.svg;
        } else {
            if (result.error === SVGParseError.svgMaxLengthRaised) {
                this.status = 'svgMaxLengthRaised';
            } else {
                this.status = 'uncontrolledError';
            }
            this.viewsSrv.revertSvgChanges();
            this.selectionSrv.unselectAll();
            return null;
        }
    }

    protected getElementsIndexes(mainSVG: SVGElement, elements: Element[]): number[] {
        const children = Array.from(mainSVG.children);
        return elements
            ?.filter(Boolean)
            .map(element => children.indexOf(element))
            .filter(index => index !== -1)
            || [];
    }

    protected selectByIndexes(indexes: number[]): void {
        indexes = indexes.filter(index => index !== -1);
        if (indexes.length) {
            this.domSrv.getSvgSvgElement$()
                .pipe(
                    debounceTime(0),
                    first(Boolean)
                )
                .subscribe(mainSVGElement => {
                    this.selectionSrv.selectElements(
                        indexes.map(index =>
                            mainSVGElement.children.item(index) as SVGElement), { resetSelection: true, refreshSelectionAppearance: true }
                    );
                });
        } else {
            this.selectionSrv.unselectAll();
        }
    }
}
