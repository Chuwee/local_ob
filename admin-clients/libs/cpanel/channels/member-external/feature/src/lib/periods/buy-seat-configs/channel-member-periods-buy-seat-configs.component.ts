import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    ChannelMemberExternalService, MemberDatesFilter, MemberPeriods, MembershipPaymentInfo
} from '@admin-clients/cpanel-channels-member-external-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { IdName } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatAccordion, MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle } from '@angular/material/expansion';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatOption, MatSelect } from '@angular/material/select';
import { RouterLink } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { filter, first, switchMap, tap } from 'rxjs/operators';
import { ChannelMemberPeriodsAvatarConfigComponent } from '../avatar-config/channel-member-periods-avatar-config.component';
import { ChannelMemberPeriodsDatesFilterComponent } from '../dates-filter/channel-member-periods-dates-filter.component';

@Component({
    selector: 'app-channel-member-periods-buy-seat-configs',
    templateUrl: './channel-member-periods-buy-seat-configs.component.html',
    styleUrls: ['./channel-member-periods-buy-seat-configs.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, ReactiveFormsModule, MatAccordion, MatExpansionPanel, MatExpansionPanelHeader,
        MatExpansionPanelTitle, AsyncPipe, TranslatePipe, MatFormField, MatSelect, MatOption, MatLabel,
        FormControlErrorsComponent, MatIcon, MatCheckbox, ChannelMemberPeriodsDatesFilterComponent,
        ChannelMemberPeriodsAvatarConfigComponent, RouterLink
    ]
})
export class ChannelMemberExternalPeriodsBuySeatConfigsComponent implements OnInit, WritingComponent {
    readonly #onDestroy = inject(DestroyRef);
    readonly #channelsService = inject(ChannelsService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly form = this.#fb.group({
        periodicity_id: [null as number, [Validators.required, Validators.min(1)]],
        unpaid_term_allowed: false,
        member_term_id: [{ value: null as number, disabled: true }, [Validators.required, Validators.min(1)]],
        allow_cross_purchases: false,
        dates_filter: this.#fb.group({
            enabled: false,
            access: this.#fb.group([]),
            default_access: null
        }),
        avatar: this.#fb.group({
            enabled: false,
            mandatory: [{ value: null as boolean, disabled: true }, [Validators.required]]
        })
    });

    readonly loading$ = booleanOrMerge([
        this.#memberExtSrv.channelOptions.loading$(),
        this.#memberExtSrv.terms.loading$(),
        this.#memberExtSrv.periodicities.loading$(),
        this.#memberExtSrv.datesFilter.loading$()
    ]);

    readonly terms$: Observable<IdName[]> = this.#memberExtSrv.terms.get$().pipe(filter(Boolean));
    readonly periodicities$: Observable<IdName[]> = this.#memberExtSrv.periodicities.get$().pipe(filter(Boolean));
    readonly memberPeriods = MemberPeriods;

    #channelId: number;

    ngOnInit(): void {
        this.#channelsService.getChannel$()
            .pipe(
                first(Boolean),
                switchMap(channel => {
                    this.#channelId = channel.id;
                    this.load();
                    return this.#memberExtSrv.channelOptions.get$();
                }),
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(membersConfig => {
                this.form.controls.member_term_id.reset();
                this.form.controls.periodicity_id.reset();
                this.form.controls.unpaid_term_allowed.reset();
                if (membersConfig.membership_periodicity_id) {
                    this.form.controls.periodicity_id.setValue(membersConfig.membership_periodicity_id, { emitEvent: false });
                }
                if (membersConfig.membership_term_id) {
                    this.form.controls.unpaid_term_allowed.setValue(true, { emitEvent: false });
                    this.form.controls.member_term_id.setValue(membersConfig.membership_term_id, { emitEvent: false });
                    this.form.controls.member_term_id.enable();
                }
                this.form.controls.allow_cross_purchases.setValue(membersConfig.allow_cross_purchases, { emitEvent: false });
                this.form.controls.avatar.controls.enabled.setValue(
                    membersConfig.member_operation_periods?.BUY_SEAT?.avatar?.enabled, { emitEvent: false });
                this.form.controls.avatar.controls.mandatory.setValue(
                    membersConfig.member_operation_periods?.BUY_SEAT?.avatar?.mandatory ?? null as boolean, { emitEvent: false });
                if (this.form.controls.avatar.controls.enabled.value) {
                    this.form.controls.avatar.controls.mandatory.enable();
                }
                this.form.markAsPristine();
            });

        this.form.controls.unpaid_term_allowed.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.member_term_id.removeValidators([Validators.required, Validators.min(1)]);
                    this.form.controls.member_term_id.setValue(null, { emitEvent: false });
                } else {
                    this.form.controls.member_term_id.addValidators([Validators.required, Validators.min(1)]);
                    this.form.controls.member_term_id.enable();
                }
            });
    }

    save(): void {
        this.save$().subscribe(() => {
            this.load();
            this.#memberExtSrv.channelOptions.load(this.#channelId);
        });
    }

    save$(): Observable<void[]> {
        if (this.form.valid && this.form.dirty) {
            const obs$: Observable<void>[] = [of(null)];
            const paymentRequest: Partial<MembershipPaymentInfo> = {};
            if (this.form.controls.periodicity_id.dirty) {
                paymentRequest.periodicity_id = this.form.value.periodicity_id;
            }
            if (this.form.controls.member_term_id.dirty) {
                paymentRequest.term_id = this.form.value.member_term_id;
            }
            if (this.form.controls.member_term_id.dirty || this.form.controls.periodicity_id.dirty) {
                obs$.push(this.#memberExtSrv.putMembershipPaymentInfo.update(this.#channelId, paymentRequest));
            }
            if (this.form.controls.allow_cross_purchases.dirty || this.form.controls.avatar.dirty) {
                const allowCrossPurchases = this.form.controls.allow_cross_purchases.value;
                const avatarConfig = this.form.controls.avatar.getRawValue();
                const value = {
                    member_operation_periods: {
                        BUY_SEAT: { avatar: avatarConfig }
                    },
                    ...allowCrossPurchases && { allow_cross_purchases: allowCrossPurchases }
                };
                obs$.push(this.#memberExtSrv.channelOptions.save(this.#channelId, value));
            }
            if (this.form.controls.dates_filter.dirty) {
                const datesFilter = this.form.value.dates_filter;
                const datesFilterRequest = {
                    enabled: datesFilter.enabled,
                    access: Object.values(datesFilter.access),
                    default_access: datesFilter.default_access
                } as MemberDatesFilter;
                obs$.push(this.#memberExtSrv.datesFilter.update(this.#channelId, MemberPeriods.buy, datesFilterRequest));
            }

            return forkJoin(obs$).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        } else {
            this.form.markAllAsTouched();
            if (this.form.value.dates_filter.enabled) {
                this.form.controls.dates_filter.controls.default_access.markAllAsTouched();
            }
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'Invalid form');
        }
    }

    cancel(): void {
        this.form.reset();
        const form = this.form.controls.dates_filter.controls.access as UntypedFormGroup;
        Object.keys(this.form.controls.dates_filter.controls.access.controls)
            .forEach(controlKey => form.removeControl(controlKey));
        this.load();
        this.#memberExtSrv.channelOptions.load(this.#channelId);
    }

    private load(): void {
        this.#memberExtSrv.periodicities.load(this.#channelId);
        this.#memberExtSrv.terms.load(this.#channelId);
        this.#memberExtSrv.datesFilter.load(this.#channelId, MemberPeriods.buy);
    }
}
