import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { NavController, Platform } from '@ionic/angular';
import { IonApp, IonRouterOutlet } from '@ionic/angular/standalone';
import { TranslateService } from '@ngx-translate/core';
import { addIcons } from 'ionicons';
import { chevronForwardOutline, chevronBackOutline, caretDownOutline, close, chevronDownOutline } from 'ionicons/icons';
import { first, firstValueFrom } from 'rxjs';
import { register } from 'swiper/element/bundle';
import { BreadcrumbService } from './core/components/back-button/services/breadcrumbs.service';
import { DeviceStorage } from './core/services/deviceStorage';
import { AuthService } from './modules/auth/services/auth.service';

register();
@Component({
    selector: 'admin-clients-root',
    templateUrl: 'app.component.html',
    styleUrls: ['app.component.scss'],
    imports: [IonApp, IonRouterOutlet],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent {
    readonly #translateService = inject(TranslateService);
    readonly #router = inject(Router);
    readonly #breadcrumbService = inject(BreadcrumbService);
    readonly #authService = inject(AuthService);
    readonly #deviceStorage = inject(DeviceStorage);
    readonly #platform = inject(Platform);
    readonly #auth = inject(AuthService);
    readonly #navCtrl = inject(NavController);

    selectedLanguage = 'es-ES';

    constructor() {
        addIcons({ chevronForwardOutline, chevronBackOutline, caretDownOutline, close, chevronDownOutline });
        this.#translateService.setFallbackLang(this.selectedLanguage);
        this.#translateService.use(this.selectedLanguage);
        this.#deviceStorage.init();
        this.#breadcrumbService.init();

        const getTokenFromStorage$ = this.#auth.getTokenFromStorage();
        firstValueFrom(getTokenFromStorage$).then(token => {
            if (token) {
                this.#authService.getLoggedUser$().pipe(first()).subscribe(() => {
                    this.#router.navigate(['/tabs']).then();
                });
            }
        });

        this.#platform.backButton.subscribeWithPriority(9999, () => {
            this.#navCtrl.back();
        });
    }
}
