import { Directive, ElementRef, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { debounceTime, distinctUntilChanged, Observable, Subject, Subscription } from 'rxjs';

@Directive({
    selector: '[appResize]',
    standalone: true
})
export class ResizeObserverDirective implements OnInit, OnDestroy {

    private readonly _elementRef = inject(ElementRef<HTMLElement>);
    private readonly _resizeTrigger = new Subject<{ width: number; height: number }>();
    private readonly _resizeObserver = new ResizeObserver(() => this._resizeTrigger.next(
        { width: this._elementRef.nativeElement.clientWidth, height: this._elementRef.nativeElement.clientHeight })
    );

    private _resizeTriggerSubscription: Subscription;

    @Output('appResize') readonly resizeEmitter = new EventEmitter<{ width: number; height: number }>();

    @Input() resizeDelay = 100;

    ngOnInit(): void {
        this._resizeObserver.observe(this._elementRef.nativeElement);
        let triggerObs: Observable<{ width: number; height: number }>;
        // variable debounceTime optional setting, no value, 0 or negative values, are like no delay.
        this.resizeDelay = Number(this.resizeDelay);
        if (!Number.isNaN(this.resizeDelay) && this.resizeDelay > 0) {
            triggerObs = this._resizeTrigger.pipe(debounceTime(this.resizeDelay));
        } else {
            triggerObs = this._resizeTrigger.pipe(distinctUntilChanged());
        }
        this._resizeTriggerSubscription = triggerObs.subscribe(resize => this.resizeEmitter.next(resize));
    }

    ngOnDestroy(): void {
        this._resizeObserver.unobserve(this._elementRef.nativeElement);
        this._resizeObserver.disconnect();
        this._resizeTriggerSubscription?.unsubscribe();
    }
}
