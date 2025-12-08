import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { TemplatePortal } from '@angular/cdk/portal';
import {
    Directive, ElementRef, Input, OnDestroy, OnInit, TemplateRef, ViewContainerRef, inject
} from '@angular/core';
import { Subject, takeUntil, BehaviorSubject, merge, debounceTime, filter, fromEvent } from 'rxjs';

@Directive({
    standalone: true,
    selector: '[appHoverOverlay]'
})
export class HoverOverlayDirective implements OnInit, OnDestroy {
    private readonly _onDestroy = new Subject<void>();
    private readonly _activeListeners = new BehaviorSubject<boolean>(false);
    private readonly _overlay = inject(Overlay);
    private readonly _viewContainerRef = inject(ViewContainerRef);
    private readonly _elementRef = inject(ElementRef);
    private _templatePortal: TemplatePortal;
    private _overlayRef: OverlayRef;
    private _disabled = false;

    @Input() appHoverOverlay: TemplateRef<unknown>;
    @Input() set overlayDisabled(disabled: boolean) {
        this._disabled = disabled;
        this.initListeners();
    }

    ngOnInit(): void {
        this._overlayRef = this._overlay.create({
            positionStrategy: this._overlay.position()
                .flexibleConnectedTo(this._elementRef)
                .withPositions([{
                    originX: 'start',
                    originY: 'bottom',
                    overlayX: 'start',
                    overlayY: 'top'
                }, {
                    originX: 'start',
                    originY: 'top',
                    overlayX: 'start',
                    overlayY: 'bottom'
                }])
        });

        this._templatePortal = new TemplatePortal(this.appHoverOverlay, this._viewContainerRef);

        this.initListeners();
    }

    ngOnDestroy(): void {
        this._activeListeners.next(null);
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    startListeners(): void {
        if (this._activeListeners.value) {
            return;
        }

        this._activeListeners.next(true);

        const open$ = fromEvent(document, 'mousemove').pipe(
            debounceTime(500),
            filter(event => this._elementRef.nativeElement.contains(event.target))
        );

        open$.pipe(
            takeUntil(this._activeListeners.pipe(
                filter(listener => !listener)
            ))
        ).subscribe(() => this.changeState(true));

        const mouseout = fromEvent(document, 'mouseout').pipe(
            debounceTime(100),
            filter(event => this.isMovedOutside(event as MouseEvent))
        );

        const wheel = fromEvent(document, 'wheel').pipe(
            filter(event => this._elementRef.nativeElement.contains(event.target))
        );

        const close$ = merge(mouseout, wheel);

        close$.pipe(
            takeUntil(this._activeListeners.pipe(
                filter(listener => !listener)
            ))
        ).subscribe(() => {
            this.changeState(false);
        });
    }

    removeListeners(): void {
        this._activeListeners.next(false);
    }

    private changeState(open: boolean): void {
        open && !this._overlayRef.hasAttached() ? this._overlayRef.attach(this._templatePortal) : this._overlayRef.detach();
    }

    private isMovedOutside(event: MouseEvent): boolean {
        return !(this._elementRef.nativeElement.contains(event.target) ||
            this._overlayRef.hostElement.contains(event.target as HTMLElement));
    }

    private initListeners(): void {
        this._disabled ? this.removeListeners() : this.startListeners();
    }
}
