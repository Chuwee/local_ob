import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { AsyncPipe, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, ElementRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import {
    VoucherGroupType, VouchersService, PostVoucherGroup,
    VoucherGroupValidationMethod as ValidationMethod
} from '@admin-clients/cpanel-vouchers-data-access';
import {
    EntitiesFilterFields,
    EntitiesBaseService,
    Entity
} from '@admin-clients/shared/common/data-access';
import { DialogSize, SelectSearchComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { LocalCurrenciesFullTranslation$Pipe } from '@admin-clients/shared/utility/pipes';
import { Currency } from '@admin-clients/shared-utility-models';

@Component({
    selector: 'app-new-voucher-groups-dialog',
    templateUrl: './new-voucher-groups-dialog.component.html',
    styleUrls: ['./new-voucher-groups-dialog.component.scss'],
    imports: [
        NgIf, AsyncPipe,
        MaterialModule,
        FlexLayoutModule,
        TranslatePipe,
        ReactiveFormsModule,
        SelectSearchComponent,
        FormControlErrorsComponent,
        LocalCurrenciesFullTranslation$Pipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class NewVoucherGroupDialogComponent implements OnInit {
    readonly #authSrv = inject(AuthenticationService);
    readonly #dialogRef = inject(MatDialogRef<NewVoucherGroupDialogComponent>);
    readonly #fb = inject(FormBuilder);
    readonly #elemRef = inject(ElementRef);
    readonly #voucherSrv = inject(VouchersService);
    readonly #auth = inject(AuthenticationService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #destroyRef = inject(DestroyRef);
    readonly form = this.#fb.group({
        entity_id: [null as number, Validators.required],
        currency: [{ value: null as Currency, disabled: true }, Validators.required],
        name: [null as string,
        //TODO: max and min length Voucher Group Field Restrictions
        [
            Validators.required
        ]
        ],
        type: [null as VoucherGroupType, Validators.required],
        validation_method: [null as ValidationMethod, Validators.required],
        //TODO: length Voucher Group Field Restrictions
        description: [null as string]
    });

    readonly reqInProgress$ = this.#voucherSrv.isVoucherGroupSaving$();
    readonly entities$ = combineLatest([
        this.#auth.getLoggedUser$().pipe(first(Boolean)),
        this.#auth.canReadMultipleEntities$()
    ]).pipe(
        switchMap(([user, canReadMultipleEntities]) => {
            if (canReadMultipleEntities) {
                this.#entitiesService.entityList.load({
                    limit: 999,
                    sort: 'name:asc',
                    fields: [EntitiesFilterFields.name],
                    type: 'CHANNEL_ENTITY'
                });
                return this.#entitiesService.entityList.getData$().pipe(
                    filter(value => value !== null)
                );
            } else {
                this.#entitiesService.loadEntity(user.entity.id);
                return this.#entitiesService.getEntity$().pipe(
                    first(Boolean),
                    tap(entity => {
                        this.form.patchValue({ entity_id: entity.id }, { emitEvent: false });
                        this.entityHandler(entity);
                    }),
                    map(entity => [entity])
                );
            }
        }),
        takeUntilDestroyed(this.#destroyRef),
        shareReplay(1)
    );

    readonly currencies$ = this.#authSrv.getLoggedUser$()
        .pipe(first(), map(AuthenticationService.operatorCurrencies));

    types = [VoucherGroupType.manual, VoucherGroupType.giftCard, VoucherGroupType.external];
    validationMethods = Object.values(ValidationMethod);

    ngOnInit(): void {
        this.#dialogRef.addPanelClass(DialogSize.MEDIUM);
        this.#dialogRef.disableClose = false;

        this.form.get('entity_id').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(entityId => {
                this.#entitiesService.clearEntity();
                this.#entitiesService.loadEntity(entityId);
                this.#entitiesService.getEntity$()
                    .pipe(first(Boolean))
                    .subscribe(entity => {
                        this.entityHandler(entity);
                    });
            });

        this.form.get('type').valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(type => {
                const validationMethodControl = this.form.get('validation_method');
                if (type === VoucherGroupType.external) {
                    this.validationMethods = [ValidationMethod.avetMemberId];
                    validationMethodControl.patchValue(ValidationMethod.avetMemberId);
                } else if (type === VoucherGroupType.manual) {
                    this.validationMethods = [ValidationMethod.code, ValidationMethod.codeAndPin];
                    validationMethodControl.patchValue(null);
                } else {
                    this.validationMethods = [ValidationMethod.code];
                    validationMethodControl.patchValue(ValidationMethod.code);
                }
            });

        this.entities$.pipe(takeUntilDestroyed(this.#destroyRef)).subscribe();

        this.currencies$
            .pipe(first())
            .subscribe(currencies => {
                if (currencies?.length > 1) {
                    this.form.get('currency').enable({ emitEvent: false });
                } else {
                    this.form.get('currency').disable({ emitEvent: false });
                }
                this.form.get('currency').updateValueAndValidity();
            });
    }

    createVoucherGroup(): void {
        if (this.form.valid) {
            this.#authSrv.getLoggedUser$()
                .pipe(first())
                .subscribe(user => {
                    const postVoucherGroup: PostVoucherGroup = {
                        name: this.form.value.name,
                        type: this.form.value.type,
                        entity_id: this.form.value.entity_id,
                        validation_method: this.form.value.validation_method,
                        description: this.form.value.description
                    };
                    if (postVoucherGroup.type === VoucherGroupType.giftCard) {
                        postVoucherGroup.validation_method = ValidationMethod.code;
                    }
                    const currencies = AuthenticationService.operatorCurrencyCodes(user);
                    //TODO(MULTICURRENCY): delete when the multicurrency functionality is finished
                    if (currencies?.length > 1) {
                        postVoucherGroup.currency_code = this.form.value.currency.code;
                    } else {
                        postVoucherGroup.currency_code = currencies?.length === 1 ? currencies[0] : user.currency;
                    }
                    this.#voucherSrv.createVoucherGroup(postVoucherGroup)
                        .subscribe(id => this.close(id));
                });
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this.#elemRef.nativeElement);
        }
    }

    close(id: number = null): void {
        this.#dialogRef.close(id);
    }

    private entityHandler(entity: Entity): void {
        const isAvetEntity = entity.settings?.allow_avet_integration;
        if (isAvetEntity) {
            this.types = [VoucherGroupType.manual, VoucherGroupType.giftCard, VoucherGroupType.external];
        } else {
            const typeField = this.form.get('type');
            const validationField = this.form.get('validation_method');
            this.types = [VoucherGroupType.manual, VoucherGroupType.giftCard];
            if (typeField.value === VoucherGroupType.external) {
                typeField.patchValue(null);
            }
            if (validationField.value === ValidationMethod.avetMemberId) {
                validationField.patchValue(null);
            }
        }
    }

}
