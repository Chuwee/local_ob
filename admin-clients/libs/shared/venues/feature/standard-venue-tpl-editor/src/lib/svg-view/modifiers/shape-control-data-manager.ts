import { inject } from '@angular/core';
import { combineLatest, startWith, Subject } from 'rxjs';
import { map, shareReplay, take } from 'rxjs/operators';
import { getAngleInDegrees } from '../../utils/geometry.utils';
import { VenueTplEditorDomService } from '../../venue-tpl-editor-dom.service';

export class ShapeControlDataManager {

    private readonly _controlData = new Subject<Partial<ControlData>>();

    private readonly _domSrv = inject(VenueTplEditorDomService);

    readonly controlData$ = combineLatest([
        this._controlData.asObservable().pipe(startWith({ pos: this._initPos, angle: this._initAngle })),
        this._domSrv.getWorkAreaCoordinates$()
    ])
        .pipe(
            // overrides scale
            map(([controlData, coords]) => ({
                ...controlData,
                scale: coords.scale
            } as Partial<ControlData>)),
            shareReplay({ refCount: true, bufferSize: 1 })
        );

    constructor(private _initPos = { x: 0, y: 0 }, private _initAngle = 0) {
    }

    dragInit(event: MouseEvent, currentAction: 'move' | 'rotate'): void {
        this.controlData$.pipe(take(1))
            .subscribe(controlData => {
                this._controlData.next({
                    ...controlData,
                    lastMousePos: { x: event.offsetX, y: event.offsetY },
                    angleOffset: getAngleInDegrees(
                        { x: event.offsetX / controlData.scale, y: event.offsetY / controlData.scale }, controlData.pos
                    ) - controlData.angle,
                    currentAction
                });
            });
    }

    dragMove(event: MouseEvent): void {
        this.controlData$
            .pipe(take(1))
            .subscribe(controlData => {
                if (controlData.currentAction === 'move') {
                    this._controlData.next({
                        ...controlData,
                        pos: {
                            x: controlData.pos.x + (event.offsetX - controlData.lastMousePos.x) / controlData.scale,
                            y: controlData.pos.y + (event.offsetY - controlData.lastMousePos.y) / controlData.scale
                        },
                        lastMousePos: { x: event.offsetX, y: event.offsetY }
                    });
                } else if (controlData.currentAction === 'rotate') {
                    this._controlData.next({
                        ...controlData,
                        angle: getAngleInDegrees(
                            { x: event.offsetX / controlData.scale, y: event.offsetY / controlData.scale }, controlData.pos
                        ) - controlData.angleOffset
                    });
                }
            });
    }

    dragEnd(): void {
        this.controlData$
            .pipe(take(1))
            .subscribe(controlData => this._controlData.next({ ...controlData, currentAction: null }));
    }

    dragCheck(event: MouseEvent): void {
        if (event.buttons === 0) { // no button
            this.dragEnd();
        }
    }
}

interface ControlData {
    scale: number;
    currentAction: 'move' | 'rotate';
    lastMousePos: { x: number; y: number };
    pos: { x: number; y: number };
    angleOffset: number;
    angle: number;
}
