import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChangeDetectionStrategy,
    Component, DestroyRef,
    inject,
    OnDestroy,
    OnInit,
    QueryList,
    ViewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Validators, FormControl, FormBuilder } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { PageEvent } from '@angular/material/paginator';
import moment from 'moment';
import { BehaviorSubject, combineLatest, Observable, of, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    VoucherGroupType, VoucherGroupFieldRestrictions,
    VoucherGroupValidationMethod, Voucher, Transaction, VoucherTransactionType,
    VouchersService, PutVoucher, VoucherLimitlessValue, VoucherStatus
} from '@admin-clients/cpanel-vouchers-data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import {
    MessageDialogService, MessageDialogConfig,
    EphemeralMessageService, DialogSize, ObMatDialogConfig
} from '@admin-clients/shared/common/ui/components';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { greaterThanValidator, isHandsetOrTablet$ } from '@admin-clients/shared/utility/utils';
import { UpdateBalanceDialogComponent } from '../update-balance/update-balance-dialog.component';
import { VoucherEmailDialogComponent } from './email-dialog/voucher-email-dialog.component';

@Component({
    selector: 'app-voucher',
    templateUrl: './voucher.component.html',
    styleUrls: ['./voucher.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class VoucherComponent implements OnInit, OnDestroy {
    @ViewChildren(MatExpansionPanel)
    private readonly _matExpansionPanels: QueryList<MatExpansionPanel>;

    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesBaseService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #matDialog = inject(MatDialog);
    readonly #msgDialogService = inject(MessageDialogService);
    readonly #formats = inject(MAT_DATE_FORMATS);
    readonly #voucherSrv = inject(VouchersService);
    readonly #auth = inject(AuthenticationService);
    readonly #fb = inject(FormBuilder);

    #voucherGroupId: number;
    #voucherGroupType: VoucherGroupType;
    #voucherCode: string;
    #voucherEmail: string;

    readonly #transactionsPage = new BehaviorSubject<number>(0);
    readonly initialColumns = ['datetime', 'amount', 'balance', 'type', 'code'];
    readonly externalColumns = ['datetime', 'amount', 'balance', 'type', 'code', 'externalId'];
    displayedColumns = this.initialColumns;
    readonly fieldRestrictions = VoucherGroupFieldRestrictions;
    readonly dateFormat = moment.localeData().longDateFormat(this.#formats.display.dateInput).toLowerCase();
    readonly dateTimeFormats = DateTimeFormats;
    readonly statusCtrl = new FormControl<boolean>({ value: null, disabled: true });
    readonly form = this.#fb.group({
        balance: [{ value: null as number, disabled: true }, Validators.required],
        expiration: this.#fb.group({
            expirationEnabled: null as boolean,
            expirationDate: null as string
        }),
        email: [null as string, Validators.email],
        pin: null as string,
        usage: null as boolean,
        limit: [null as number, greaterThanValidator(0)]
    });

    readonly voucherGroup$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            filter(Boolean),
            tap(voucherGroup => {
                this.#entitiesSrv.loadEntity(voucherGroup.entity.id);
                this.#voucherGroupId = voucherGroup.id;
                this.#voucherGroupType = voucherGroup.type;
                if (voucherGroup.type === this.groupType.external) {
                    this.displayedColumns = this.externalColumns;
                } else {
                    this.displayedColumns = this.initialColumns;
                }
            }),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

    readonly groupType = VoucherGroupType;
    readonly voucherValidationMethod = VoucherGroupValidationMethod;
    readonly voucher$ = this.#voucherSrv.getVoucher$()
        .pipe(
            filter(Boolean),
            tap(voucher => {
                this.#voucherCode = voucher.code;
                const isActiveGroup = voucher?.status === VoucherStatus.active;
                this.statusCtrl.patchValue(isActiveGroup);
                this.statusCtrl.enable();
                this.updateForm(voucher);
            }),
            takeUntilDestroyed(this.#destroyRef),
            shareReplay(1)
        );

    readonly transactions$ = this.#voucherSrv.getVoucher$()
        .pipe(
            filter(Boolean),
            map(voucher => voucher?.transactions?.sort((a, b) => a.date > b.date ? -1 : 1))
        );

    readonly transactionsPage$ = this.#transactionsPage.asObservable();
    readonly transactionsPageSize = 10;
    readonly pagedTransactions$ = combineLatest([
        this.transactions$,
        this.#transactionsPage
    ]).pipe(
        map(([transactions, page]) => transactions.slice(page * this.transactionsPageSize, (page + 1) * this.transactionsPageSize)),
        shareReplay(1)
    );

    readonly transactionType = VoucherTransactionType;
    readonly reqInProgress$ = this.#voucherSrv.isVoucherLoading$();
    readonly isHandsetOrTablet$ = isHandsetOrTablet$();
    readonly currency$ = this.#voucherSrv.getVoucherGroup$()
        .pipe(
            first(),
            switchMap(voucherGroup => {
                if (voucherGroup.currency_code) {
                    return of(voucherGroup.currency_code);
                } else {
                    return this.#auth.getLoggedUser$()
                        .pipe(first(), map(user => user.currency));
                }
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );

    readonly #languages$ = this.#entitiesSrv.getEntity$()
        .pipe(
            filter(Boolean),
            map(entity => entity.settings?.languages.available)
        );

    ngOnInit(): void {
        this.#voucherSrv.getVoucherGroup$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(voucherGroup => {
                if (voucherGroup?.validation_method === VoucherGroupValidationMethod.codeAndPin) {
                    this.form.get('pin').setValidators([Validators.required]);
                }
            });
    }

    ngOnDestroy(): void {
        this.#voucherSrv.clearVoucher();
    }

    pageFilter(pageOptions: PageEvent): void {
        this.#transactionsPage.next(pageOptions.pageIndex);
    }

    getPaginatorStartItem(page: number): number {
        return (page * this.transactionsPageSize) + 1;
    }

    getPaginatorEndItem(page: number, transactionsLength: number): number {
        return Math.min((page + 1) * this.transactionsPageSize, transactionsLength);
    }

    cancel(): void {
        this.#voucherSrv.loadVoucher(this.#voucherGroupId, this.#voucherCode);
    }

    save(): void {
        this.save$().subscribe(() => this.#voucherSrv.loadVoucher(this.#voucherGroupId, this.#voucherCode));
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const data = this.form.value;
            const voucher: PutVoucher = {
                pin: data.pin,
                email: data.email,
                expiration: {
                    enable: data.expiration?.expirationEnabled,
                    date: data.expiration?.expirationEnabled ? data.expiration?.expirationDate : null
                },
                usage: {
                    limit: {
                        type: data.usage ? VoucherLimitlessValue.fixed : VoucherLimitlessValue.unlimited,
                        value: data.usage ? data.limit : null
                    }
                }
            };
            return this.#voucherSrv.saveVoucher(this.#voucherGroupId, this.#voucherCode, voucher)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.showValidationErrors();
            return throwError(() => 'invalid form');
        }
    }

    saveVoucherStatus(isActive: boolean): void {
        const voucher: PutVoucher = {
            status: isActive ? VoucherStatus.active : VoucherStatus.inactive
        };
        this.#voucherSrv.saveVoucher(this.#voucherGroupId, this.#voucherCode, voucher)
            .subscribe(() => {
                this.#ephemeralSrv.showSaveSuccess();
                this.#voucherSrv.loadVoucher(this.#voucherGroupId, this.#voucherCode);
            });
    }

    updateBalance(): void {
        this.#matDialog.open(UpdateBalanceDialogComponent, new ObMatDialogConfig())
            .beforeClosed()
            .pipe(filter(balance => (balance && balance !== null) || balance === 0))
            .subscribe(balance => {
                const updatedBalance: PutVoucher = { balance };
                const warnConfig: MessageDialogConfig = {
                    size: DialogSize.SMALL,
                    title: 'VOUCHER.UPDATE_BALANCE',
                    message: 'VOUCHER.UPDATE_BALANCE_CONFIRM',
                    messageParams: {
                        code: this.#voucherCode
                    },
                    actionLabel: 'FORMS.ACTIONS.MODIFY'
                };
                this.#msgDialogService.showWarn(warnConfig)
                    .pipe(
                        filter(Boolean),
                        switchMap(() => this.#voucherSrv.saveVoucherBalance(this.#voucherGroupId, this.#voucherCode, updatedBalance))
                    )
                    .subscribe(() => {
                        this.#ephemeralSrv.showSaveSuccess();
                        this.#voucherSrv.loadVoucher(this.#voucherGroupId, this.#voucherCode);
                    });
            });
    }

    openSendEmailDialog(): void {
        this.#languages$
            .pipe(
                take(1),
                withLatestFrom(this.voucherGroup$),
                switchMap(([languages, voucherGroup]) => this.#matDialog.open(VoucherEmailDialogComponent, new ObMatDialogConfig({
                    type: voucherGroup.type,
                    voucherEmail: this.#voucherEmail,
                    languages,
                    voucherGroupId: voucherGroup.id,
                    code: this.#voucherCode
                })).beforeClosed()),
                filter(Boolean)
            )
            .subscribe(() => this.#ephemeralSrv.showSuccess({ msgKey: 'FORMS.FEEDBACK.ACTION_SUCCESS' }));
    }

    isLinkableTransaction(transaction: Transaction): boolean {
        return transaction.code &&
            (this.#voucherGroupType !== this.groupType.external ||
                (this.#voucherGroupType === this.groupType.external && transaction.type === this.transactionType.creation));
    }

    getOrderLink({ type, code }: Transaction): string[] {
        let route: string;
        if (this.#voucherGroupType === this.groupType.giftCard && type === VoucherTransactionType.creation) {
            route = '/voucher-orders';
        } else if (this.#voucherGroupType !== this.groupType.external || type === this.transactionType.creation) {
            route = '/transactions';
        }
        return route && code ? [route, code] : null;
    }

    private showValidationErrors(): void {
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanels);
    }

    private updateForm({ expiration: { enable, date }, pin, email, usage: { limit: { type: usageLimitType, value } } }: Voucher): void {
        this.form.reset();
        this.form.patchValue({
            pin, email, usage: usageLimitType === VoucherLimitlessValue.fixed, limit: value,
            expiration: {
                expirationEnabled: enable,
                expirationDate: date
            }
        });
        this.form.markAsPristine();
    }

}
