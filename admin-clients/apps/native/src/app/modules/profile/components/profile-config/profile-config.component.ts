import { User } from '@admin-clients/cpanel/core/data-access';
import { EntityUser, EntityUserStatus, EntityUsersService } from '@admin-clients/cpanel/organizations/entity-users/data-access';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, NgZone, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, map, Subject } from 'rxjs';
import { DeviceStorage } from 'apps/native/src/app/core/services/deviceStorage';
import { getInitials } from 'apps/native/src/app/helpers/string.utils';
import { AuthService } from '../../../auth/services/auth.service';
import { AlertType } from './models/profile-config.model';

@Component({
    selector: 'profile-config',
    templateUrl: './profile-config.component.html',
    styleUrls: ['./profile-config.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ProfileConfigComponent implements OnInit {
    private readonly _translateService = inject(TranslateService);
    private readonly _deviceStorage = inject(DeviceStorage);
    private readonly _authService = inject(AuthService);
    private readonly _entityUsersService = inject(EntityUsersService);
    private readonly _changeDetector = inject(ChangeDetectorRef);
    private readonly _router = inject(Router);
    private readonly _ngZone = inject(NgZone);

    private _alerts: AlertType[] = [
        {
            key: 'deactivation',
            isSelected: false,
            header: 'PROFILE.ALERT-DEACTIVATE-ACCOUNT',
            buttons: [
                {
                    text: '',
                    role: 'confirm',
                    cssClass: 'ob-btn ghost size--small',
                    // eslint-disable-next-line @typescript-eslint/naming-convention
                    htmlAttributes: { 'data-override-styles': '' },
                    handler: () => {
                        this.deactivateAccount();
                    }
                },
                {
                    text: '',
                    role: 'cancel',
                    cssClass: 'ob-btn primary size--small',
                    // eslint-disable-next-line @typescript-eslint/naming-convention
                    htmlAttributes: { 'data-override-styles': '' }
                }
            ]
        },
        {
            key: 'logout',
            isSelected: false,
            header: 'PROFILE.ALERT-LOGOUT',
            buttons: [
                {
                    text: '',
                    role: 'confirm',
                    cssClass: 'ob-btn ghost size--small',
                    // eslint-disable-next-line @typescript-eslint/naming-convention
                    htmlAttributes: { 'data-override-styles': '' },
                    handler: () => {
                        this.logout();
                    }
                },
                {
                    text: '',
                    role: 'cancel',
                    cssClass: 'ob-btn primary size--small',
                    // eslint-disable-next-line @typescript-eslint/naming-convention
                    htmlAttributes: { 'data-override-styles': '' }
                }
            ]
        }
    ];

    readonly isLoading$ = new BehaviorSubject<boolean>(true);
    readonly isError$ = new BehaviorSubject<boolean>(false);
    readonly username$ = new BehaviorSubject<string>('');
    readonly initials$ = new BehaviorSubject<string>('');
    readonly usermail$ = new BehaviorSubject<string>('');
    readonly user$ = new Subject<User>();

    selectedLang: string;
    alertOpen = false;
    isSelectingLang = false;

    ngOnInit(): void {
        this.loadDataFromDeviceStorage();
        this.loadModalButtons();
        this._entityUsersService.loadEntityUser('myself');

    }

    get selectedAlert(): AlertType {
        return this._alerts.find(alert => alert.isSelected);
    }

    activateSelectingLang(): void {
        this.isSelectingLang = true;
    }

    deactivateSelectingLang(e): void {
        this.isSelectingLang = false;
        const data = e.detail.data;
        if (data?.selectedLang) {
            this.translateLang(data.selectedLang);
        }
    }

    logout(): void {
        this._authService.logout();
        this._ngZone.run(() => {
            this._router.navigate(['/login']);
        });
    }

    showAlert(type: 'logout' | 'deactivation'): void {
        this._alerts.find(alert => alert.key === type).isSelected = true;
        this.alertOpen = true;
    }

    hideAlert(): void {
        this._alerts = this._alerts.map(alert => ({ ...alert, isSelected: false }));
        this.alertOpen = false;
    }

    reTry(): void {
        this.loadDataFromDeviceStorage();
    }

    //TODO: Això hauria de ser un eliminar compte, no desactivarlo
    private deactivateAccount(): void {
        const subscription = this._entityUsersService.getEntityUser$()
            .pipe(
                map((entityUser: EntityUser) => {
                    const updatedEntityUser: EntityUser = {
                        ...entityUser,
                        status: EntityUserStatus.blocked
                    };
                    return updatedEntityUser;
                }),
                map((updatedEntityUser: EntityUser) => {
                    this._entityUsersService.updateEntityUser('myself', updatedEntityUser).subscribe();
                })
            ).subscribe(() => {
                this.logout();
            });

        subscription.unsubscribe();
    }

    private loadDataFromDeviceStorage(): void {
        this._deviceStorage.getItem('user_data')
            .pipe(
                map((dataString: string) => {
                    if (dataString === null) {
                        throw Error('Empty storage');
                    } else {
                        this.isError$.next(false);
                        return JSON.parse(dataString);
                    }
                })
            ).subscribe({
                next: (user: User) => {
                    if (user) {
                        this.isLoading$.next(false);
                        this.initials$.next(
                            getInitials(user.name, user.last_name)
                        );
                        this.username$.next(`${user.name} ${user.last_name}`);
                        this.usermail$.next(user.email);
                        this.translateLang(user.language);
                    }
                },
                error: () => {
                    this.isError$.next(true);
                }
            });
    }

    private translateLang(lang: string): void {
        this._translateService.get('LANG-SELECTOR.' + lang.toUpperCase())
            .subscribe(translatedLang => {
                this.selectedLang = translatedLang;
                this._changeDetector.detectChanges();
            });
    }

    private loadModalButtons(): void {
        this._translateService.get([
            'BUTTONS.YES',
            'BUTTONS.EXIT',
            'BUTTONS.NO',
            'BUTTONS.CANCEL'
        ]).subscribe(translation => {
            const translationKeys = Object.keys(translation);
            translationKeys.forEach(key => {
                if (key === 'BUTTONS.YES') {
                    this._alerts.find(alert => alert.key === 'deactivation')
                        .buttons.find(
                            button => button.role === 'confirm'
                        ).text = translation[key];
                }
                if (key === 'BUTTONS.EXIT') {
                    this._alerts.find(alert => alert.key === 'logout')
                        .buttons.find(
                            button => button.role === 'confirm'
                        ).text = translation[key];
                }
                if (key === 'BUTTONS.NO') {
                    this._alerts.find(alert => alert.key === 'deactivation')
                        .buttons.find(
                            button => button.role === 'cancel'
                        ).text = translation[key];
                }
                if (key === 'BUTTONS.CANCEL') {
                    this._alerts.find(alert => alert.key === 'logout')
                        .buttons.find(
                            button => button.role === 'cancel'
                        ).text = translation[key];
                }
            });
        });
    }
}
