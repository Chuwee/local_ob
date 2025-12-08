import { Directive, ElementRef, OnInit } from '@angular/core';
import { Capacitor } from '@capacitor/core';

@Directive({
    selector: '[appAutofill]',
    standalone: false
})
export class AutofillDirective implements OnInit {

    constructor(private _el: ElementRef) {
    }

    ngOnInit(): void {
        if (Capacitor.getPlatform() !== 'ios') { return; }
        setTimeout(() => {
            try {
                this._el.nativeElement.children[0].addEventListener('change', e => {
                    this._el.nativeElement.value = (e.target).value;
                });
            } catch { /* empty */ }
        }, 100);
    }
}
