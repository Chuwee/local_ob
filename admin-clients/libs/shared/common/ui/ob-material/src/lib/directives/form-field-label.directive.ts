/* eslint-disable @angular-eslint/directive-selector */
import { Directive, ElementRef, OnDestroy, OnInit, inject } from '@angular/core';

/**
 * Angular directive that adds a class to each material form field that has a mat label inside  
 */
@Directive({
    selector: 'mat-form-field.ob-form-field',
    standalone: true
})
export class ObFormFieldLabelDirective implements OnInit, OnDestroy {

    readonly #class = 'field-with-label';
    readonly #observer = new MutationObserver(() => this.checkForClass());
    readonly #element = inject<ElementRef<HTMLElement>>(ElementRef).nativeElement;

    ngOnInit(): void {
        this.#observer.observe(this.#element, {
            attributes: true, childList: true, subtree: true
        });
    }

    ngOnDestroy(): void {
        this.#observer.disconnect();
    }

    private checkForClass(): void {
        const hasClass = this.#element.classList.contains(this.#class);

        if (hasClass) {
            this.#observer.disconnect();
            return;
        }

        const appearance = this.#element.getAttribute('appearance');
        const value = this.#element.querySelector('mat-label');

        if (value && appearance === 'outline' && !hasClass) {
            this.#element.classList.add(this.#class);
            this.#observer.disconnect();
        }

        if (!!value && !!appearance) {
            this.#observer.disconnect();
        }
    }

}
