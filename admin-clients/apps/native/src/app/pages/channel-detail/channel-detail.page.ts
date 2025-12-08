import {
    EventChannel, eventChannelsProviders, EventChannelsService, UpdateEventChannelsRequest
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AlertController, ToastController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { filter, Observable, Subject, takeUntil } from 'rxjs';

@Component({
    selector: 'channel-detail-page',
    templateUrl: './channel-detail.page.html',
    styleUrls: ['./channel-detail.page.scss'],
    providers: [eventChannelsProviders],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelDetailPage implements OnInit, OnDestroy {
    private readonly _activatedRoute = inject(ActivatedRoute);
    private readonly _channelsService = inject(EventChannelsService);
    private readonly _translate = inject(TranslateService);
    private readonly _alertController = inject(AlertController);
    private readonly _changeDetectorRef = inject(ChangeDetectorRef);
    private readonly _toastController = inject(ToastController);
    private readonly _onDestroy = new Subject<void>();
    private _eventId: number;
    private _id: number;

    readonly isError$: Observable<HttpErrorResponse> = this._channelsService.eventChannel.error$();
    readonly isLoading$: Observable<boolean> = this._channelsService.eventChannel.inProgress$();
    readonly dateTimeFormats = DateTimeFormats;

    channel: EventChannel;

    ngOnInit(): void {
        this._activatedRoute.queryParams.pipe(
            takeUntil(this._onDestroy)
        ).subscribe(params => {
            this._id = params['id'];
            this._eventId = params['eventId'];
            this._channelsService.eventChannel.load(this._eventId, this._id);
        });

        this._channelsService.eventChannel.get$().pipe(
            filter(Boolean),
            takeUntil(this._onDestroy)
        ).subscribe(channel => {
            this.channel = this.addParsedQuotas(channel);
        });
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    reTry(): void {
        this._channelsService.eventChannel.load(this._eventId, this._id);
    }

    toggleActivatedClass(ev: Event): void {
        const element = ev.currentTarget as HTMLElement;
        const allAccordionTitles = document.querySelectorAll('.channel-detail-page__accordion-title');

        allAccordionTitles.forEach(accordion => {
            accordion.id === element.id ? element.classList.toggle('activated') : accordion.classList.remove('activated');
        });
    }

    async changeStatusSession(key: 'sale' | 'release' | 'use_event_dates'): Promise<void> {
        if (key === 'use_event_dates') {
            this.channel.settings.use_event_dates = !this.channel.settings.use_event_dates;
        } else {
            this.channel.settings[key].enabled = !this.channel.settings[key].enabled;
        }

        const title = this._translate.instant(`SESSION_DETAIL.DATES.ALERT.TITLE`);
        const text = this._translate.instant(`SESSION_DETAIL.DATES.ALERT.TEXT`);
        const cancelText = this._translate.instant(`SESSION_DETAIL.DATES.ALERT.CANCEL`);
        const saveText = this._translate.instant(`SESSION_DETAIL.DATES.ALERT.SAVE`);
        const alert = await this._alertController.create({
            header: title,
            message: text,
            cssClass: 'ob-alert alert-warning',
            htmlAttributes: { ['data-override-styles']: '' },
            buttons: [
                {
                    text: cancelText,
                    role: 'cancel',
                    cssClass: 'ob-btn ghost size--small',
                    htmlAttributes: { ['data-override-styles']: '' },
                    handler: () => {
                        if (key === 'use_event_dates') {
                            this.channel.settings.use_event_dates = !this.channel.settings.use_event_dates;
                        } else {
                            this.channel.settings[key].enabled = !this.channel.settings[key].enabled;
                        }

                        this._changeDetectorRef.detectChanges();
                    }
                },
                {
                    text: saveText,
                    role: 'confirm',
                    cssClass: 'ob-btn primary size--small',
                    htmlAttributes: { ['data-override-styles']: '' },
                    handler: () => {
                        this.updateChannelStatus({
                            settings: this.channel.settings,
                            quotas: this.channel.quotas.map(quota => quota.id),
                            use_all_quotas: this.channel.use_all_quotas
                        });
                    }
                }
            ]
        });

        await alert.present();
    }

    updateChannelStatus(update: UpdateEventChannelsRequest): void {
        this._channelsService.updateEventChannel(this._eventId, this._id, update).subscribe(() => {
            this.showToast('success').then();
        });
    }

    private async showToast(type: 'error' | 'success'): Promise<void> {
        const toast = await this._toastController.create({
            message: this._translate.instant(`SESSION_DETAIL.TOAST.${type.toUpperCase()}`),
            duration: 2500,
            position: 'top',
            icon: `./assets/media/icons/${type}_circle.svg`,
            cssClass: `ob-toast ob-toast--${type}`
        });

        await toast.present();
    }

    private addParsedQuotas(channel: EventChannel): EventChannel {
        const quot = {};
        channel.quotas?.forEach(quota => {
            if (!quot[quota.template_id]) {
                quot[quota.template_id] = {
                    template_id: quota.template_id,
                    quotas: [],
                    template_name: quota.template_name
                };
            }
            quot[quota.template_id].quotas.push({ description: quota.description, id: quota.id });
        });
        channel.parsedQuotas = [];
        Object.keys(quot).forEach(templateId => {
            channel.parsedQuotas.push(quot[templateId]);
        });
        return channel;
    }
}
