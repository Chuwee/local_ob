import { User } from '@admin-clients/cpanel/core/data-access';
import { EntityUser, EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, EventEmitter, OnInit, Output, inject } from '@angular/core';
import { ModalController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs';
import { DeviceStorage } from 'apps/native/src/app/core/services/deviceStorage';
import { LanguageOptions } from './languages';

//TODO: Las traducciones deberían estar en weblate, no hardcoded en un JSON
//Ahora mismo sólo hay disponible el español
@Component({
    selector: 'language-selector',
    templateUrl: './language-selector.component.html',
    styleUrls: ['./language-selector.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class LanguageSelectorComponent implements OnInit {
    private readonly _translateService = inject(TranslateService);
    private readonly _deviceStorage = inject(DeviceStorage);
    private readonly _entityUsersService = inject(EntityUsersService);
    private readonly _changeDetector = inject(ChangeDetectorRef);
    private readonly _modalCtrl = inject(ModalController);
    readonly languages = Object.values(LanguageOptions).map(language => ({ code: language }));

    @Output() readonly saveLanguage: EventEmitter<string> = new EventEmitter();
    alertOpen = false;
    alertButtons = [];
    userId: number;
    userLang: string;
    selectedLang: string;

    ngOnInit(): void {
        this._entityUsersService.loadEntityUser('myself');
        this._deviceStorage.getItem('user_data').pipe(
            map((userData: string) => {
                const user: User = JSON.parse(userData);
                this.userId = user.id;
                return user.language.toUpperCase();
            })
        ).subscribe((userLang: string) => {
            this.userLang = userLang;
            this.selectedLang = userLang;
            this._changeDetector.detectChanges();
        });

        this.loadModalButtons();
    }

    changeLang(selectedLang: string): void {
        this.selectedLang = selectedLang;
    }

    toggleAlert(): void {
        this.alertOpen = !this.alertOpen;
    }

    saveOptions(): void {
        const subscription = this._entityUsersService.getEntityUser$().pipe(
            map((entityUser: EntityUser) => {
                const updatedEntityUser: EntityUser = {
                    ...entityUser,
                    language: this.selectedLang
                };
                return updatedEntityUser;
            }),
            map((updatedEntityUser: EntityUser) => {
                this._entityUsersService.updateEntityUser('myself', updatedEntityUser).subscribe();
                return updatedEntityUser;
            })
        ).subscribe(updatedEntityUser => {
            this._deviceStorage.setItem('user_data', JSON.stringify(updatedEntityUser));

            //TODO: Por ahora no tenemos todas las traducciones, habilitar esto cuando estén disponbiles
            // this._translateService.use(updatedEntityUser.language);
            this._modalCtrl.dismiss({ selectedLang: this.selectedLang });
        });
        subscription.unsubscribe();
    }

    onBack(): void {
        this.userLang !== this.selectedLang ? this.toggleAlert() : this._modalCtrl.dismiss();
    }

    private loadModalButtons(): void {
        this._translateService.get(['BUTTONS.EXIT', 'BUTTONS.CANCEL'])
            .subscribe(translation => {
                this.alertButtons = [
                    {
                        text: translation['BUTTONS.EXIT'],
                        role: 'confirm',
                        cssClass: 'ob-btn ghost size--small',
                        // eslint-disable-next-line @typescript-eslint/naming-convention
                        htmlAttributes: { 'data-override-styles': '' },
                        handler: () => { this._modalCtrl.dismiss(); }
                    },
                    {
                        text: translation['BUTTONS.CANCEL'],
                        role: 'cancel',
                        cssClass: 'ob-btn primary size--small',
                        // eslint-disable-next-line @typescript-eslint/naming-convention
                        htmlAttributes: { 'data-override-styles': '' }
                    }
                ];
            });
    }
}
