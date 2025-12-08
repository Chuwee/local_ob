import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DomSanitizer } from '@angular/platform-browser';
import { Box, Circle, G } from '@svgdotjs/svg.js';
import { combineLatest, distinctUntilChanged, Observable, Subject } from 'rxjs';
import { filter, map, shareReplay, take, tap } from 'rxjs/operators';
import { EdSeat } from '../../../models/venue-tpl-editor-venue-map-items.model';
import { VenueTplEditorWeightsConfigurationType } from '../../../models/venue-tpl-editor-weights-configuration';
import { colorToWeight, getWeightsColors, weightToColor } from '../../../utils/seat-weights.utils';
import { VenueTplEditorDomService } from '../../../venue-tpl-editor-dom.service';
import { VenueTplEditorWeightsSetupService } from '../../../venue-tpl-editor-weights-setup.service';
import { VenueTplEditorSvgModifier } from '../venue-tpl-editor-svg-modifier';
import { VenueTplEditorWeightsSetupLinearComponent } from './linear/venue-tpl-editor-weights-setup-linear.component';
import { VenueTplEditorWeightsSetupRadialComponent } from './radial/venue-tpl-editor-weights-setup-radial.component';

@Component({
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        VenueTplEditorWeightsSetupLinearComponent,
        VenueTplEditorWeightsSetupRadialComponent
    ],
    selector: 'app-venue-tpl-editor-weights-setup',
    templateUrl: './venue-tpl-editor-weights-setup.component.html',
    styleUrls: ['./venue-tpl-editor-weights-setup.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorWeightsSetupComponent extends VenueTplEditorSvgModifier implements OnInit {

    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _weightsSetupSrv = inject(VenueTplEditorWeightsSetupService);
    private readonly _domSanitizer = inject(DomSanitizer);

    @ViewChild('weightsSvg', { read: ElementRef<SVGSVGElement> })
    private readonly _weightsSvg: ElementRef<SVGSVGElement>;

    private readonly _weightsColors = getWeightsColors();

    readonly configurationTypes = VenueTplEditorWeightsConfigurationType;

    readonly viewBox$ = this._domSrv.getSvgSvgElementViewBox$();

    readonly paintedSeats$ = combineLatest([
        this._weightsSetupSrv.getViewSeatBoxes$(),
        this._weightsSetupSrv.getViewSeats$()
    ])
        .pipe(
            filter(sources => sources.every(Boolean)),
            map(([seatBoxes, seats]) => {
                const g = new G();
                if (seats.size) {
                    const seatsArray = Array.from(seats.values());
                    const weightsInfo = seatsArray
                        .map(seat => ({ min: seat.weight, max: seat.weight }))
                        .reduce((p, c) => ({ min: Math.min(p.min, c.min), max: Math.max(p.max, c.max) }));
                    seatsArray.forEach(seat =>
                        g.add(this.createWeightCircle(seat, seatBoxes.get(seat.id), weightsInfo.min, weightsInfo.max))
                    );
                }
                return this._domSanitizer.bypassSecurityTrustHtml(g.node.innerHTML);
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    readonly configuration$ = this._weightsSetupSrv.getWeightsConfiguration$()
        .pipe(
            distinctUntilChanged(),
            tap(config => {
                if (config?.type === VenueTplEditorWeightsConfigurationType.snake) {
                    this._weightsSetupSrv.setSnakeConfiguration();
                }
            }),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    ngOnInit(): void {
        this._weightsSetupSrv.setWeightsConfiguration(null);
        this._weightsSetupSrv.resetViewSeats();
    }

    preCommitWeights(): void {
        combineLatest([
            this.getSvgCanvasImageData$(),
            this._weightsSetupSrv.getViewSeats$(),
            this._weightsSetupSrv.getViewSeatBoxes$(),
            this._domSrv.getWorkAreaCoordinates$()
        ])
            .pipe(take(1))
            .subscribe(([canvasImageData, viewSeats, viewBoxes, coords]) => {
                Array.from(viewSeats.values())
                    .forEach(seat => {
                        const box = viewBoxes.get(seat.id);
                        // this data comes in format [R, G, B, A]
                        const seatColorData = canvasImageData.getImageData(
                            (box.x + (box.width / 2)) * coords.scale,
                            (box.y + (box.height / 2)) * coords.scale,
                            1, 1
                        ).data;
                        const color = '#' + Array.from(seatColorData.slice(0, 3))
                            .map(colorComp => Number(colorComp).toString(16).padStart(2, '0'))
                            .join('');
                        seat.weight = colorToWeight(this._weightsColors, color);
                    });
            });
    }

    // for testing purposes, add click handler to this component svg
    // testClickCoords(event: MouseEvent): void {
    //     this.getSvgCanvasImageData$()
    //         .subscribe(canvasImageData => {
    //             const pixelData = canvasImageData.getImageData(event.offsetX, event.offsetY, 1, 1).data;
    //             console.log(
    //                 'seat color',
    //                 event.offsetX, event.offsetY, colorToWeight(this.getColor(pixelData[0], pixelData[1], pixelData[2]))
    //             );
    //         });
    // }

    private getSvgCanvasImageData$(): Observable<CanvasImageData> {
        const subject = new Subject<CanvasImageData>();
        const svgElement = this._weightsSvg.nativeElement;
        const canvas = document.createElement('canvas');
        canvas.width = svgElement.clientWidth;
        canvas.height = svgElement.clientHeight;
        const svgUrl = URL.createObjectURL(
            new Blob(
                [new XMLSerializer().serializeToString(svgElement)],
                { type: 'image/svg+xml;charset=utf-8' }
            )
        );
        const img = new Image();
        img.onload = () => {
            const ctx = canvas.getContext('2d', { willReadFrequently: true });
            ctx.drawImage(img, 0, 0);
            subject.next(ctx);
            subject.complete();
        };
        img.src = svgUrl;
        return subject.asObservable();
    }

    private createWeightCircle(seat: EdSeat, box: Box, minWeight: number, maxWeight: number): Circle {
        const r = box.width / 2;
        const circle = new Circle({ id: seat.id.toString(), r, cx: box.x + r, cy: box.y + r });
        circle.stroke({ color: '#000000', width: 1 });
        circle.attr('vector-effect', 'non-scaling-stroke');
        circle.fill(weightToColor(this._weightsColors, seat.weight, minWeight, maxWeight));
        return circle;
    }
}
