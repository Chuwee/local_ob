import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, inject, OnInit, Output } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { BehaviorSubject, combineLatest, of } from 'rxjs';
import { delay, map, shareReplay, take, tap } from 'rxjs/operators';
import { getAngleInDegreesRounded, getDistanceBetween, getHypotenuse, getMiddlePoint } from '../../../../utils/geometry.utils';
import { getWeightsKeyColors } from '../../../../utils/seat-weights.utils';
import { VenueTplEditorDomService } from '../../../../venue-tpl-editor-dom.service';
import { VenueTplEditorWeightsSetupService } from '../../../../venue-tpl-editor-weights-setup.service';
import { PointControlComponent } from '../../controls/point/point-control.component';

@Component({
    // It's a svg component, this is the best way found, works as a directive,
    // but it's a component, for this reason has a directive selector
    // eslint-disable-next-line @angular-eslint/component-selector
    selector: '[appEditorWeightsSetupLinear]',
    imports: [
        CommonModule,
        MaterialModule,
        FlexLayoutModule,
        PointControlComponent
    ],
    templateUrl: './venue-tpl-editor-weights-setup-linear.component.html',
    styleUrls: ['./venue-tpl-editor-weights-setup-linear.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorWeightsSetupLinearComponent implements OnInit {

    private readonly _domSrv = inject(VenueTplEditorDomService);
    private readonly _weightsSetupSrv = inject(VenueTplEditorWeightsSetupService);

    private readonly _controlsDataBehavior = new BehaviorSubject({
        emitPreCommit: false,
        currentMovingPoint: null as 'first' | 'second',
        point1: { x: 0, y: 0 },
        point2: { x: 0, y: 0 }
    });

    readonly keyColors = getWeightsKeyColors();
    readonly firstKeyColor = getWeightsKeyColors()[0];
    readonly lastKeyColor = getWeightsKeyColors().pop();

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

    readonly gradientCoords$ = combineLatest([this._domSrv.getSvgSvgElementViewBox$(), this.controlsData$])
        .pipe(map(([viewBox, controlsData]) => {
            const width = getHypotenuse(Math.max(viewBox[2], viewBox[3]));
            return {
                width,
                height: width / 2,
                gradientHeight: getDistanceBetween(controlsData.point1, controlsData.point2),
                midPoint: getMiddlePoint(controlsData.point1, controlsData.point2),
                rotation: getAngleInDegreesRounded(controlsData.point1, controlsData.point2) - 90
            };
        }));

    ngOnInit(): void {
        // init controls data set
        this._weightsSetupSrv.getViewSeatBox$()
            .pipe(take(1))
            .subscribe(box =>
                this._controlsDataBehavior.next({
                    emitPreCommit: true, currentMovingPoint: null,
                    point1: { x: box.x + box.width / 2, y: box.y },
                    point2: { x: box.x + box.width / 2, y: box.y + box.height }
                })
            );
    }

    moveInit(point: 'first' | 'second'): void {
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
                    const oldData = this._controlsDataBehavior.value;
                    const mousePos = { x: event.offsetX / coords.scale, y: event.offsetY / coords.scale };
                    this._controlsDataBehavior.next({
                        ...this._controlsDataBehavior.value,
                        point1: oldData.currentMovingPoint === 'first' ? mousePos : oldData.point1,
                        point2: oldData.currentMovingPoint === 'second' ? mousePos : oldData.point2
                    });
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
}
