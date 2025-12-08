import { Directive, ElementRef, inject, OnInit } from '@angular/core';
import { MatTooltip } from '@angular/material/tooltip';

@Directive({
    standalone: true,
    selector: '[obErrorIcon]',
    hostDirectives: [
        { directive: MatTooltip, inputs: ['matTooltip: obErrorIcon'] }
    ]
})
export class ErrorIconDirective implements OnInit {
    readonly #elementRef = inject(ElementRef<HTMLElement>);
    readonly #matTooltip = inject(MatTooltip);

    ngOnInit(): void {
        this.#elementRef.nativeElement.classList.add('ob-icon', 'xsmall', 'error');
        this.#matTooltip.tooltipClass = 'ob-tooltip-error ob-tooltip-nowrap';
    }
}
