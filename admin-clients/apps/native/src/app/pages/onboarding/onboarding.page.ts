import { ChangeDetectionStrategy, Component, ElementRef, OnInit, ViewChild, inject } from '@angular/core';
import { Router } from '@angular/router';
import { App as CapacitorApp } from '@capacitor/app';
import { SwiperContainer } from 'swiper/element';
import { DeviceStorage } from '../../core/services/deviceStorage';

@Component({
    selector: 'onboarding',
    templateUrl: './onboarding.page.html',
    styleUrls: ['./onboarding.page.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class OnboardingPage implements OnInit {
    private readonly _deviceStorage = inject(DeviceStorage);
    private readonly _router = inject(Router);

    @ViewChild('swiper') private readonly _swiperRef: ElementRef;
    backButtonActivated = false;

    ngOnInit(): void {
        CapacitorApp.addListener('backButton', () => {
            if (this._swiperRef?.nativeElement?.swiper.activeIndex > 0) {
                this._swiperRef.nativeElement.swiper.slidePrev();
            }
        });
    }

    onSlideChange(e: Event): void {
        const swiperContainer: SwiperContainer = e.target as SwiperContainer;
        const swiper = swiperContainer.swiper;
        this.backButtonActivated = swiper.activeIndex > 0 ? true : false;
    }

    onSkip(): void {
        this._deviceStorage.setItem('onboarding-executed', true);
        this._router.navigate(['/login']);
    }

    onBackSlide(): void {
        this._swiperRef.nativeElement?.swiper.slidePrev();
    }
}
