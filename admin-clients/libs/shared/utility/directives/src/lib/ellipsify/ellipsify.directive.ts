import { isPlatformBrowser } from '@angular/common';
import {
    AfterViewInit, Attribute, Directive, ElementRef,
    Inject, OnDestroy, OnInit, Optional, PLATFORM_ID, Renderer2, Self
} from '@angular/core';
import { MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EllipsifyInstancesService } from './ellipsify-instances.service';

@Directive({
    selector: '[appEllipsify]',
    standalone: true
})
export class EllipsifyDirective implements OnInit, AfterViewInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _domElement: HTMLElement;

    private get _hasOverflow(): boolean {
        return (this._domElement.offsetWidth < this._domElement.scrollWidth) ||
            (this._domElement.clientHeight < this._domElement.scrollHeight);
    }

    constructor(
        private _renderer: Renderer2,
        private _elementRef: ElementRef<HTMLElement>,
        private _ellipsifyInstancesService: EllipsifyInstancesService,
        @Self() @Optional() private _matTooltip: MatTooltip,
        @Optional() private _matSelect: MatSelect,
        @Inject(PLATFORM_ID) private _platformId: unknown,
        @Optional() @Attribute('class') private _classNames: string
    ) { }

    ngOnInit(): void {
        this._domElement = this._elementRef.nativeElement;
        // setting compulsory required styles to the DOM element
        /* eslint-disable @typescript-eslint/naming-convention */
        const elipsifyStyles = {
            'text-overflow': 'ellipsis',
            overflow: 'hidden',
            'white-space': this.whiteSpace
        };
        /* eslint-enable @typescript-eslint/naming-convention */
        Object.keys(elipsifyStyles).forEach(style => {
            this._renderer.setStyle(
                this._domElement, `${style}`, elipsifyStyles[style]
            );
        });
    }

    // to check and set disable tooltip on the element at the time when node renders first time.
    ngAfterViewInit(): void {
        this._renderer.setProperty(this._domElement, 'scrollTop', 1);
        this.setDisableTooltip();
        //For displaying tooltip correctly on select options
        if (this._matSelect) {
            this._matSelect?.openedChange
                .pipe(takeUntil(this._onDestroy))
                .subscribe(isOpen => {
                    if (isOpen) {
                        this.setDisableTooltip();
                    }
                });
        } else {
            //Register instances service to improve performance on tooltips
            this._ellipsifyInstancesService.registerInstance(this);
        }
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        if (!this._matSelect) {
            this._ellipsifyInstancesService.unregisterInstance(this);
        }
    }

    setDisableTooltip(): void {
        // to set disable tooltip on the element when it is changing width.
        if (this._matTooltip) {
            const isBrowser = isPlatformBrowser(this._platformId);
            this._matTooltip.disabled = !isBrowser || !this._hasOverflow;
        }
    }

    get whiteSpace(): string {
        return this._classNames?.includes('ellipsify-no-wrap') ? 'normal' : 'nowrap';
    }
}
