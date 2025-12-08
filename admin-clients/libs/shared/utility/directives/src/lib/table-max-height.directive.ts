import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import {
    AfterViewInit,
    Directive,
    ElementRef,
    inject,
    Input,
    OnChanges,
    OnDestroy,
    PLATFORM_ID,
    Renderer2,
    SimpleChanges
} from '@angular/core';

/**
 * Directive that reads the height of the element it's applied to (source element)
 * and updates the max-height of a target element to prevent double overflow.
 * 
 * Usage:
 * <div appTableMaxHeight [targetElement]="tableElement">
 *   <!-- Source element content -->
 * </div>
 * <mat-table #tableElement class="ob-table ob-table-drop-list">
 *   <!-- Table content -->
 * </mat-table>
 */
@Directive({
    selector: '[appTableMaxHeight]',
    standalone: true
})
export class TableMaxHeightDirective implements AfterViewInit, OnChanges, OnDestroy {
    private readonly _elementRef = inject(ElementRef<HTMLElement>);
    private readonly _renderer = inject(Renderer2);
    private readonly _platformId = inject(PLATFORM_ID);

    readonly #document = inject(DOCUMENT);

    private _resizeObserver?: ResizeObserver;
    private _containerElement?: HTMLElement;

    /**
     * Reference to the target element whose max-height should be updated.
     * Can be an ElementRef, HTMLElement, or Angular component instance.
     */
    @Input() targetElement?: ElementRef<HTMLElement> | HTMLElement | { elementRef?: ElementRef<HTMLElement> };

    /**
     * Optional offset in pixels to subtract from the calculated max-height.
     * Useful for margins, padding, or other spacing.
     */
    @Input() offset: number = 0;

    ngAfterViewInit(): void {
        if (!isPlatformBrowser(this._platformId)) {
            return;
        }
        this.initializeDirective();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['targetElement'] && !changes['targetElement'].firstChange && isPlatformBrowser(this._platformId)) {
            if (this._resizeObserver) {
                this._resizeObserver.disconnect();
            }
            this.initializeDirective();
        }
    }

    ngOnDestroy(): void {
        if (this._resizeObserver) {
            this._resizeObserver.disconnect();
        }
    }

    private initializeDirective(): void {
        // Wait a tick to ensure targetElement is available (in case ViewChild hasn't been set yet)
        setTimeout(() => {
            if (!this.targetElement) {
                console.warn('[appTableMaxHeight] targetElement is not set');
                return;
            }

            this._containerElement = this.#document.documentElement;

            if (!this._containerElement) {
                console.warn('[appTableMaxHeight] Could not find container element');
                return;
            }

            const target = this.getTargetElement();
            if (!target) {
                console.warn('[appTableMaxHeight] Could not resolve target element');
                return;
            }

            this._resizeObserver = new ResizeObserver(() => {
                this.updateMaxHeight();
            });

            this._resizeObserver.observe(this._elementRef.nativeElement);
            this._resizeObserver.observe(this._containerElement);

            this.updateMaxHeight();
        }, 0);
    }

    private updateMaxHeight(): void {
        if (!this._containerElement || !this.targetElement) {
            return;
        }

        const target = this.getTargetElement();
        if (!target) {
            return;
        }

        const sourceElement = this._elementRef.nativeElement;
        const sourceHeight = sourceElement.offsetHeight;

        const containerHeight = this._containerElement === this.#document.documentElement ?
            window.innerHeight : this._containerElement.offsetHeight;

        const offsetValue = typeof this.offset === 'number' && !isNaN(this.offset) ? this.offset : 0;

        const availableHeight = containerHeight - sourceHeight - offsetValue;

        const minHeight = 200;
        const calculatedHeight = Math.max(availableHeight, minHeight);

        this._renderer.setStyle(target, 'max-height', `${calculatedHeight}px`);
        this._renderer.setStyle(target, 'overflow-y', 'auto');
    }

    private getTargetElement(): HTMLElement | null {
        if (!this.targetElement) {
            return null;
        }

        if (this.targetElement instanceof HTMLElement) {
            return this.targetElement;
        }

        if (this.targetElement instanceof ElementRef) {
            return this.targetElement.nativeElement;
        }

        // Handle Angular component instances - try to access _elementRef (private property)
        if (this.targetElement && typeof this.targetElement === 'object') {
            // Try accessing _elementRef (common in Angular Material components)
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const elementRef = (this.targetElement as any)._elementRef;
            if (elementRef instanceof ElementRef) {
                return elementRef.nativeElement;
            }

            // Try accessing elementRef (public property)
            if ('elementRef' in this.targetElement) {
                const publicElementRef = (this.targetElement as { elementRef: ElementRef<HTMLElement> }).elementRef;
                if (publicElementRef instanceof ElementRef) {
                    return publicElementRef.nativeElement;
                }
            }
        }

        return null;
    }
}

