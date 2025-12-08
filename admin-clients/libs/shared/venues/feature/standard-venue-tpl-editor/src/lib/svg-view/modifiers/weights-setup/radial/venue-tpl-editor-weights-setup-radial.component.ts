import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BehaviorSubject, combineLatest, of } from 'rxjs';
import { delay, map, shareReplay, take, tap } from 'rxjs/operators';
import { getDistanceBetween, getHypotenuse } from '../../../../utils/geometry.utils';
import { getWeightsKeyColors } from '../../../../utils/seat-weights.utils';
import { VenueTplEditorDomService } from '../../../../venue-tpl-editor-dom.service';
import { VenueTplEditorWeightsSetupService } from '../../../../venue-tpl-editor-weights-setup.service';
import { PointControlComponent } from '../../controls/point/point-control.component';

@Component({
    // It's a svg component, this is the best way found, works as a directive,
    // but it's a component, for this reason has a directive selector
    // eslint-disable-next-line @angular-eslint/component-selector
    selector: '[appEditorWeightsSetupRadial]',
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        PointControlComponent
    ],
    templateUrl: './venue-tpl-editor-weights-setup-radial.component.html',
    styleUrls: ['./venue-tpl-editor-weights-setup-radial.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorWeightsSetupRadialComponent implements OnInit {

    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _weightsSetupSrv = inject(VenueTplEditorWeightsSetupService);

    private readonly _minGradientOffset = 0.02;
    private readonly _maxGradientOffset = 1 - this._minGradientOffset;

    private readonly _controlsDataBehavior = new BehaviorSubject({
        emitPreCommit: false,
        currentMovingPoint: null as 'axis' | 'gradient' | 'size',
        axisPoint: { x: 0, y: 0 },
        gradientPoint: { x: 0, y: 0 },
        gradientOffset: 0,
        sizePoint: { x: 0, y: 0 }
    });

    readonly firstKeyColor = getWeightsKeyColors()[0];

    @Output()
    readonly preCommitWeights = new EventEmitter<void>();

    readonly viewBox$ = this._domSrv.getSvgSvgElementViewBox$();

    readonly invScale$ = this._domSrv.getWorkAreaCoordinates$().pipe(map(coords => 1 / (coords?.scale || 1)));

    readonly seatsMask$ = this._weightsSetupSrv.getSeatsMask$();

    readonly seatsStrokes$ = this._weightsSetupSrv.getSeatsStrokes();

    readonly controlsData$ = this._controlsDataBehavior.asObservable()
        .pipe(shareReplay({ refCount: true, bufferSize: 1 }));

    readonly saver$ = of(null).pipe(delay(0), tap(() => {
        this.preCommitWeights.next();
        this._controlsDataBehavior.next({ ...this._controlsDataBehavior.value, emitPreCommit: false });
    }));

    readonly gradientStops$ = this.controlsData$
        .pipe(map(controlsData => {
            const keyColors = getWeightsKeyColors().reverse();
            const step = (1 - controlsData.gradientOffset) / (keyColors.length - 1);
            return keyColors.map((color, index) => ({
                color,
                offset: step * index + controlsData.gradientOffset
            }));
        }));

    readonly gradientCoords$ = combineLatest([this._domSrv.getSvgSvgElementViewBox$(), this.controlsData$])
        .pipe(map(([viewBox, controlsData]) => ({
            bgSize: getHypotenuse(Math.max(viewBox[2], viewBox[3])) * 2,
            gradientSize: getDistanceBetween(controlsData.axisPoint, controlsData.sizePoint),
            axisPoint: controlsData.axisPoint
        })));

    ngOnInit(): void {
        // init controls data set
        this._weightsSetupSrv.getViewSeatBox$()
            .pipe(take(1))
            .subscribe(box => {
                const x = box.x + box.width / 2;
                const axisPoint = { x, y: box.y + box.height };
                const sizePoint = { x, y: Math.max(axisPoint.y - getDistanceBetween(axisPoint, box), 0) };
                this._controlsDataBehavior.next({
                    emitPreCommit: true, currentMovingPoint: null,
                    axisPoint,
                    gradientPoint: this.inferOffsetPoint(axisPoint, sizePoint, this._minGradientOffset),
                    gradientOffset: this._minGradientOffset,
                    sizePoint
                });
            });
    }

    moveInit(point: 'axis' | 'gradient' | 'size'): void {
        this._controlsDataBehavior.next({
            ...this._controlsDataBehavior.value,
            currentMovingPoint: point
        });
    }

    movePoint(event: MouseEvent): void {
        if (this._controlsDataBehavior.value.currentMovingPoint) {
            this._domSrv.getWorkAreaCoordinates$()
                .pipe(take(1))
                .subscribe(coords => {
                    const newData = { ...this._controlsDataBehavior.value };
                    const mousePos = { x: event.offsetX / coords.scale, y: event.offsetY / coords.scale };
                    if (newData.currentMovingPoint === 'axis') {
                        newData.axisPoint = mousePos;
                    } else if (newData.currentMovingPoint === 'gradient') {
                        const dist1 = getDistanceBetween(newData.axisPoint, mousePos);
                        const dist3 = getDistanceBetween(newData.sizePoint, mousePos);
                        newData.gradientOffset = dist1 / (dist1 + dist3);
                        newData.gradientOffset =
                            Math.min(Math.max(newData.gradientOffset, this._minGradientOffset), this._maxGradientOffset);
                    } else {
                        newData.sizePoint = mousePos;
                    }
                    newData.gradientPoint = this.inferOffsetPoint(newData.axisPoint, newData.sizePoint, newData.gradientOffset);
                    this._controlsDataBehavior.next(newData);
                });
        }
    }

    moveEnd(): void {
        if (this._controlsDataBehavior.value.currentMovingPoint) {
            this._controlsDataBehavior.next({ ...this._controlsDataBehavior.value, currentMovingPoint: null, emitPreCommit: true });
        }
    }

    moveCheck(event: MouseEvent): void {
        if (this._controlsDataBehavior.value.currentMovingPoint && event.buttons === 0) { // no button
            this.moveEnd();
        }
    }

    private inferOffsetPoint(p1: { x: number; y: number }, p3: { x: number; y: number }, offset: number): { x: number; y: number } {
        return {
            x: p1.x - (p1.x - p3.x) * offset,
            y: p1.y - (p1.y - p3.y) * offset
        };
    }
}
