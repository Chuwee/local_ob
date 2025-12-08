import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import { ResendOrderFormRestrictions, OrdersService } from '@admin-clients/cpanel-sales-data-access';
import { Prefix, PrefixesService } from '@admin-clients/shared/common/data-access';
import { DialogSize } from '@admin-clients/shared/common/ui/components';
import { ChangeDetectionStrategy, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Observable, startWith, tap } from 'rxjs';

@Component({
    selector: 'app-resend-order-dialog',
    templateUrl: './resend-order-dialog.component.html',
    styleUrls: ['./resend-order-dialog.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ResendOrderDialogComponent implements OnInit {
    readonly #fb = inject(FormBuilder);
    readonly #ordersService = inject(OrdersService);
    readonly #prefixesService = inject(PrefixesService);
    readonly #dialogRef = inject(MatDialogRef<ResendOrderDialogComponent>);
    readonly #authSrv = inject(AuthenticationService);
    readonly #destroyRef: DestroyRef = inject(DestroyRef);

    readonly #data: {
        code: string;
        buyerEmail: string;
        canResendWhatsapp?: boolean;
        internationalPhone?: { prefix: string; number: string };
    } = inject(MAT_DIALOG_DATA);

    readonly #orderCode = this.#data.code;
    readonly #buyerEmail = this.#data.buyerEmail;
    readonly #internationalPhone: { prefix: string; number: string } = this.#data.internationalPhone;
    readonly canResendWhatsapp = this.#data.canResendWhatsapp;

    readonly phoneForm = this.#fb.group({
        prefix: [{ value: this.#internationalPhone?.prefix, disabled: !this.canResendWhatsapp }],
        number: [{ value: this.#internationalPhone?.number, disabled: !this.canResendWhatsapp },
        [Validators.required, Validators.pattern('[0-9]{1,}[0-9 ]*')]]
    });

    readonly resendOrderForm = this.#fb.group({
        email_address: [this.#buyerEmail, [Validators.email, Validators.required]],
        subject: [null as string, [Validators.maxLength(ResendOrderFormRestrictions.subjectLength)]],
        body: [null as string, [Validators.maxLength(ResendOrderFormRestrictions.bodyLength)]],
        resend_whatsapp: [this.canResendWhatsapp],
        full_regeneration: [false],
        phone: this.phoneForm
    });

    readonly maxSubjectLength: number = ResendOrderFormRestrictions.subjectLength;
    readonly maxBodyLength: number = ResendOrderFormRestrictions.bodyLength;
    readonly showPhone$ = this.resendOrderForm.controls.resend_whatsapp.valueChanges
        .pipe(
            startWith(this.canResendWhatsapp),
            tap(value => {
                if (value) {
                    this.phoneForm.enable();
                } else {
                    this.phoneForm.disable();
                }
            })
        );

    readonly prefixes$: Observable<Prefix[]> = this.#prefixesService.prefixes.get$();
    readonly selectedPrefix$ = this.phoneForm.controls.prefix.valueChanges.pipe(startWith(this.#internationalPhone?.prefix));
    readonly isOperatorUser$ = this.#authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;
        if (this.canResendWhatsapp) {
            this.#prefixesService.prefixes.load();
        }
    }

    resendOrder(): void {
        this.#ordersService.resendOrder(
            this.#orderCode,
            {
                ...this.resendOrderForm.value,
                phone: this.phoneForm.value
            }
        )
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.close(true));
    }

    close(isDone = false): void {
        this.#dialogRef.close(isDone);
    }
}
