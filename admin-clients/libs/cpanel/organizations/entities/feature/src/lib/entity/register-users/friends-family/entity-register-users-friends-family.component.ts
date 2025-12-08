import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { EntitiesService } from '@admin-clients/cpanel/organizations/entities/data-access';
import { EntityFriends, PutEntity } from '@admin-clients/shared/common/data-access';
import { DialogSize, EphemeralMessageService, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge, maxDecimalLength } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioButton, MatRadioGroup } from '@angular/material/radio';
import {
    MatCell, MatCellDef, MatColumnDef, MatHeaderCell, MatHeaderCellDef, MatHeaderRow, MatHeaderRowDef, MatRow, MatRowDef, MatTable
} from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, firstValueFrom, Observable, throwError } from 'rxjs';
import { filter, map, tap } from 'rxjs/operators';

export type LimitsType = 'ALL_SAME' | 'NOT_SAME';
export type RelationType = EntityFriends['friends_relation_mode'];
@Component({
    selector: 'app-entity-register-users-friends-family',
    templateUrl: './entity-register-users-friends-family.component.html',
    styleUrl: './entity-register-users-friends-family.component.scss',
    imports: [
        FormContainerComponent, TranslatePipe, MatAccordion, MatExpansionPanel, MatButton, ReactiveFormsModule,
        MatExpansionPanelTitle, MatExpansionPanelHeader, MatRadioGroup, MatRadioButton, MatFormField, FormControlErrorsComponent,
        MatTable, MatHeaderCell, MatCell, MatColumnDef, MatHeaderRow, MatRow, MatHeaderRowDef, MatRowDef, MatHeaderCellDef,
        MatCellDef, MatInput, MatProgressSpinner, MatIcon
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})

export class EntityFriendsFamilyComponent implements OnInit, OnDestroy {
    readonly #fb = inject(FormBuilder);
    readonly #destroyRef = inject(DestroyRef);
    readonly #entitiesSrv = inject(EntitiesService);
    readonly #ephemeralMessageService = inject(EphemeralMessageService);
    readonly #messageDialogSrv = inject(MessageDialogService);

    readonly $loading = toSignal(
        booleanOrMerge([
            this.#entitiesSrv.isEntityLoading$(),
            this.#entitiesSrv.entityCustomerTypes.inProgress$(),
            this.#entitiesSrv.entityFriends.loading$()
        ])
    );

    readonly columns = ['customer_type', 'amount'];
    readonly $entity = toSignal(this.#entitiesSrv.getEntity$().pipe(filter(Boolean)));
    readonly $customerTypes = toSignal(this.#entitiesSrv.entityCustomerTypes.get$());

    readonly $isFriendsAndFamilyButtonEnabled = toSignal(this.#entitiesSrv.getEntity$().pipe(
        filter(Boolean),
        map(entity => !entity.settings?.allow_friends)
    ));

    readonly form = this.#fb.group({
        limits: 'ALL_SAME' as LimitsType,
        default: [null as number, [Validators.required, Validators.min(1), maxDecimalLength(0)]],
        exceptions: this.#fb.group({}),
        friends_relation_mode: ['BIDIRECCIONAL' as RelationType, [Validators.required]]
    });

    ngOnInit(): void {
        this.#entitiesSrv.entityCustomerTypes.load(this.$entity().id);
        this.#entitiesSrv.entityFriends.load(this.$entity().id);

        this.form.controls.limits.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(value => {
                if (value === 'ALL_SAME') {
                    this.form.controls.default.enable();
                } else if (value === 'NOT_SAME') {
                    this.form.controls.default.disable();
                }
            });

        combineLatest([this.#entitiesSrv.entityCustomerTypes.get$(), this.#entitiesSrv.entityFriends.get$()])
            .pipe(
                filter(([_, entityFriends]) => !!entityFriends),
                takeUntilDestroyed(this.#destroyRef)
            ).subscribe(([customersTypes, entityFriends]) => {
                customersTypes?.forEach(customerType => {
                    const controlExceptionName = `${customerType.id}`;
                    const existingExceptionControl = this.form.controls.exceptions.get(controlExceptionName);
                    const friendLimit = entityFriends.limits.exceptions?.
                        find(exception => exception.id === customerType.id)?.value || entityFriends.limits.default;
                    if (!existingExceptionControl) {
                        this.form.controls.exceptions
                            .setControl(controlExceptionName, this.#fb.control(friendLimit, [Validators.min(1), maxDecimalLength(0)]));
                    } else if (!existingExceptionControl.dirty) {
                        existingExceptionControl.setValue(friendLimit);
                    }
                });
                this.form.controls.default.patchValue(entityFriends.limits.default || 20);
                this.form.controls.friends_relation_mode.patchValue(entityFriends.friends_relation_mode || 'BIDIRECTIONAL');
                entityFriends.limits.exceptions?.length > 0 ? this.form.controls.limits.patchValue('NOT_SAME') :
                    this.form.controls.limits.patchValue('ALL_SAME');
            });
    }

    ngOnDestroy(): void {
        this.#entitiesSrv.entityCustomerTypes.clear();
    }

    enableFriendsAndFamily(): void {
        this.#messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'ENTITY.FRIENDS_AND_FAMILY.ENABLE_DIALOG_TITLE',
            message: 'ENTITY.FRIENDS_AND_FAMILY.ENABLE_DIALOG_MESSAGE',
            actionLabel: 'ENTITY.FRIENDS_AND_FAMILY.ENABLE_DIALOG_BUTTON',
            showCancelButton: true
        })
            .subscribe(async success => {
                if (success) {
                    const updatedEntity: PutEntity = {
                        ...this.$entity(),
                        settings: { allow_friends: true }
                    };

                    await firstValueFrom(this.#entitiesSrv.updateEntity(this.$entity().id, updatedEntity));

                    this.#ephemeralMessageService.showSuccess({ msgKey: 'ENTITY.FRIENDS_AND_FAMILY.ENABLE_SUCCESS' });
                    this.#entitiesSrv.loadEntity(this.$entity().id);
                }
            });

    }

    save(): void {
        this.save$().subscribe(() => {
            this.#entitiesSrv.entityFriends.load(this.$entity().id);
            this.form.markAsPristine();
            this.form.markAsUntouched();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const updateConfig = {
                limits: {
                    default: this.form.controls.default.value,
                    exceptions: []
                },
                friends_relation_mode: this.form.controls.friends_relation_mode.value
            };
            if (this.form.controls.limits.value === 'NOT_SAME') {
                const exceptions = Object.keys(this.form.controls.exceptions.controls).map(id => ({
                    id,
                    value: this.form.controls.exceptions.controls[id].value
                }));
                updateConfig.limits.exceptions = exceptions;
            }
            return this.#entitiesSrv.entityFriends.save(this.$entity().id, updateConfig).pipe(tap(() => {
                this.#ephemeralMessageService.showSuccess({
                    msgKey: 'ENTITY.UPDATE_SUCCESS'
                });
            }));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.form.markAsPristine();
        this.#entitiesSrv.entityFriends.load(this.$entity().id);
    }
}
