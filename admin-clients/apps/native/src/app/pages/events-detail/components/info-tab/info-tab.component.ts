import { Event, EventPrice, EventStatus, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Input, OnInit, inject } from '@angular/core';
import { AlertController, ToastController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import { catchError, filter, of } from 'rxjs';
import { PickerDataItem } from '../../../../core/components/picker/models/pickerData';
import { PriceZoneFilterModel } from '../../models/price-zone-filter.model';

@Component({
    selector: 'events-info-tab',
    templateUrl: './info-tab.component.html',
    styleUrls: ['./info-tab.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class InfoTabComponent implements OnInit {
    readonly #translate = inject(TranslateService);
    readonly #eventsSrv = inject(EventsService);
    readonly #alertController = inject(AlertController);
    readonly #changeDetectorRef = inject(ChangeDetectorRef);
    readonly #toastController = inject(ToastController);
    #isError = false;
    #pricesFound: EventPrice[] = [];

    @Input() event: Event;

    eventPrices$ = this.#eventsSrv.eventPrices.get$().pipe(filter(Boolean))
        .subscribe((prices: EventPrice[]) => {
            this.#pricesFound = prices;

            const dataZones = prices.map(price => ({
                label: price.price_type.code,
                value: price.price_type.code,
                isSelected: false
            }));
            const uniqueValues: { [key: string]: boolean } = {};
            this.pricesOptions = dataZones.filter(item => {
                if (!uniqueValues[item.value]) {
                    uniqueValues[item.value] = true;
                    return true;
                }
                return false;
            });

            this.filterPrice = this.filterPricesByPriceType();
        });

    statusOptions: PickerDataItem[] = [];
    templateOptions: PickerDataItem[] = [];
    pricesOptions: PickerDataItem[] = [];
    filterPrice: PriceZoneFilterModel = {};
    selectTemplateId: number;
    selectPriceZone: number;

    ngOnInit(): void {
        this.statusOptions = Object.keys(EventStatus).map(key => ({
            label: this.#translate.instant(`FILTERS.STATUS.OPTIONS.${EventStatus[key].toUpperCase()}`),
            value: EventStatus[key as keyof typeof EventStatus],
            isSelected: this.event.status === EventStatus[key as keyof typeof EventStatus]
        }));

        this.loadTemplate();
    }

    loadTemplate(): void {
        this.templateOptions = this.event.venue_templates.map(template => ({
            label: template.name,
            value: template.id,
            isSelected: false
        }));
    }

    selectTemplate(templateId: number): void {
        this.selectTemplateId = templateId;
        this.selectPriceZone = null;
        this.templateOptions.find(template => template.value === templateId).isSelected = true;

        this.#eventsSrv.eventPrices.load(this.event.id.toString(), templateId.toString());
    }

    selectPrice(zoneSelect?: number): void {
        this.selectPriceZone = zoneSelect;
        this.filterPrice = this.filterPricesByPriceType(zoneSelect.toString());
    }

    async eventStatusChange(status: EventStatus): Promise<void> {
        const previousStatus = this.event.status;
        this.event.status = status;
        if (status !== previousStatus) {
            const title = this.#translate.instant(`EVENT_DETAIL.INFO.ALERT.TITLE`);
            const text = this.#translate.instant(`EVENT_DETAIL.INFO.ALERT.TEXT`);
            const cancelText = this.#translate.instant(`EVENT_DETAIL.INFO.ALERT.CANCEL`);
            const saveText = this.#translate.instant(`EVENT_DETAIL.INFO.ALERT.SAVE`);
            const alert = await this.#alertController.create({
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
                            this.event.status = previousStatus;
                            this.#changeDetectorRef.detectChanges();
                        }
                    },
                    {
                        text: saveText,
                        role: 'confirm',
                        cssClass: 'ob-btn primary size--small',
                        htmlAttributes: { ['data-override-styles']: '' },
                        handler: () => {
                            this.updateStateInEvent();
                        }
                    }
                ]
            });

            await alert.present();
        }
    }

    updateStateInEvent(): void {
        this.#eventsSrv.event.update(this.event.id, { status: this.event.status }).pipe(
            catchError(() => {
                this.#isError = true;
                return of(null);
            })
        ).subscribe(() => {
            if (!this.#isError) {
                this.showToast('success').then();
            }
        });
    }

    filterPricesByPriceType(code?: string): Record<string, EventPrice[]> {
        // GroupBy EventPrice[] by price_type.code equal to code param
        const prices = !code ? this.#pricesFound : this.#pricesFound.filter(price => price.price_type.code === code);

        return prices.reduce<Record<string, EventPrice[]>>((group, item) => {
            const key = item.price_type.code;
            group[key] = group[key] || [];
            group[key].push(item);
            return group;
        }, {});
    }

    getKeys(object: PriceZoneFilterModel): string[] {
        return Object.keys(object);
    }

    private async showToast(type: 'error' | 'success'): Promise<void> {
        const toast = await this.#toastController.create({
            message: this.#translate.instant(
                `EVENT_DETAIL.INFO.TOAST.${type.toUpperCase()}`
            ),
            duration: 2500,
            position: 'top',
            icon: `./assets/media/icons/${type}_circle.svg`,
            cssClass: `ob-toast ob-toast--${type}`
        });

        await toast.present();
    }
}
