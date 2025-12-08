import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { Circle, G } from '@svgdotjs/svg.js';
import { combineLatest, switchMap } from 'rxjs';
import { debounceTime, filter, map } from 'rxjs/operators';
import { SVGDefs } from '../../../models/SVGDefs.enum';
import { VisualizationMode } from '../../../models/venue-tpl-editor-modes.enum';
import { EdSeat } from '../../../models/venue-tpl-editor-venue-map-items.model';
import { getWeightsColors, weightToColor } from '../../../utils/seat-weights.utils';
import { VenueTplEditorDomService } from '../../../venue-tpl-editor-dom.service';
import { VenueTplEditorVenueMapService } from '../../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../../venue-tpl-editor.service';

@Component({
    imports: [AsyncPipe],
    selector: 'app-venue-tpl-editor-weights-info-layer',
    templateUrl: './venue-tpl-editor-weights-info-layer.component.html',
    styleUrls: ['./venue-tpl-editor-weights-info-layer.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorWeightsInfoLayerComponent {

    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _viewsSrv = inject(VenueTplEditorViewsService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);
    private readonly _domSanitizer = inject(DomSanitizer);

    private readonly _weightsColors = getWeightsColors();

    readonly viewBox$ = this._domSrv.getSvgSvgElement$().pipe(map(mainSVGElement => mainSVGElement?.getAttribute(SVGDefs.attributes.viewBox)));

    readonly paintedSeats$ = this._viewsSrv.getSvgData$()
        .pipe(
            filter(Boolean),
            debounceTime(10),
            switchMap(() => combineLatest({
                seats: this._venueMapSrv.getVenueItems$().pipe(filter(Boolean), map(items => items.seats)),
                parentSVG: this._domSrv.getSvgSvgElement$(),
                showingBlocks: this._editorSrv.modes.getVisualizationModes$().pipe(map(vm => vm.includes(VisualizationMode.blocks)))
            })),
            map(({ seats, parentSVG, showingBlocks }) => {
                const g = new G();
                if (parentSVG) {
                    const wrappers = Array.from(parentSVG.children)
                        .filter(c => c.tagName === SVGDefs.nodeTypes.seat && c.id && c.classList.contains(SVGDefs.classes.interactive))
                        .filter(e => seats.has(Number(e.id)))
                        .map(element => ({ element, seat: seats.get(Number(element.id)) }));
                    if (wrappers.length) {
                        const weightsInfo = wrappers
                            .map(wrapper => ({ min: wrapper.seat.weight, max: wrapper.seat.weight }))
                            .reduce((p, c) => ({ min: Math.min(p.min, c.min), max: Math.max(p.max, c.max) }));
                        wrappers.forEach(wrapper =>
                            g.add(this.createWeightCircle(wrapper.element, wrapper.seat, showingBlocks, weightsInfo.min, weightsInfo.max))
                        );
                    }
                }
                return this._domSanitizer.bypassSecurityTrustHtml(g.node.innerHTML);
            })
        );

    private createWeightCircle(element: Element, seat: EdSeat, showingBlocks: boolean, minWeight: number, maxWeight: number): Circle {
        const svgCircle = element.cloneNode(false) as SVGCircleElement;
        if (svgCircle.hasAttribute('style')) {
            svgCircle.attributes.removeNamedItem('style');
        }
        svgCircle.setAttribute('vector-effect', 'non-scaling-stroke');
        const svgSeat = new Circle(svgCircle);
        // blocks are showed as strokes, so if they are shown, this stroke is unnecessary
        if (!showingBlocks) {
            svgSeat.stroke({ color: '#000000', width: 1 });
        }
        svgSeat.fill(weightToColor(this._weightsColors, seat.weight, minWeight, maxWeight));
        return svgSeat;
    }
}
