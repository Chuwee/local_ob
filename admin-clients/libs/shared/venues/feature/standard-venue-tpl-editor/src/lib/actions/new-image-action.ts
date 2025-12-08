import { ObFileDimensions } from '@admin-clients/shared/data-access/models';
import { VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { switchMap } from 'rxjs';
import { take, tap, withLatestFrom } from 'rxjs/operators';
import { VenueTplEditorBaseSvgAction } from '../models/actions/venue-tpl-editor-base-svg-action';
import { VenueTplEditorImage } from '../models/venue-tpl-editor-image.model';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorImagesService } from '../venue-tpl-editor-images.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';

export class NewImageAction extends VenueTplEditorBaseSvgAction {

    #prevSVG: string;
    #prevSVGModify: boolean;
    #newSVG: string;
    #viewId: number;
    #imageIndex: number[];
    #image: VenueTplEditorImage;

    constructor(
        private _obFile: ObFileDimensions,
        private _venueTplSrv: VenueTemplatesService,
        private _imagesSrv: VenueTplEditorImagesService,
        viewsSrv: VenueTplEditorViewsService,
        domSrv: VenueTplEditorDomService,
        selectionSrv: VenueTplEditorSelectionService
    ) {
        super(domSrv, viewsSrv, selectionSrv);
        this._imagesSrv.getNewImageName(this._obFile.name)
            .pipe(
                tap(filename => this._obFile.name = filename),
                switchMap(() => this._venueTplSrv.venueTpl.get$()),
                take(1),
                switchMap(venueTpl => this._imagesSrv.uploadTemporaryImage(venueTpl.id, this._obFile.name, this._obFile.data)),
                withLatestFrom(this.viewsSrv.getSvgData$(), this.domSrv.getSvgSvgElement$())
            )
            .subscribe(([venueTemplateImage, svgData, mainSVGElement]) => {
                this.#image = {
                    id: venueTemplateImage.id,
                    url: venueTemplateImage.url,
                    fileName: _obFile.name,
                    data: this._obFile.data
                };
                this.domSrv.addImage(mainSVGElement, venueTemplateImage.url, this._obFile.width, this._obFile.height);
                this.#viewId = svgData.viewId;
                this.#prevSVG = svgData.svg;
                this.#prevSVGModify = !!svgData.modify;
                this.#newSVG = this.parseSVG(mainSVGElement);
                this.#imageIndex = [mainSVGElement.children.length - 1];
                if (this.isReady) {
                    this.do(VenueTplEditorSvgTriggerType.DOMChange);
                }
        });
    }

    protected do(changer?: VenueTplEditorSvgTriggerType): void {
        this._imagesSrv.addImage(this.#image);
        this.viewsSrv.changeSvg(this.#viewId, this.#newSVG, { changer });
        this.selectByIndexes(this.#imageIndex);
    }

    protected undo(): void {
        this.selectionSrv.unselectAll();
        this._imagesSrv.addImage(this.#image, true);
        this.viewsSrv.changeSvg(this.#viewId, this.#prevSVG, {  resultModify: this.#prevSVGModify });
    }
}
