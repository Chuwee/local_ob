import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { SVG } from '@svgdotjs/svg.js';
import { combineLatest } from 'rxjs';
import { debounceTime, map } from 'rxjs/operators';
import { SVGDefs } from '../../../models/SVGDefs.enum';
import { VisualizationMode } from '../../../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorDomService } from '../../../venue-tpl-editor-dom.service';
import { VenueTplEditorVenueMapService } from '../../../venue-tpl-editor-venue-map.service';
import { VenueTplEditorViewsService } from '../../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../../venue-tpl-editor.service';

@Component({
    imports: [AsyncPipe, NgFor, NgIf],
    selector: 'app-venue-tpl-editor-names-info-layer',
    templateUrl: './venue-tpl-editor-names-info-layer.component.html',
    styleUrls: ['./venue-tpl-editor-names-info-layer.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorNamesInfoLayerComponent {

    private readonly _editorSrv = inject(VenueTplEditorService);
    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _viewSrv = inject(VenueTplEditorViewsService);
    private readonly _venueMapSrv = inject(VenueTplEditorVenueMapService);

    readonly viewBox$ = this._domSrv.getSvgSvgElementViewBox$();

    readonly seatNames$ = combineLatest([
        this._domSrv.getSvgSvgElement$(),
        this._venueMapSrv.getVenueItems$(),
        this._editorSrv.modes.getVisualizationModes$().pipe(
            map(modes => modes.includes(VisualizationMode.weights) || modes.includes(VisualizationMode.blocks))
        ),
        this._viewSrv.getSvgData$()
    ])
        .pipe(
            debounceTime(10),
            map(([svgElement, items, darkColor]) => {
                if (svgElement) {
                    const svg = SVG(svgElement);
                    return svg.children()
                        .filter(child =>
                            child.node.tagName === SVGDefs.nodeTypes.seat
                            && child.hasClass(SVGDefs.classes.interactive)
                            && !items.seats.get(Number(child.id())).delete
                        )
                        .map(child => ({
                            box: child.rbox(svg),
                            name: items.seats.get(Number(child.id())).name,
                            darkColor
                        }));
                } else {
                    return null;
                }
            })
        );

    getSeatLabelOffset(seatNameLength: number): number {
        switch (seatNameLength) {
            case 1: return 0.28;
            case 2: return 0.08;
            default: return -0.05;
        }
    }
}
