import { getIntersectionObserverEntries$ } from '@admin-clients/shared/utility/utils';
import { AfterViewInit, DestroyRef, Directive, ElementRef, EventEmitter, inject, Output } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Directive({
    selector: '[appIntersectionListener]',
    standalone: true
})
export class IntersectionListenerDirective implements AfterViewInit {
    private readonly _destroyRef = inject(DestroyRef);
    private readonly _element = inject(ElementRef);

    @Output() appIntersectionListener = new EventEmitter<boolean>();

    ngAfterViewInit(): void {
        getIntersectionObserverEntries$(this._element.nativeElement, { threshold: 0.5 })
            .pipe(takeUntilDestroyed(this._destroyRef))
            .subscribe(entries => this.appIntersectionListener.emit(entries[0].isIntersecting));
    }
}
