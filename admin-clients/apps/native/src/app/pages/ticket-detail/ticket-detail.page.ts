import { OrdersService, ordersProviders } from '@admin-clients/cpanel-sales-data-access';
import { TicketDetail, TicketsBaseService, ticketsBaseProviders } from '@admin-clients/shared/common/data-access';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Browser } from '@capacitor/browser';
import { ToastController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { filter } from 'rxjs';
import { formattedCode } from '../../helpers/string.utils';

@Component({
    selector: 'ticket-detail',
    templateUrl: './ticket-detail.page.html',
    styleUrls: ['./ticket-detail.page.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ticketsBaseProviders, ordersProviders],
    standalone: false
})
export class TicketDetailPage {
    readonly #ticketsBaseSrv = inject(TicketsBaseService);
    readonly #ordersSrv = inject(OrdersService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #toastController = inject(ToastController);
    readonly #translateService = inject(TranslateService);

    $isLoading = signal(false);
    $isError = signal(false);
    priceModalOpen = false;
    ticket: TicketDetail;
    ticketCode: string;
    ticketId: string;
    readonly getTicketDetail$ = this.#ticketsBaseSrv.ticketDetail.get$().pipe(filter(Boolean)).subscribe({
        next: ticketResponse => {
            this.ticket = ticketResponse;
            this.$isLoading.set(false);
        },
        error: () => {
            this.$isError.set(true);
        }
    });

    readonly getData$ = this.#activatedRoute.queryParams
        .pipe(filter(Boolean)).subscribe(params => {
            this.ticketCode = params['order_code'];
            this.ticketId = params['id'];
            this.loadData();
        });

    get validation(): string {
        return this.ticket.ticket.validation_last_date ? 'VALIDATED' : 'NOT_VALIDATED';
    }

    get channelCharges(): number {
        return this.ticket.price.charges.channel;
    }

    get formattedCode(): string {
        return formattedCode(this.ticket.ticket.barcode.code);
    }

    getLastTenItemsFromList<T>(list: T[]): T[] {
        return list.slice(-10).reverse();
    }

    getFormattedDate(date: string): string {
        return moment(date).format('DD/MM/yyyy - HH:mm');
    }

    seeTicket(): void {
        this.#ticketsBaseSrv.ticketDetail.load(this.ticket.order.code, this.ticket.id.toString());
        this.#ticketsBaseSrv.ticketDetail.link.get$()
            .subscribe({
                next: response => this.showOpenBrowserPdf(response)
                , error: () => {
                    this.showErrorToast();
                }
            });
    }

    showAll(): void {
        this.#ordersSrv.getTicketsLink$(this.ticket.order.code)
            .subscribe({
                next: response => {
                    this.showOpenBrowserPdf(response);
                },
                error: () => {
                    this.showErrorToast();
                }
            });
    }

    openPriceModal(): void {
        this.priceModalOpen = true;
    }

    closePriceModal(): void {
        this.priceModalOpen = false;
    }

    toggleActivatedClass(ev: Event): void {
        const element = ev.currentTarget as HTMLElement;
        const allAccordionTitles = document.querySelectorAll(
            '.ticket-detail__accordion-title'
        );

        allAccordionTitles.forEach(accordion => {
            if (accordion.id === element.id) {
                element.classList.toggle('activated');
            } else {
                accordion.classList.remove('activated');
            }
        });
    }

    reTry(): void {
        this.loadData();
    }

    get attendantData(): { [key: string]: string }[] {
        const attendantData: { [key: string]: string }[] = [];

        Object.keys(this.ticket.attendant).forEach((key: string) => {
            attendantData.push({
                key,
                value: this.ticket.attendant[key]
            });
        });

        return attendantData;
    }

    private async showOpenBrowserPdf(url: string): Promise<void> {
        await Browser.open({
            url
        });
    }

    private loadData(): void {
        this.$isError.set(false);
        this.$isLoading.set(true);
        this.#ticketsBaseSrv.ticketDetail.load(this.ticketCode, this.ticketId);
    }

    private async showErrorToast(): Promise<void> {
        const toast = await this.#toastController.create({
            message: this.#translateService.instant('TICKETS.ERROR-PRINT'),
            duration: 2500,
            position: 'top',
            icon: './assets/media/icons/error_circle.svg',
            cssClass: 'ob-toast ob-toast--error'
        });

        await toast.present();
    }
}
