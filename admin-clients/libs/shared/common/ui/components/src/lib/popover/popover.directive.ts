import { isHandset$ } from '@admin-clients/shared/utility/utils';
import { ViewportRuler } from '@angular/cdk/overlay';
import { Directive, ElementRef, Renderer2, inject, Input, OnInit, OnDestroy } from '@angular/core';
import { SatPopoverComponent } from '@ncstate/sat-popover';
import { firstValueFrom, Subject, takeUntil } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Directive({
    standalone: true,
    selector: '[appPopover]'
})
export class PopoverDirective implements OnInit, OnDestroy {
    private readonly _el = inject<ElementRef<HTMLElement>>(ElementRef);
    private readonly _renderer2 = inject(Renderer2);
    private readonly _viewportRuler = inject(ViewportRuler);
    private readonly _onDestroy = new Subject<void>();
    private readonly _isHandset$ = isHandset$();

    @Input() popover: SatPopoverComponent;

    ngOnInit(): void {
        this.popover.afterOpen
            .pipe(takeUntil(this._onDestroy))
            .subscribe(async () => {
                const isHandset = await firstValueFrom(this._isHandset$);
                if (!isHandset) {
                    this.setMaxHeight();
                }
            });

        this._viewportRuler.change(200)
            .pipe(
                debounceTime(200),
                takeUntil(this._onDestroy)
            )
            .subscribe(async () => {
                if (this.popover.isOpen()) {
                    const isHandset = await firstValueFrom(this._isHandset$);
                    if (!isHandset) {
                        this.setMaxHeight();
                    }
                }
            });

        this._isHandset$
            .pipe(takeUntil(this._onDestroy))
            .subscribe(isHandset => {
                if (isHandset) {
                    this._renderer2.removeStyle(this._el.nativeElement, 'max-height');
                }
            });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    private setMaxHeight(): void {
        const rect = this._el.nativeElement.getBoundingClientRect();
        const viewportHeight = this._viewportRuler.getViewportSize().height;
        const maxHeight = viewportHeight - rect.top;
        this._renderer2.setStyle(this._el.nativeElement, 'max-height', `${maxHeight}px`);
    }
}
