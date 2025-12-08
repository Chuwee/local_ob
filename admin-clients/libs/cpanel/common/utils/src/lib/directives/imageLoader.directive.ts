import { Directive, ElementRef, OnInit } from '@angular/core';

@Directive({
    // eslint-disable-next-line @angular-eslint/directive-selector
    selector: 'img[src]',
    standalone: true
})
export class ImageLoaderDirective implements OnInit {

    constructor(private _elemRef: ElementRef<HTMLImageElement>) { }

    ngOnInit(): void {
        if (this._elemRef.nativeElement.src) {
            const originalSource = new URL(this._elemRef.nativeElement.src);
            if (window.location.hostname === originalSource.hostname &&
                originalSource.pathname.split('/')[1] === 'assets') {
                this._elemRef.nativeElement.src = originalSource.pathname;
            }
        }
    }

}

