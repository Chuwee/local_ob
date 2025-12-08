import { AsyncPipe, NgIf, NgStyle } from '@angular/common';
import {
    AfterViewChecked, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, EventEmitter, Input, OnDestroy, Output, ViewChild
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { BehaviorSubject, Observable } from 'rxjs';
import { SVGEvent } from '../../models/svg/svg-event.model';
import { SVGDefs } from '../model/svf-defs.enum';

@Component({
    imports: [
        NgIf, NgStyle, AsyncPipe
    ],
    selector: 'app-venue-template-svg',
    templateUrl: './venue-template-svg.component.html',
    styleUrls: ['./venue-template-svg.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class VenueTemplateSVGComponent implements AfterViewChecked, OnDestroy {
    // css fit properties
    private readonly pixelUnit = 'px';
    private readonly fitAnimationDuration = 200;
    // Customizations
    private _svgPadding = 40;
    private _zoomFactor = 1.25;
    // View childs native elements, HTMLElements
    private _svgContainer: HTMLElement;
    private _svgElement: SVGElement;
    private _svgStyles = { width: 0, height: 0, left: 0, top: 0 };
    private _selectionRectPosition = new BehaviorSubject<unknown>({
        ['top.px']: 0, ['left.px']: 0, ['width.px']: 0, ['height.px']: 0
    });

    // update flags, used to update layout after contents has been updated
    private _pendingInitialFitToContainer = false;
    private _pendingScroll = { pendingUpdate: false, originalPoint: { top: 0, left: 0 } };
    // memorizes the last element where cursor is, used to throw the minimum number of roll over and roll out events
    private _interactiveElements = new BehaviorSubject<SVGElement[]>(null);
    private _skipNextClick = false;
    private _sourceSvg: string;
    private _dragSelection = {
        ctrlKey: false,
        shiftKey: false,
        initPoint: { top: null as number, left: null as number },
        destPoint: { top: null as number, left: null as number }
    };

    private _currentOverElement: {
        element: SVGElement;
        type: string;
        id: string;
    } = null;

    private _dragScrollScale = {
        initScale: null as number,
        initPoint: { top: null as number, left: null as number, distance: null as number },
        destPoint: { top: null as number, left: null as number, distance: null as number }
    };

    // EVENTS
    @Output() svgLoaded: EventEmitter<void> = new EventEmitter();
    @Output() elementClick: EventEmitter<SVGEvent> = new EventEmitter();
    @Output() elementDoubleClick: EventEmitter<SVGEvent> = new EventEmitter();
    @Output() elementsSurrounded: EventEmitter<SVGEvent> = new EventEmitter();
    @Output() elementMouseIn: EventEmitter<SVGEvent> = new EventEmitter();
    @Output() elementMouseOut: EventEmitter<SVGEvent> = new EventEmitter();
    safeSvg: SafeHtml;
    readonly interactiveElements$: Observable<SVGElement[]> = this._interactiveElements.asObservable();
    // drag selection
    @ViewChild('dragSelectionDiv', { read: ElementRef })
    selectorDiv: ElementRef;

    selectionRectPosition = this._selectionRectPosition.asObservable();
    selectionRectVisible = false;

    constructor(
        private _domSanitizer: DomSanitizer,
        private _changeDetector: ChangeDetectorRef
    ) { }

    ngAfterViewChecked(): void {
        this.initFit();
        this.updateScrollPosition(.5, .5);
    }

    ngOnDestroy(): void {
        this.clearDragFeature();
    }

    /* This method initiates the references to the template elements, svgContainer template element must have always
     the same structure, only one child and this one another one inside. */
    @ViewChild('svgContainer', { read: ElementRef })
    set svgContainer(value: ElementRef) {
        this._svgContainer = value.nativeElement;
        this.initFit();
    }

    @Input()
    set svg(value: string) {
        if (value !== this._sourceSvg) {
            this._sourceSvg = value;
            if (this._interactiveElements.value) {
                this._interactiveElements.next(null);
            }
            if (value !== null) {
                if (this._svgContainer) {
                    this._svgContainer.style.overflow = 'hidden';
                }
                this._sourceSvg = value;
                this.safeSvg = this._domSanitizer.bypassSecurityTrustHtml(value);
                this._pendingInitialFitToContainer = true;
            } else {
                this._sourceSvg = null;
                this.safeSvg = null;
                this._svgElement = null;
            }
            this._changeDetector.markForCheck();
        }
    }

    fit(): void {
        this.resetSVGStyles();
        if (this._svgElement) {
            this._svgElement.classList.add(SVGDefs.fitSvgAnimationClass);
            this.applySVGStyles();
            setTimeout(() => {
                if (this._svgElement) {
                    this._svgElement.classList.remove(SVGDefs.fitSvgAnimationClass);
                }
                this._svgContainer.style.overflow = 'auto';
            }, this.fitAnimationDuration);
        }
    }

    reFit(): void {
        const totalPadding: number = 2 * this._svgPadding;
        if (this._svgStyles.width + totalPadding <= this._svgContainer.offsetWidth
            || this._svgStyles.height + totalPadding <= this._svgContainer.offsetHeight) {
            this.fit();
        }
    }

    // ZOOM

    zoomIn(): void {
        this.setZoomFactor(this.getCurrentScale() * this._zoomFactor);
    }

    zoomOut(): void {
        this.setZoomFactor(this.getCurrentScale() * (1 / this._zoomFactor));
    }

    // mouse click

    mouseDoubleClick(event: MouseEvent): void {
        this.elementDoubleClick.emit({
            element: this.getInteractiveElementOf(event.target as SVGElement),
            elements: null,
            ctrlKey: event.ctrlKey || event.metaKey,
            shiftKey: event.shiftKey
        });
    }

    mouseClick(event: MouseEvent): void {
        if (!this._skipNextClick) {
            this.elementClick.emit({
                element: this.getInteractiveElementOf(event.target as SVGElement),
                elements: null,
                ctrlKey: event.ctrlKey || event.metaKey,
                shiftKey: event.shiftKey
            });
            this.clearDragFeature();
        }
        this._skipNextClick = false;
    }

    mouseOver(event: MouseEvent): void {
        if (event.target) {
            const element = this.getInteractiveElementOf(event.target as SVGElement);
            if (element !== null) {
                const type = element.getAttribute(SVGDefs.typeProperty);
                const id = element.getAttribute(SVGDefs.idProperty);
                if (!this._currentOverElement
                    || (type !== this._currentOverElement.type || id !== this._currentOverElement.id)) {
                    if (this._currentOverElement !== null) {
                        this.elementMouseOut.emit({
                            element: this._currentOverElement.element,
                            elements: null,
                            ctrlKey: false,
                            shiftKey: false
                        });
                    }
                    this.elementMouseIn.emit({
                        element,
                        elements: null,
                        ctrlKey: false,
                        shiftKey: false
                    });
                    this._currentOverElement = { element, type, id };
                }
            } else if (this._currentOverElement !== null) {
                this.elementMouseOut.emit({
                    element: this._currentOverElement.element,
                    elements: null,
                    ctrlKey: false,
                    shiftKey: false
                });
                this._currentOverElement = null;
            }
        }
    }

    // drag features

    mouseDown(event: MouseEvent): void {
        this.startDragSelection(event.clientY, event.clientX, event.ctrlKey || event.metaKey, event.shiftKey, false);
    }

    touchStart(event: TouchEvent): void {
        // 1 touch: drag selection, 2 touches: drag scale move
        if (event.touches.length === 1) {
            const touch: Touch = event.touches.item(0);
            this.startDragSelection(touch.clientY, touch.clientX,
                event.ctrlKey || event.metaKey, event.shiftKey, true);
        } else if (event.touches.length === 2) {
            this.startDragScaleMove(event.touches);
        }
    }

    // FIT FUNCTIONS

    private initFit(): void {
        if (this._pendingInitialFitToContainer
            && this._svgContainer
            && this._svgContainer.children.length > 0) {
            this._svgContainer.childNodes.forEach(childNode => {
                // TYPE 1 === element (no commment)
                if (childNode.nodeType === 1) {
                    this._svgElement = childNode as SVGElement;
                }
            });
            this._svgElement.classList.add(SVGDefs.venueTemplateSvgClass);
            this._svgElement.setAttribute('draggable', 'false');
            this.fit();
            // fix for some devices that doesn't has enought time to fit correctly the view
            setTimeout(() => this.fit(), this.fitAnimationDuration + 1);
            this._pendingInitialFitToContainer = false;
            this._interactiveElements.next(
                Array.from(this._svgElement.getElementsByClassName(SVGDefs.interactiveClass)).map(e => e as SVGElement)
            );
        }
    }

    private resetSVGStyles(): void {
        const totalPadding = this._svgPadding * 2;
        this._svgStyles.height = Math.floor(this._svgContainer.offsetHeight) - totalPadding;
        this._svgStyles.width = Math.floor(this._svgContainer.offsetWidth) - totalPadding;
        this._svgStyles.top = this._svgPadding;
        this._svgStyles.left = this._svgPadding;
    }

    private applySVGStyles(): void {
        this._svgElement.style.height = this._svgStyles.height + this.pixelUnit;
        this._svgElement.style.width = this._svgStyles.width + this.pixelUnit;
        this._svgElement.style.top = this._svgStyles.top + this.pixelUnit;
        this._svgElement.style.left = this._svgStyles.left + this.pixelUnit;
    }

    private invalidateScrollPosition(relativeTop: number, relativeLeft: number): void {
        this._pendingScroll.originalPoint.top = (relativeTop * this._svgContainer.offsetHeight + this._svgContainer.scrollTop)
            / (this._svgStyles.height + this._svgStyles.top);
        this._pendingScroll.originalPoint.left = (relativeLeft * this._svgContainer.offsetWidth + this._svgContainer.scrollLeft)
            / (this._svgStyles.width + this._svgStyles.left);
        this._pendingScroll.pendingUpdate = true;
    }

    private updateScrollPosition(relativeTop: number, relativeLeft: number): void {
        if (this._pendingScroll.pendingUpdate) {
            this._svgContainer.scrollTop = ((this._svgStyles.height + this._svgStyles.top) * this._pendingScroll.originalPoint.top)
                - this._svgContainer.offsetHeight * relativeTop;
            this._svgContainer.scrollLeft = ((this._svgStyles.width + this._svgStyles.left) * this._pendingScroll.originalPoint.left)
                - this._svgContainer.offsetWidth * relativeLeft;
            this._pendingScroll.pendingUpdate = false;
        }
    }

    private getCurrentScale(): number {
        return this._svgStyles.width / this._svgContainer.offsetWidth;
    }

    private setZoomFactor(
        factor: number, oldRTop = .5, oldRLeft = .5, newRTop = .5, newRLeft = .5
    ): void {
        this.invalidateScrollPosition(oldRTop, oldRLeft);
        this.resetSVGStyles();
        this.applyZoomFactorToSVGStyles(factor);
        this.applySVGStyles();
        this.updateScrollPosition(newRTop, newRLeft);
    }

    private applyZoomFactorToSVGStyles(factor: number): void {
        this._svgStyles.height = this._svgStyles.height * factor;
        this._svgStyles.width = this._svgStyles.width * factor;
        this._svgStyles.top = Math.max((this._svgContainer.offsetHeight - this._svgStyles.height) / 2, this._svgPadding);
        this._svgStyles.left = Math.max((this._svgContainer.offsetWidth - this._svgStyles.width) / 2, this._svgPadding);
    }

    private getInteractiveElementOf(element: SVGElement): SVGElement {
        if (element.classList.contains(SVGDefs.interactiveClass)) {
            return element;
        } else if (element.parentElement instanceof SVGElement) {
            return this.getInteractiveElementOf(element.parentElement);
        } else {
            return null;
        }
    }

    // drag selection

    private startDragSelection(clientY: number, clientX: number, ctrlKey: boolean, shiftKey: boolean, isTouch: boolean): void {
        this.clearDragFeature();
        const ds = this._dragSelection;
        ds.ctrlKey = ctrlKey;
        ds.shiftKey = shiftKey;
        ds.initPoint = this.getPointPosition(clientY, clientX);
        ds.destPoint = ds.initPoint;
        this.refreshSelectionRect();
        this.selectionRectVisible = true;
        if (isTouch) {
            this._svgContainer.addEventListener('touchmove', this.touchMoveSelection.bind(this));
            this._svgContainer.ownerDocument.addEventListener('touchend', this.dragSelectionEnd.bind(this));
            this._svgContainer.ownerDocument.addEventListener('touchcancel', this.dragSelectionEnd.bind(this));
        } else {
            this._svgContainer.addEventListener('mousemove', this.mouseMove.bind(this));
            this._svgContainer.ownerDocument.addEventListener('mouseup', this.dragSelectionEnd.bind(this));
        }
    }

    private mouseMove(event: MouseEvent): void {
        if (event.buttons === 1) {
            this.moveDragSelection(event.clientY, event.clientX);
        } else {
            this.clearDragFeature();
        }
    }

    private touchMoveSelection(event: TouchEvent): void {
        if (event.touches.length === 1) {
            const touch: Touch = event.touches.item(0);
            this.moveDragSelection(touch.clientY, touch.clientX);
        }
    }

    private moveDragSelection(clientY: number, clientX: number): void {
        this._dragSelection.destPoint = this.getPointPosition(clientY, clientX);
        this.refreshSelectionRect();
    }

    private dragSelectionEnd(): void {
        if (this.selectorDiv) {
            const selectionRectBCR = this.selectorDiv.nativeElement.getBoundingClientRect();
            if (selectionRectBCR.width > 5 || selectionRectBCR.height > 5) {
                const selectedItems: SVGElement[] = [];
                const top1: number = selectionRectBCR.top;
                const top2: number = selectionRectBCR.top + selectionRectBCR.height;
                const left1: number = selectionRectBCR.left;
                const left2: number = selectionRectBCR.left + selectionRectBCR.width;
                for (const svgElement of this._interactiveElements.value) {
                    const seatBCR = svgElement.getBoundingClientRect();
                    if (top1 < (seatBCR.top + seatBCR.height)
                        && top2 > seatBCR.top
                        && left1 < (seatBCR.left + seatBCR.width)
                        && left2 > seatBCR.left) {
                        selectedItems.push(svgElement);
                    }
                }
                if (selectedItems.length > 0) {
                    this.elementsSurrounded.emit({
                        element: null,
                        elements: selectedItems,
                        ctrlKey: this._dragSelection.ctrlKey,
                        shiftKey: this._dragSelection.shiftKey
                    });
                    this._skipNextClick = true;
                }
            }
            this.clearDragFeature();
            this._changeDetector.markForCheck();
        }
    }

    private refreshSelectionRect(): void {
        const ds = this._dragSelection;
        const top: number = Math.min(ds.initPoint.top, ds.destPoint.top);
        const left: number = Math.min(ds.initPoint.left, ds.destPoint.left);
        this._selectionRectPosition.next({
            ['top.px']: top,
            ['left.px']: left,
            ['width.px']: Math.max(ds.initPoint.left, ds.destPoint.left) - left,
            ['height.px']: Math.max(ds.initPoint.top, ds.destPoint.top) - top
        });
    }

    // touch drag scale move

    private startDragScaleMove(touches: TouchList): void {
        this._dragScrollScale.initPoint = this.getTouchesPoint(touches);
        this._dragScrollScale.initScale = this._svgStyles.width / this._svgContainer.offsetWidth;
        this._svgContainer.addEventListener('touchmove', this.touchMoveScrollScale.bind(this));
        this._svgContainer.ownerDocument.addEventListener('touchend', this.clearDragFeature.bind(this));
        this._svgContainer.ownerDocument.addEventListener('touchcancel', this.clearDragFeature.bind(this));
    }

    private touchMoveScrollScale(event: TouchEvent): void {
        if (event.touches.length === 2) {
            this._dragScrollScale.destPoint = this.getTouchesPoint(event.touches);
            this.setZoomFactor(
                this._dragScrollScale.initScale * this._dragScrollScale.destPoint.distance / this._dragScrollScale.initPoint.distance,
                this._dragScrollScale.initPoint.top,
                this._dragScrollScale.initPoint.left,
                this._dragScrollScale.destPoint.top,
                this._dragScrollScale.destPoint.left);
        }
    }

    private getTouchesPoint(touchList: TouchList): { top: number; left: number; distance: number } {
        const point1 = this.getTouchPosition(touchList.item(0));
        const point2 = this.getTouchPosition(touchList.item(1));
        return {
            top: VenueTemplateSVGComponent.getNewRelativePointComponent(
                point1.top, point2.top, this._svgContainer.scrollTop, this._svgStyles.height, this._svgStyles.top),
            left: VenueTemplateSVGComponent.getNewRelativePointComponent(
                point1.left, point2.left, this._svgContainer.scrollLeft, this._svgStyles.width, this._svgStyles.left),
            distance:
                Math.sqrt(Math.pow(Math.abs(point1.top - point2.top), 2) + Math.pow(Math.abs(point1.left - point2.left), 2))
        };
    }

    private static getNewRelativePointComponent(
        p1: number, p2: number, containerScroll: number, svgSize: number, svgPosition: number
    ): number {
        return ((p1 + p2) / 2 + containerScroll) / (svgSize + svgPosition * 2);
    }

    // drag common

    private getTouchPosition(touch: Touch): { top: number; left: number } {
        return this.getPointPosition(touch.clientY, touch.clientX);
    }

    private getPointPosition(clientY: number, clientX: number): { top: number; left: number } {
        const svgContainerCR = this._svgContainer.getBoundingClientRect();
        return { top: clientY - svgContainerCR.top, left: clientX - svgContainerCR.left };
    }

    private clearDragFeature(): void {
        // drag selection flags
        this.selectionRectVisible = false;
        // mouse drag selection
        this._svgContainer.removeEventListener('pointermove', this.mouseMove.bind(this));
        this._svgContainer.ownerDocument.removeEventListener('pointerup', this.dragSelectionEnd.bind(this));
        // touch drag selection
        this._svgContainer.removeEventListener('touchmove', this.touchMoveSelection.bind(this));
        this._svgContainer.ownerDocument.removeEventListener('touchend', this.dragSelectionEnd.bind(this));
        this._svgContainer.ownerDocument.removeEventListener('touchcancel', this.dragSelectionEnd.bind(this));
        // touch drag move scale
        this._svgContainer.removeEventListener('touchmove', this.touchMoveScrollScale.bind(this));
        this._svgContainer.ownerDocument.removeEventListener('touchend', this.clearDragFeature.bind(this));
        this._svgContainer.ownerDocument.removeEventListener('touchcancel', this.clearDragFeature.bind(this));
    }
}
