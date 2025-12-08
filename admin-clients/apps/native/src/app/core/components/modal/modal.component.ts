import { NgClass } from '@angular/common';
import { ChangeDetectionStrategy, Component, ElementRef, EventEmitter, inject, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Animation, AnimationController, Gesture, GestureController, GestureDetail, IonicModule } from '@ionic/angular';

@Component({
    selector: 'modal',
    imports: [IonicModule, NgClass],
    templateUrl: './modal.component.html',
    styleUrls: ['./modal.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ModalComponent implements OnInit {
    private readonly _animationCtrl = inject(AnimationController);
    private readonly _gestureCtrl = inject(GestureController);

    @ViewChild('modal', { read: ElementRef }) private readonly _modal: ElementRef;

    private _animation: Animation;
    private _gesture: Gesture;
    private _started = false;
    private _startHidePosition = 0;
    private _maxDisplacement: number;
    private _fullyOpenPosition: number;
    private _dismissalPosition: string;

    @Input() readonly modalTitle: string;
    @Input() readonly backdropDismiss = true;
    @Input() isOpen: boolean;
    @Input() position: 'top' | 'bottom' = 'bottom';

    @Output() readonly openModal = new EventEmitter();
    @Output() readonly closeModal = new EventEmitter();

    ngOnInit(): void {
        this._dismissalPosition = this.position === 'top' ? '-100%' : '100%';
    }

    enterAnimation = (baseEl: HTMLElement): Animation => {
        const root = baseEl.shadowRoot;

        const backdropAnimation = this._animationCtrl
            .create()
            .addElement(root.querySelector('ion-backdrop'))
            .fromTo('opacity', '0', '0.65');

        const wrapperAnimation = this._animationCtrl
            .create()
            .addElement(root.querySelector('.modal-wrapper'))
            .beforeStyles({})
            .fromTo('transform', `translateY(${this._dismissalPosition})`, 'translateY(0)')
            .fromTo('opacity', 0, 1);

        return this._animationCtrl
            .create()
            .addElement(baseEl)
            .easing('ease-out')
            .duration(500)
            .addAnimation([backdropAnimation, wrapperAnimation]);
    };

    leaveAnimation = (baseEl: HTMLElement): Animation => {
        const root = baseEl.shadowRoot;

        const backdropAnimation = this._animationCtrl
            .create()
            .addElement(root.querySelector('ion-backdrop'))
            .fromTo('opacity', '0.65', '0');

        const wrapperAnimation = this._animationCtrl
            .create()
            .addElement(root.querySelector('.modal-wrapper'))
            .beforeStyles({})
            .fromTo('transform', `translateY(${this._fullyOpenPosition}px)`, `translateY(${this._dismissalPosition})`)
            .fromTo('opacity', 1, 0);

        return this._animationCtrl
            .create()
            .addElement(baseEl)
            .easing('ease-out')
            .duration(500)
            .addAnimation([backdropAnimation, wrapperAnimation]);
    };

    modalDidPresentHandle(): void {
        this.openModal.emit();
        this._fullyOpenPosition = 0;

        if (this.backdropDismiss) {
            this._maxDisplacement = this._modal.nativeElement.shadowRoot.querySelector('.modal-wrapper').offsetHeight;

            this._animation = this._animationCtrl
                .create()
                .addElement(this._modal.nativeElement.shadowRoot.querySelector('.modal-wrapper'))
                .duration(1000)
                .fromTo('transform', 'translateY(0)', `translateY(${this._dismissalPosition})`);

            const gesture = (this._gesture = this._gestureCtrl.create({
                el: this._modal.nativeElement.querySelector('.ob-modal__custom-handler'),
                threshold: 0,
                gestureName: 'modal-drag',
                onMove: ev => this.onMove(ev),
                onEnd: ev => this.onEnd(ev)
            }));

            gesture.enable(true);
        }

    }

    onCloseModal(): void {
        this.closeModal.emit();
        this.isOpen = false;
    }

    private onMove(ev: GestureDetail): void {
        if (!this._started) {
            this._animation.progressStart();
            this._started = true;
        }

        this._animation.progressStep(this.getStep(ev));
    }

    private onEnd(ev: GestureDetail): void {
        if (!this._started) {
            return;
        }

        this._gesture.enable(false);

        const step = this.getStep(ev);
        const shouldComplete = step > 0.5;

        this._animation.progressEnd(shouldComplete ? 1 : 0, step).onFinish(() => {
            this._gesture.enable(true);
        });

        if (shouldComplete) {
            this._modal.nativeElement.dismiss();
            this._startHidePosition = 0;
        } else {
            this._fullyOpenPosition = 0;
        }

        this._started = false;

    }

    private clamp(min: number, n: number, max: number): number {
        return Math.max(min, Math.min(n, max));
    }

    private getStep(ev: GestureDetail): number {
        const delta = this._startHidePosition + (this.position === 'top' ? -ev.deltaY : ev.deltaY);
        this._fullyOpenPosition = this.position === 'top' ? -delta : delta;

        return this.clamp(0, delta / this._maxDisplacement, 1);
    }
}
