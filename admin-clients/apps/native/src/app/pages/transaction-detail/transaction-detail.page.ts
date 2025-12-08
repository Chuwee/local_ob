import { OrderDetail, OrdersService, ordersProviders } from '@admin-clients/cpanel-sales-data-access';
import { OrderItem, OrderItemType, ticketsBaseProviders } from '@admin-clients/shared/common/data-access';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Browser } from '@capacitor/browser';
import { ToastController } from '@ionic/angular';
import { TranslateService } from '@ngx-translate/core';
import moment from 'moment';
import { filter } from 'rxjs';

@Component({
    selector: 'transaction-detail',
    templateUrl: './transaction-detail.page.html',
    styleUrls: ['./transaction-detail.page.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [ticketsBaseProviders, ordersProviders],
    standalone: false
})
export class TransactionDetailPage {
    readonly #ordersSrv = inject(OrdersService);
    readonly #activatedRoute = inject(ActivatedRoute);
    readonly #toastController = inject(ToastController);
    readonly #translateService = inject(TranslateService);
    readonly #formBuilder = inject(FormBuilder);
    $isLoading = signal(true);
    $isError = signal(false);

    readonly getOrderDetails$ = this.#ordersSrv.getOrderDetail$().pipe(filter(Boolean)).subscribe({
        next: transactionResponse => {
            this.transaction = transactionResponse;
            this.tickets = transactionResponse.items.filter(item => item.type !== OrderItemType.product);
            this.products = transactionResponse.items.filter(item => item.type === OrderItemType.product);
            this.$isLoading.set(false);
        },
        error: () => {
            this.$isLoading.set(false);
            this.$isError.set(true);
        }
    });

    readonly getData$ = this.#activatedRoute.queryParams
        .pipe(filter(Boolean)).subscribe(params => {
            this.transactionCode = params['order_code'];
            this.loadData();
        });

    priceModalOpen = false;
    resendModalOpen = false;
    //TODO: Check if this should be OrderDetail or not
    transaction: OrderDetail;
    products: OrderItem[];
    tickets: OrderItem[];
    transactionCode: string;

    readonly form: FormGroup = this.#formBuilder.group({
        email: ['', [Validators.required, Validators.email]],
        subject: [''],
        body: ['']
    });

    getLastTenItemsFromList<T>(list: T[]): T[] {
        return list.slice(-10).reverse();
    }

    getFormattedDate(date: string): string {
        return moment(date).format('DD/MM/yyyy - HH:mm');
    }

    showAll(): void {
        this.#ordersSrv.getTicketsLink$(this.transaction.code).subscribe({
            next: response => {
                Browser.open({
                    url: response
                });
            },
            error: () => {
                this.showErrorToast();
            }
        });
    }

    openResendModal(): void {
        this.resendModalOpen = true;
        this.form.get('email').setValue(this.transaction.buyer_data.email);
    }

    closeResendModal(): void {
        this.resendModalOpen = false;
        this.form.reset();
        this.emailCtrl.setErrors({ invalidEmail: false });
    }

    openPriceModal(): void {
        this.priceModalOpen = true;
    }

    closePriceModal(): void {
        this.priceModalOpen = false;
    }

    toggleActivatedClass(ev: Event): void {
        const element = ev.currentTarget as HTMLElement;
        const allAccordionTitles = document.querySelectorAll('.transaction-detail__accordion-title');

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

    validateWhitespaces(e: KeyboardEvent): void {
        if (e.key === ' ') {
            e.preventDefault();
            const input = e.target as HTMLInputElement;
            input.value = input.value.replace(/\s/g, '');
        }
    }

    get emailCtrl(): AbstractControl {
        return this.form.get('email');
    }

    resend(): void {
        const emailData = {
            email: this.form.get('email').value,
            subject: this.form.get('subject').value,
            body: this.form.get('body').value
        };

        this.#ordersSrv.resendOrder(this.transaction.code, emailData).subscribe();
        this.closeResendModal();
    }

    get channelCharges(): number {
        return this.transaction.price.charges.channel;
    }

    private loadData(): void {
        this.$isError.set(false);
        this.$isLoading.set(true);
        this.#ordersSrv.loadOrderDetail(this.transactionCode);
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
