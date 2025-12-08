import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, ElementRef, HostListener, inject, OnInit, ViewChild
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Box, Element, G, MatrixExtract, SVG } from '@svgdotjs/svg.js';
import { BehaviorSubject, combineLatest, Observable, startWith, Subject, switchMap } from 'rxjs';
import { filter, map, shareReplay, take, tap } from 'rxjs/operators';
import { EditCurrentViewAction } from '../../../actions/edit-current-view-action';
import { EditSvgAction } from '../../../actions/edit-svg-action';
import { SVGDefs } from '../../../models/SVGDefs.enum';
import { InteractionMode } from '../../../models/venue-tpl-editor-modes.enum';
import { VenueTplEditorSvgTriggerType } from '../../../models/venue-tpl-editor-svg-trigger-type.enum';
import { getAngleInDegrees } from '../../../utils/geometry.utils';
import { VenueTplEditorDomService } from '../../../venue-tpl-editor-dom.service';
import { VenueTplEditorSelectionService } from '../../../venue-tpl-editor-selection.service';
import { VenueTplEditorViewsService } from '../../../venue-tpl-editor-views.service';
import { VenueTplEditorService } from '../../../venue-tpl-editor.service';
import { RotateControlComponent } from '../controls/rotate/rotate-control.component';
import { ScaleControlComponent } from '../controls/scale/scale-control.component';
import { VenueTplEditorSvgModifier } from '../venue-tpl-editor-svg-modifier';
import { SvgTransformPipe } from './svg-transform.pipe';

@Component({
    imports: [
        CommonModule,
        SvgTransformPipe,
        RotateControlComponent,
        ScaleControlComponent,
        FlexLayoutModule
    ],
    selector: 'app-venue-tpl-editor-svg-transforms',
    templateUrl: './venue-tpl-editor-svg-transforms.component.html',
    styleUrls: ['./venue-tpl-editor-svg-transforms.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTplEditorSvgTransformsComponent extends VenueTplEditorSvgModifier implements OnInit {
    private readonly ARROW_KEYS = {
        up: 'ArrowUp',
        left: 'ArrowLeft',
        down: 'ArrowDown',
        right: 'ArrowRight'
    };

    // on destroy
    #onDestroy = inject(DestroyRef);
    // deps
    readonly #changeDetRef = inject(ChangeDetectorRef);
    readonly #editorSrv = inject(VenueTplEditorService);
    readonly #selectionSrv = inject(VenueTplEditorSelectionService);
    readonly #viewsSrv = inject(VenueTplEditorViewsService);
    readonly #domSrv = inject(VenueTplEditorDomService);
    readonly #domSanitizer = inject(DomSanitizer);
    //sync access to main svg scale, updated asynchronously, but required synchronously in some interactions
    private _scale: number;

    @ViewChild('svg')
    private _svg: ElementRef<SVGElement>;

    @ViewChild('selectionModifierContainer')
    private readonly _selectionModifierContainer: ElementRef<SVGElement>;

    @ViewChild('selectionModifier')
    private readonly _selectionModifier: ElementRef<SVGElement>;

    @ViewChild('globalSelectionRect')
    private readonly _globalSelectionRect: ElementRef<SVGElement>;

    @ViewChild('selectDragRect')
    private readonly _selectDragRect: ElementRef<SVGElement>;

    readonly #selectDrag = new BehaviorSubject<{
        keepSelection: boolean;
        initPos: { x: number; y: number };
        rect?: Box;
        changed?: boolean;
    }>(null);

    readonly #moveDrag = new BehaviorSubject<{
        lastPos: { x: number; y: number };
        modifierElement: Element;
        changed?: boolean;
    }>(null);

    readonly #resizeDrag = new BehaviorSubject<{
        axisPoint: { x: number; y: number };
        initSize: { width: number; height: number };
        angle: number;
        modifierElement: Element;
        changed?: boolean;
    }>(null);

    readonly #rotateDrag = new BehaviorSubject<{
        initialAngle: number;
        axisPoint: { x: number; y: number };
        modifierElement: Element;
        changed?: boolean;
    }>(null);

    private readonly _refreshGlobalRect = new Subject<void>();

    readonly viewBox$ = this.#domSrv.getSvgSvgElementViewBox$();

    readonly invScale$ = this.#domSrv.getWorkAreaCoordinates$().pipe(map(coords => 1 / (coords?.scale || 1)));

    readonly selectDrag$ = this.#selectDrag.asObservable();

    readonly showShapesClones$ = combineLatest([this.#moveDrag, this.#resizeDrag, this.#rotateDrag])
        .pipe(map(sources => sources.map(drag => drag?.changed).some(Boolean)));

    readonly dragAreaActive$ = combineLatest([this.#selectDrag, this.#moveDrag, this.#resizeDrag, this.#rotateDrag])
        .pipe(map(sources => sources.map(drag => drag?.changed).some(Boolean)));

    readonly selectedInfoWrappers$: Observable<SelectionInfoWrapper> = this.#domSrv.getSvgSvgElement$().pipe(
        filter(Boolean),
        map(mainSvg => {
            const svgElements = Array.from(mainSvg.childNodes).map(child => child as SVGElement);
            const interactiveSvgElements = svgElements.filter(child => child.classList?.contains(SVGDefs.classes.interactive));
            return { svgElements, interactiveSvgElements };
        }),
        switchMap(elements => this.#selectionSrv.graphicSelection$().pipe(map(selection => ({ ...elements, selection })))),
        map(({ svgElements, interactiveSvgElements, selection }) => {
            svgElements.forEach(svgElement => svgElement.classList?.remove(SVGDefs.classes.dragMove));
            const elementWrappers = [
                ...selection.elements,
                ...Array.from(selection.seats).map(seatId => this.searchElement(seatId.toString(), interactiveSvgElements)),
                ...Array.from(selection.nnzs).map(nnzId => this.searchElement(nnzId.toString(), interactiveSvgElements))
            ]
                .filter(Boolean)
                .map(svgElement => ({
                    svgElement,
                    element: new Element(svgElement)
                }));
            elementWrappers.forEach(ew => ew.svgElement.classList.add(SVGDefs.classes.dragMove));
            return {
                disableScale: !!selection.seats.size,
                elementWrappers
            } as SelectionInfoWrapper;
        }),
        map(result => {
            result.elementWrappers.forEach(elementWrapper => {
                elementWrapper.transform = elementWrapper.element.transform();
                elementWrapper.bbox = this.getElementBBox(elementWrapper.element);
            });
            const elementsClone = result.elementWrappers.map(ew => ew.svgElement.outerHTML).join('');
            return result.elementWrappers.length ? {
                ...result,
                svgElementsClone: this.#domSanitizer.bypassSecurityTrustHtml(elementsClone)
            } : null;
        }),
        shareReplay(1)
    );

    readonly globalSelectionRect$ = this.selectedInfoWrappers$
        .pipe(
            tap(() => this.#changeDetRef.detectChanges()), //Uff, I can't explain, required for addImage feature
            map(selectedInfoWrappers => {
                // in this step it clones the selection and gets the boundaries, if there is only one element selected,
                // calculates the boundaries without rotation, and passes the angle to the next step.
                // DisableScale depends on the type of the selection, seats cannot be scaled never.
                if (selectedInfoWrappers?.elementWrappers?.length && this._selectionModifier) {
                    const selModClone = new Element(this._selectionModifier.nativeElement.cloneNode(true));
                    // selection modifier
                    // const modClone = modContainerClone.children()[0] as G;
                    let angle = 0;
                    if (selectedInfoWrappers.elementWrappers.length === 1) {
                        // it can be a clone of the selected element, or the selection rect of this element, these elements
                        // has the same structure in sizing, positioning and transforming.
                        const modCloneChild = selModClone.children()[0].children()[0];
                        angle = modCloneChild.transform().rotate;
                        modCloneChild.rotate(-angle);
                    }
                    // gets the result modifier Container clone bounding box, without the angles of its children,
                    const bbox = this.getElementBBox(selModClone);
                    return {
                        bbox,
                        angle,
                        disableScale: selectedInfoWrappers.disableScale // in seat selection, scaling is disabled
                    };
                } else {
                    return null;
                }
            }),
            switchMap(globalSelectionRect => this._refreshGlobalRect.pipe(startWith(null), map(() => globalSelectionRect))),
            map(globalSelectionRect => {
                // In this step it applies the changes to selectionModifier, is the group modified on drag operations.
                if (globalSelectionRect) {
                    const transform = new G(this._selectionModifier.nativeElement).transform();
                    const width = globalSelectionRect.bbox.width * transform.scaleX; // scale width result
                    const height = globalSelectionRect.bbox.height * transform.scaleY;// scale height result
                    let moveOffsetX = 0;//this.#moveDrag.value?.changed ? transform.translateX * transform.scaleX : 0;
                    let moveOffsetY = 0;//this.#moveDrag.value?.changed ? transform.translateY * transform.scaleY : 0;
                    if (this.#moveDrag.value?.changed) {
                        moveOffsetX = transform.translateX * transform.scaleX;
                        moveOffsetY = transform.translateY * transform.scaleY;
                    }
                    return {
                        bbox: {
                            x: globalSelectionRect.bbox.x // initial position
                                - (width - globalSelectionRect.bbox.width) / 2 // width scale compensation
                                + moveOffsetX, // x movement compensation
                            y: globalSelectionRect.bbox.y // initial position
                                - (height - globalSelectionRect.bbox.height) / 2 // height scale compensation
                                + moveOffsetY, // y movement compensation
                            width,
                            height
                        },
                        angle: globalSelectionRect.angle + transform.rotate,
                        disableScale: globalSelectionRect.disableScale
                    };
                } else {
                    return null;
                }
            }),
            shareReplay(1)
        );

    // eslint-disable-next-line @typescript-eslint/unbound-method
    mouseMove: (event: MouseEvent) => void = this.emptyMouseMoveHandler;

    // eslint-disable-next-line @typescript-eslint/unbound-method
    mouseUp: (event: MouseEvent) => void = this.emptyMouseUpHandler;

    ngOnInit(): void {
        this.#domSrv.getWorkAreaCoordinates$().pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(coords => this._scale = coords?.scale ?? 1);
    }

    outerClick(event: MouseEvent): void {
        if (event.button === 0) { // primary button
            this.#domSrv.getSvgSvgElement$()
                .pipe(take(1))
                .subscribe(mainSvg => {
                    const element = this.searchTopLevelElement(mainSvg, event.target as SVGElement);
                    if (element) {
                        // incremental selection
                        if (event.ctrlKey || event.shiftKey) {
                            this.#selectionSrv.invertElementSelection(element);
                        }
                    } else {
                        // selection clear on empty space, drag operations skips clicks because the overlay mousemove control
                        if (!event.ctrlKey && !event.shiftKey) {
                            this.#selectionSrv.unselectAll();
                        }
                    }
                });
        }
    }

    outerDblClick(): void {
        combineLatest([
            this.#selectionSrv.getSelection$(),
            this.#viewsSrv.getViewData$()
        ])
            .pipe(take(1))
            .subscribe(([selection, viewData]) => {
                if (selection.nnzs.size === 0 && selection.seats.size === 0 && selection.elements.length === 1) {
                    const item = selection.elements[0];
                    if (item.id && item.classList.contains(SVGDefs.classes.interactive)) {
                        const targetView = viewData.links.find(link => link.ref_id === item.id);
                        if (targetView) {
                            this.#editorSrv.history.enqueue(new EditCurrentViewAction(targetView.view_id, this.#viewsSrv));
                        }
                    }
                }
            });
    }

    outerMouseDown(event: MouseEvent): void {
        if (event.button === 0) { // primary button
            combineLatest([
                this.#domSrv.getSvgSvgElement$(),
                this.selectedInfoWrappers$
            ])
                .pipe(take(1))
                .subscribe(([mainSvg, selectedInfoWrappers]) => {
                    const svgElement = this.searchTopLevelElement(mainSvg, event.target as SVGElement);
                    if (svgElement) {
                        if (!event.ctrlKey && !event.shiftKey) {
                            // fast selection on mouse down, to enable drag move operations without previous item selection
                            const targetInfoWrapper = selectedInfoWrappers?.elementWrappers.find(siw => siw.svgElement === svgElement);
                            if (!targetInfoWrapper) {
                                if (selectedInfoWrappers?.elementWrappers.length) {
                                    this.#selectionSrv.unselectAll();
                                }
                                this.#selectionSrv.selectElements([svgElement]);
                            }
                            this.initDragMove(event);
                        }
                    } else {
                        // mouse down on empty space inits drag selection, but the first move is the real drag selection start, this first
                        // move comes from svg container (this.outerMouseMove()), the next ones from drag overlay (this.mouseMove()).
                        this.initDragSelection(event);
                    }
                });
        }
    }

    outerMouseMove(event: MouseEvent): void {
        this.mouseMove(event);
    }

    outerMouseUp(event: MouseEvent): void {
        this.mouseUp(event);
    }

    resizeControlMouseDown(event: MouseEvent): void {
        //skips selection drag
        event.preventDefault();
        event.stopImmediatePropagation();
        event.stopPropagation();
        if (event.button === 0) { // primary button
            this.initDragResize();
        }
    }

    rotateControlMouseDown(event: MouseEvent): void {
        //skips selection drag
        event.preventDefault();
        event.stopImmediatePropagation();
        event.stopPropagation();
        if (event.button === 0) { // primary button
            this.initDragRotate(event);
        }
    }

    mouseEnterOnDragGuard(event: MouseEvent): void {
        if (event.buttons === 0) { // no button
            this.mouseUp(event);
        }
    }

    @HostListener('window:keydown', ['$event'])
    onKeyDown(event: KeyboardEvent): void {
        if (event.target instanceof HTMLBodyElement && Object.values(this.ARROW_KEYS).includes(event.key)) {
            event.preventDefault();
            event.stopPropagation();
            event.stopImmediatePropagation();
            this.selectedInfoWrappers$.pipe(take(1))
                .subscribe(selectionInfoWrappers => {
                    if (selectionInfoWrappers?.elementWrappers?.length) {
                        const unit = event.ctrlKey || event.shiftKey ? 10 : 1;
                        const mx = event.key === this.ARROW_KEYS.right ? unit : event.key === this.ARROW_KEYS.left ? -unit : 0;
                        const my = event.key === this.ARROW_KEYS.down ? unit : event.key === this.ARROW_KEYS.up ? -unit : 0;
                        new G(this._selectionModifier.nativeElement).translate(mx / this._scale, my / this._scale);
                        this.commitModifications();
                        this.disableDragOperations();
                    }
                });
        }
    }

    private searchElement(id: string, svgElements: SVGElement[]): SVGElement {
        return svgElements.find(svgElement => svgElement.id === id);
    }

    private getElementBBox(element: Element): Box {
        try {
            if (element instanceof G && element.children().length === 0) {
                console.warn('Using empty group bbox', element);
                return new Box(0, 0, 0, 0);
            } else {
                return element?.bbox();
            }
        } catch (e) {
            console.error('could not extract bbox from ', element, e);
            return new Box(0, 0, 0, 0);
        }
    }

    private disableDragOperations(): void {
        // eslint-disable-next-line @typescript-eslint/unbound-method
        this.mouseMove = this.emptyMouseMoveHandler;
        // eslint-disable-next-line @typescript-eslint/unbound-method
        this.mouseUp = this.emptyMouseUpHandler;
        this.#selectDrag.next(null);
        this.#moveDrag.next(null);
        this.#resizeDrag.next(null);
        this.#rotateDrag.next(null);
    }

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    private emptyMouseMoveHandler(): void { }

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    private emptyMouseUpHandler(): void { }

    // DRAG SELECTION

    private initDragSelection(event: MouseEvent): void {
        this.mouseMove = this.dragSelectionMove.bind(this);
        this.mouseUp = this.dragSelectionEnd.bind(this);
        this.#selectDrag.next({
            initPos: { x: event.offsetX, y: event.offsetY },
            keepSelection: event.ctrlKey || event.shiftKey
        });
    }

    private dragSelectionMove(event: MouseEvent): void {
        const selectDrag = this.#selectDrag.value;
        this.#selectDrag.next({
            initPos: selectDrag.initPos,
            keepSelection: selectDrag.keepSelection || event.ctrlKey || event.shiftKey,
            changed: true,
            rect: new Box(
                Math.min(event.offsetX, selectDrag.initPos.x) / this._scale,
                Math.min(event.offsetY, selectDrag.initPos.y) / this._scale,
                Math.abs(event.offsetX - selectDrag.initPos.x) / this._scale,
                Math.abs(event.offsetY - selectDrag.initPos.y) / this._scale
            )
        });
    }

    private dragSelectionEnd(event: MouseEvent): void {
        if (this.#selectDrag.value?.changed) {
            this.#selectDrag.value.keepSelection = this.#selectDrag.value.keepSelection || event.ctrlKey || event.shiftKey;
            if (!this.#selectDrag.value.keepSelection) {
                this.#selectionSrv.unselectAll();
            }
            combineLatest([
                this.#editorSrv.modes.getInteractionMode$(),
                this.#domSrv.getSvgSvgElement$()
            ])
                .pipe(take(1))
                .subscribe(([intMode, mainSvg]) => {
                    const dragRect = this._selectDragRect.nativeElement.getBoundingClientRect();
                    const elementsToSelect = Array.from(mainSvg.children)
                        .filter(child => {
                            const childRect = child.getBoundingClientRect();
                            return !(
                                (childRect.x > dragRect.x + dragRect.width || childRect.x + childRect.width < dragRect.x)
                                || (childRect.y > dragRect.y + dragRect.height || childRect.y + childRect.height < dragRect.y)
                            );
                        }) as SVGElement[];
                    if (intMode === InteractionMode.all) {
                        this.#selectionSrv.selectElements(elementsToSelect);
                    } else if (intMode === InteractionMode.graphic) {
                        this.#selectionSrv.selectElements(elementsToSelect.filter(e => !e.classList.contains(SVGDefs.classes.interactive)));
                    } else if (intMode === InteractionMode.interactive) {
                        this.#selectionSrv.selectElements(elementsToSelect.filter(e => e.classList.contains(SVGDefs.classes.interactive)));
                    }
                });
        }
        this.disableDragOperations();
    }

    // DRAG MOVE

    private initDragMove(event: MouseEvent): void {
        this.mouseMove = this.dragMoveMove.bind(this);
        this.mouseUp = this.dragMoveEnd.bind(this);
        setTimeout(() => {
            if (this._selectionModifier && this._globalSelectionRect) {
                this.#moveDrag.next({
                    lastPos: { x: event.clientX, y: event.clientY },
                    modifierElement: new Element(this._selectionModifier.nativeElement)
                });
            }
        }, 50);
    }

    private dragMoveMove(event: MouseEvent): void {
        if (this.#moveDrag.value) {
            const moveDrag = this.#moveDrag.value;
            const x = (event.clientX - moveDrag.lastPos.x) / this._scale;
            const y = (event.clientY - moveDrag.lastPos.y) / this._scale;
            moveDrag.modifierElement.translate(x, y);
            this._refreshGlobalRect.next();
            this.#moveDrag.next({
                ...moveDrag,
                lastPos: { x: event.clientX, y: event.clientY },
                changed: true
            });
        }
    }

    private dragMoveEnd(): void {
        if (this.#moveDrag.value?.changed) {
            this.commitModifications();
        }
        this.disableDragOperations();
    }

    // DRAG RESIZE

    private initDragResize(): void {
        this.mouseMove = this.dragResizeMove.bind(this);
        this.mouseUp = this.dragResizeEnd.bind(this);
        const globalSelectionRect = new Element(this._globalSelectionRect.nativeElement);
        const modifierContainerElement = new Element(this._selectionModifierContainer.nativeElement);
        const containerBBox = this.getElementBBox(modifierContainerElement);
        let angle = globalSelectionRect.transform()?.rotate;
        while (angle < 0) { angle += 90; }
        while (angle > 90) { angle -= 90; }
        if (angle === 0) {
            angle = 0.01;
        }
        this.#resizeDrag.next({
            modifierElement: new Element(this._selectionModifier.nativeElement),
            angle,
            axisPoint: {
                x: (containerBBox.x + (containerBBox.width / 2)) * this._scale,
                y: (containerBBox.y + (containerBBox.height / 2)) * this._scale
            },
            initSize: {
                width: containerBBox.width * this._scale,
                height: containerBBox.height * this._scale
            }
        });
    }

    private dragResizeMove(event: MouseEvent): void {
        const resizeDrag = this.#resizeDrag.value;
        const radiansH = (90 - resizeDrag.angle) * Math.PI / 180;
        const radiansV = resizeDrag.angle * Math.PI / 180;
        const newWidth = 2 * (Math.abs(event.offsetX - resizeDrag.axisPoint.x)) / Math.sin(radiansV);
        const newHeight = 2 * (Math.abs(event.offsetY - resizeDrag.axisPoint.y)) / Math.sin(radiansH);
        const initWidth = resizeDrag.initSize.width / Math.sin(radiansV);
        const initHeight = resizeDrag.initSize.height / Math.sin(radiansH);
        let newScaleX = newWidth / initWidth;
        let newScaleY = newHeight / initHeight;
        if (!event.ctrlKey && !event.shiftKey) {
            newScaleX = newScaleY = Math.max(newScaleX, newScaleY);
        }
        resizeDrag.modifierElement.scale(
            newScaleX / resizeDrag.modifierElement.transform().scaleX,
            newScaleY / resizeDrag.modifierElement.transform().scaleY
        );
        this._refreshGlobalRect.next();
        if (!resizeDrag.changed) {
            this.#resizeDrag.next({ ...resizeDrag, changed: true });
        }
    }

    private dragResizeEnd(): void {
        if (this.#resizeDrag.value?.changed) {
            this.commitModifications();
        }
        this.disableDragOperations();
    }

    // DRAG ROTATE

    private initDragRotate(event: MouseEvent): void {
        this.mouseMove = this.dragRotateMove.bind(this);
        this.mouseUp = this.dragRotateEnd.bind(this);
        const modifierElement = new Element(this._selectionModifier.nativeElement);
        const modifierContainerElement = new Element(this._selectionModifierContainer.nativeElement);
        const containerBBox = this.getElementBBox(modifierContainerElement);
        const axisPoint = {
            x: containerBBox.x + (containerBBox.width / 2),
            y: containerBBox.y + (containerBBox.height / 2)
        };
        const initialAngle = getAngleInDegrees(axisPoint, { x: event.offsetX / this._scale, y: event.offsetY / this._scale });
        this.#rotateDrag.next({
            initialAngle,
            axisPoint,
            modifierElement
        });
    }

    private dragRotateMove(event: MouseEvent): void {
        const rotateDrag = this.#rotateDrag.value;
        if (!rotateDrag.changed) {
            this.#rotateDrag.next({ ...rotateDrag, changed: true });
        }
        let degrees = getAngleInDegrees(rotateDrag.axisPoint, { x: event.offsetX / this._scale, y: event.offsetY / this._scale })
            - rotateDrag.initialAngle;
        if (event.ctrlKey || event.shiftKey) {
            degrees = Math.round(degrees / 15) * 15;
        } else {
            degrees = Math.round(degrees);
        }
        rotateDrag.modifierElement.rotate(degrees - rotateDrag.modifierElement.transform().rotate);
        this._refreshGlobalRect.next();
    }

    private dragRotateEnd(): void {
        if (this.#rotateDrag.value?.changed) {
            this.commitModifications();
        }
        this.disableDragOperations();
    }

    // COMMON DRAG END

    private commitModifications(): void {
        if (this._selectionModifier) {
            this.selectedInfoWrappers$
                .pipe(take(1))
                .subscribe(selectedInfoWrappers => {
                    const svg = SVG(this._svg.nativeElement as SVGSVGElement);
                    // transform merge magic, magic! maaagic
                    const transformedElementClones = (new G(this._selectionModifier.nativeElement).children()[0] as G).children();
                    const parentCTM = new G(this._selectionModifierContainer.nativeElement).screenCTM().inverse();
                    let wrapper: { svgElement: SVGElement; element: Element };
                    for (let i = 0; i < selectedInfoWrappers.elementWrappers.length; i++) {
                        wrapper = selectedInfoWrappers.elementWrappers[i];
                        wrapper.element.untransform().transform(parentCTM.multiply(transformedElementClones[i].screenCTM()));
                        this.#domSrv.optimizeSeatTransform(wrapper.element, svg);
                        this.#domSrv.optimizeTransform(wrapper.svgElement);
                    }
                    // resets selection Modifier
                    this._selectionModifier.nativeElement.removeAttribute('transform');
                    // action enqueue
                    const elementsToSelect = selectedInfoWrappers.elementWrappers.map(wrapper => wrapper.svgElement);
                    this.#editorSrv.history.enqueue(new EditSvgAction(
                        this.#viewsSrv, this.#domSrv, this.#selectionSrv,
                        { changer: VenueTplEditorSvgTriggerType.DOMChange, elementsToSelect })
                    );
                });
        }
    }

    private searchTopLevelElement(mainSvg: SVGSVGElement, element: SVGElement): SVGElement {
        if (element?.parentNode === mainSvg) {
            return element;
        } else if (element?.parentNode instanceof SVGElement) {
            return this.searchTopLevelElement(mainSvg, element.parentNode);
        } else {
            return null;
        }
    }
}

interface SelectionInfoWrapper {
    svgElementsClone?: SafeHtml;
    disableScale: boolean;
    elementWrappers: {
        svgElement: SVGElement;
        element: Element;
        transform?: MatrixExtract;
        bbox?: Box;
    }[];
}
