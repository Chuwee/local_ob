import { Directive, ElementRef, OnInit, inject } from '@angular/core';

//Expermiental SCSS selector, don't use in cpanel or channels
@Directive({
    selector: '[appCustomScroll]',
    standalone: true
})
export class CustomScrollDirective implements OnInit {
    private readonly _element = inject(ElementRef);
    private _hostElement: HTMLElement;

    ngOnInit(): void {
        this._hostElement = this._element.nativeElement;
        this._hostElement?.classList.add('custom-scroll-vertical');
    }
}
