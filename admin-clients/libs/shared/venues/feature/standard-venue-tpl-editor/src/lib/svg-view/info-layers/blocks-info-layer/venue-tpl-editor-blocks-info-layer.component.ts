import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Circle, G } from '@svgdotjs/svg.js';
import { combineLatest, switchMap } from 'rxjs';
import { debounceTime, filter, map } from 'rxjs/operators';
import { SVGDefs } from '../../../models/SVGDefs.enum';
import { VisualizationMode } from '../../../models/venue-tpl-editor-modes.enum';
import { EdSeat } from '../../../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorDomService } from '../../../venue-tpl-editor-dom.service';
import { VenueTplEditorVenueMapService } from '../../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../../venue-tpl-editor.service';

@Component({
    imports: [AsyncPipe],
    selector: 'app-venue-tpl-editor-blocks-info-layer',
    templateUrl: './venue-tpl-editor-blocks-info-layer.component.html',
    styleUrls: ['./venue-tpl-editor-blocks-info-layer.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorBlocksInfoLayerComponent {

    // row blocks uses to be between 1 and 4, indexes 0 and 3 will not be very used, but is not impossible.
    private readonly _rowBlockDashArrays = ['5 4 2 4', '', '2 4', '8 3'];
    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _domSanitizer = inject(DomSanitizer);

    readonly viewBox$ = this._domSrv.getSvgSvgElement$().pipe(
        map(mainSVGElement => mainSVGElement?.getAttribute(SVGDefs.attributes.viewBox))
    );

    readonly paintedSeats$ = this._viewsSrv.getSvgData$()
        .pipe(
            filter(Boolean),
            debounceTime(10),
            switchMap(() => combineLatest({
                items: this._venueMapSrv.getVenueItems$().pipe(filter(Boolean)),
                parentSVG: this._domSrv.getSvgSvgElement$(),
                visualizationModes: this._editorSrv.modes.getVisualizationModes$()
            })),
            map(({ items, parentSVG, visualizationModes }) => {
                const g = new G();
                if (parentSVG) {
                    const showingWeights = visualizationModes.includes(VisualizationMode.weights);
                    Array.from(parentSVG.children)
                        .filter(e => e.tagName === SVGDefs.nodeTypes.seat && e.id && e.classList.contains(SVGDefs.classes.interactive))
                        .filter(element => items.seats.has(Number(element.id)))
                        .map(element => ({ element, seat: items.seats.get(Number(element.id)) }))
                        .forEach(({ element, seat }) => g.add(this.createBlockStrokedCircle(element, seat, showingWeights)));
                }
                return this._domSanitizer.bypassSecurityTrustHtml(g.node.innerHTML);
            })
        );

    private createBlockStrokedCircle(element: Element, seat: EdSeat, showingWeights): Circle {
        const svgCircle = element.cloneNode(false) as SVGCircleElement;
        const svgSeat = new Circle(svgCircle);
        if (svgCircle.hasAttribute('style')) {
            svgCircle.removeAttribute('style');
        }
        svgCircle.setAttribute('vector-effect', 'non-scaling-stroke');
        svgSeat.stroke({
            color: '#000000',
            width: 2,
            dasharray: this._rowBlockDashArrays[Number(seat.rowBlock) % this._rowBlockDashArrays.length]
        });
        // weights are showed as fill, so if they are not shown, we require a white fill to show the stroke clearly
        if (!showingWeights) {
            svgSeat.fill('#FFF');
        } else { // otherwise, fill is transparent
            svgSeat.fill({ opacity: 0 });
        }
        return svgSeat;
    }
}
