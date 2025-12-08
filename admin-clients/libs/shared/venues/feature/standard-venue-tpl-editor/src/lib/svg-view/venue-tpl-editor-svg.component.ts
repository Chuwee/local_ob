import { MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { ResizeObserverDirective } from '@admin-clients/shared/utility/directives';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, inject, viewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { combineLatestWith, first, Observable, pairwise, startWith, switchMap } from 'rxjs';
import { debounceTime, filter, map, take, tap } from 'rxjs/operators';
import { DeleteItemsAction } from '../actions/delete-items-action';
import { DeleteLinksAction } from '../actions/delete-links-action';
import { EditSvgAction } from '../actions/edit-svg-action';
import { EditorMode, InteractionMode, VisualizationMode } from '../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorSvgTriggerType } from '../models/venue-tpl-editor-svg-trigger-type.enum';
import { VenueTplEditorDomService } from '../venue-tpl-editor-dom.service';
import { VenueTplEditorImagesService } from '../venue-tpl-editor-images.service';
import { VenueTplEditorSaveService } from '../venue-tpl-editor-save.service';
import { VenueTplEditorSelectionService } from '../venue-tpl-editor-selection.service';
import { VenueTplEditorTplCorrectionsManager } from '../venue-tpl-editor-tpl-corrections.manager';
import { VenueTplEditorVenueMapService } from '../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../venue-tpl-editor.service';
import { VenueTplEditorBlocksInfoLayerComponent } from './info-layers/blocks-info-layer/venue-tpl-editor-blocks-info-layer.component';
import { VenueTplEditorNamesInfoLayerComponent } from './info-layers/names-info-layer/venue-tpl-editor-names-info-layer.component';
import { VenueTplEditorWeightsInfoLayerComponent } from './info-layers/weights-info-layer/venue-tpl-editor-weights-info-layer.component';
import { VenueTplEditorBlocksSetupComponent } from './modifiers/blocks-setup/venue-tpl-editor-blocks-setup.component';
import { VenueTplEditorSeatMatrixComponent } from './modifiers/seat-matrix/venue-tpl-editor-seat-matrix.component';
import { VenueTplEditorSvgTransformsComponent } from './modifiers/svg-transforms/venue-tpl-editor-svg-transforms.component';
import { VenueTplEditorSvgModifier } from './modifiers/venue-tpl-editor-svg-modifier';
import { VenueTplEditorWeightsSetupComponent } from './modifiers/weights-setup/venue-tpl-editor-weights-setup.component';
import { VenueTplEditorZoomButtonsComponent } from './zoom/venue-tpl-editor-zoom-buttons.component';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        ResizeObserverDirective,
        VenueTplEditorZoomButtonsComponent,
        VenueTplEditorSvgTransformsComponent,
        VenueTplEditorBlocksInfoLayerComponent,
        VenueTplEditorWeightsInfoLayerComponent,
        VenueTplEditorBlocksSetupComponent,
        VenueTplEditorWeightsSetupComponent,
        VenueTplEditorSeatMatrixComponent,
        VenueTplEditorNamesInfoLayerComponent
    ],
    selector: 'app-venue-tpl-editor-svg',
    templateUrl: './venue-tpl-editor-svg.component.html',
    styleUrls: ['./venue-tpl-editor-svg.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSvgComponent {
    // DEPS
    readonly #domSanitizer = inject(DomSanitizer);
    readonly #viewsSrv = inject(VenueTplEditorViewsService);
    readonly #domSrv = inject(VenueTplEditorDomService);
    readonly #venueMapSrv = inject(VenueTplEditorVenueMapService);
    readonly #imagesSrv = inject(VenueTplEditorImagesService);
    readonly #editorSrv = inject(VenueTplEditorService);
    readonly #selectionSrv = inject(VenueTplEditorSelectionService);
    readonly #saveSrv = inject(VenueTplEditorSaveService);
    readonly #msgDialogSrv = inject(MessageDialogService);

    private readonly _$svgContainer = viewChild<ElementRef<HTMLDivElement>>('svgContainer');

    readonly $currentModifier = viewChild<VenueTplEditorSvgModifier>('modifier');

    // ENUMS
    readonly interactionMode = InteractionMode;
    readonly modes = EditorMode;
    readonly visualizationModes = VisualizationMode;
    // svg load and set, so crazy
    readonly safeSvg$: Observable<SafeHtml> = this.#viewsSrv.getViewData$()
        .pipe(
            startWith(null),
            pairwise(),
            filter(([prevViewData, viewData]) => viewData && viewData !== prevViewData),
            map(([_, viewData]) => viewData),
            filter(Boolean),
            tap(viewData => {
                this.#selectionSrv.unselectAll();
                this.#viewsSrv.loadSvgData(viewData);
            }),
            switchMap(() => this.#viewsSrv.getSvgData$()),
            filter(svgData => svgData?.triggerType !== VenueTplEditorSvgTriggerType.DOMChange),
            debounceTime(0),
            map(svgData => (svgData ? this.#domSanitizer.bypassSecurityTrustHtml(svgData.svg ?? '') : null)),
            tap(svg => {
                this.#domSrv.setSvgSvgElement(null);
                if (svg) {
                    this.#waitForDOMElement();
                }
            })
        );

    // work area coordinates, defined by zoom component
    readonly workAreaCoordinates$ = this.#domSrv.getWorkAreaCoordinates$()
        .pipe(
            map(coords => ({
                width: coords?.width ? Math.ceil(coords.width) + 'px' : 'unset',
                height: coords?.height ? Math.ceil(coords.height) + 'px' : 'unset',
                top: coords?.top ? Math.ceil(coords.top) + 'px' : 'unset',
                left: coords?.left ? Math.ceil(coords.left) + 'px' : 'unset'
            }))
        );

    // other screen conditions
    readonly interactionMode$ = this.#editorSrv.modes.getInteractionMode$();
    readonly mode$ = this.#editorSrv.modes.getEditorMode$();
    readonly selectedVisualizationModes$ = this.#editorSrv.modes.getVisualizationModes$().pipe(map(modes => modes?.length ? modes : null));

    #waitForDOMElement(): void {
        if (this._$svgContainer()) {
            const mutationObserver = new MutationObserver(() => {
                const svgSvgElement = Array.from(this._$svgContainer()?.nativeElement?.childNodes)
                    ?.find(node => node instanceof SVGSVGElement);
                this.#domSrv.setSvgSvgElement(svgSvgElement);
                if (svgSvgElement) {
                    this.#editorSrv.modes.setEditorMode(EditorMode.base);
                }
                mutationObserver.disconnect();
                this.#checkViewConsistency();
            });
            mutationObserver.observe(this._$svgContainer().nativeElement, { subtree: true, childList: true, attributes: true });
        }
    }

    #checkViewConsistency(): void {
        booleanOrMerge([
            this.#viewsSrv.isViewsLoading$(),
            this.#viewsSrv.isSVGDataLoading$(),
            this.#venueMapSrv.isVenueMapLoading$(),
            this.#imagesSrv.isInProgress$()
        ])
            .pipe(
                first(loading => !loading),
                switchMap(() => this.#saveSrv.isSaving$()),
                take(1),
                filter(saving => !saving),
                combineLatestWith(
                    this.#domSrv.getSvgSvgElement$(),
                    this.#viewsSrv.getViewData$(),
                    this.#domSrv.getSvgSvgElementViewBox$(),
                    this.#venueMapSrv.getVenueItems$(),
                    this.#editorSrv.inUse.get$()
                ),
                first(([_, viewSvg, viewData, viewBox, venueItems]) => !!(viewSvg && viewData && viewBox && venueItems)),
                switchMap(([_, viewSvg, viewData, viewBox, venueItems, inUse]) =>
                    new VenueTplEditorTplCorrectionsManager({ viewSvg, viewData, viewBox, venueItems, inUse }, this.#msgDialogSrv).results$
                )
            )
            .subscribe(result => {
                if (result.svgModified) {
                    this.#editorSrv.history.enqueueFix(
                        new EditSvgAction(
                            this.#viewsSrv, this.#domSrv, this.#selectionSrv, { changer: VenueTplEditorSvgTriggerType.DOMChange }
                        )
                    );
                }
                if (result.linksToDelete?.length) {
                    this.#editorSrv.history.enqueueFix(
                        new DeleteLinksAction(result.linksToDelete.map(link => link.id), this.#viewsSrv, this.#domSrv, this.#selectionSrv)
                    );
                }
                if (result.zonesToDelete?.length || result.seatsToDelete?.length) {
                    this.#editorSrv.history.enqueueFix(
                        new DeleteItemsAction({
                            nnzIds: result.zonesToDelete?.map(zone => zone.id) ?? undefined,
                            seatIds: result.seatsToDelete?.map(seat => seat.id) ?? undefined
                        }, this.#venueMapSrv, this.#viewsSrv, this.#domSrv, this.#selectionSrv)
                    );
                }
                if (result.svgRefreshRequired) {
                    this.#viewsSrv.refreshSvgData();
                }
            });
    }
}
