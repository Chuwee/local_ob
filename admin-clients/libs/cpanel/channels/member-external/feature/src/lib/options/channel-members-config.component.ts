import { FormControlErrorsComponent, scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    ChannelMemberExternalService, MemberPeriods, MembersPermissions, MembersOptions, MembersImageCardType
} from '@admin-clients/cpanel-channels-member-external-data-access';
import {
    DateTimeModule, EphemeralMessageService, HelpButtonComponent, ImageUploaderComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ImageRestrictions, ObFile } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, throwError } from 'rxjs';
import { filter, first, map, pairwise, switchMap, tap } from 'rxjs/operators';

@Component({
    selector: 'app-channel-members-config',
    templateUrl: './channel-members-config.component.html',
    styleUrls: ['./channel-members-config.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule, ReactiveFormsModule, TranslatePipe, FormContainerComponent, FormControlErrorsComponent,
        DateTimeModule, AsyncPipe, HelpButtonComponent, ImageUploaderComponent
    ]
})
export class ChannelMembersConfigComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #channelsService = inject(ChannelsService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #fb = inject(FormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #onDestroy = inject(DestroyRef);

    #channelId: number;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    readonly disableUpdateBatchPricesButton$ = this.#memberExtSrv.membersBatchPrices.loading$()
        .pipe(
            pairwise(),
            filter(([prev, _]) => !prev),
            map(([prev, current]) => prev || current)
        );

    readonly inProgress$ = booleanOrMerge([
        this.#memberExtSrv.channelOptions.loading$(),
        this.#memberExtSrv.membersBatchPrices.loading$(),
        this.#memberExtSrv.membersPermissions.loading$()
    ]);

    readonly orphanSeatsPeriods = [MemberPeriods.buy, MemberPeriods.change];
    readonly periods = [MemberPeriods.buyNew, MemberPeriods.buy, MemberPeriods.renewal, MemberPeriods.change];
    readonly membersPermisions$ = this.#memberExtSrv.membersPermissions.get$().pipe(
        first(Boolean),
        map(permissions => permissions.map(permission =>
            ({ id: permission, name: 'CHANNELS.MEMBERS_PERMISSION.' + permission })))
    );

    readonly horizontalRestrictions: ImageRestrictions = {
        width: 650,
        height: 390,
        size: 253500
    };

    readonly verticalRestrictions: ImageRestrictions = {
        width: 390,
        height: 650,
        size: 253500
    };

    readonly form = this.#fb.group({
        change_pin: false,
        remember_pin: false,
        user_area: false,
        member_enabled: false,
        max_additional_members: [0, Validators.min(0)],
        open_additional_members: null,
        buy_url: null,
        force_regenerate_passbook: null,
        expiration_date_passbook: null,
        show_role: null,
        show_subscription_mode: null,
        show_previous_seat: null,
        signup_email: true,
        member_operation_periods: this.#fb.group(
            this.periods.reduce((acc, curr) =>
            (acc[curr] = this.#fb.group({
                orphan_seats_enabled: null,
                payment_mode: [0, Validators.min(0)],
                emission_reason: [0, Validators.min(0)]
            }), acc), {}
            )
        ),
        download_passbook_permissions: null,
        buy_seat_permission: MembersPermissions.pendingIssue,
        new_member_permission: MembersPermissions.pendingIssue,
        allow_tutor_form: false,
        tutee_max_age: [{ value: null as number, disabled: true }, [Validators.required, Validators.min(0)]],
        members_card_image: this.#fb.group({
            type: 'HORIZONTAL' as MembersImageCardType,
            image: null as string | ObFile
        })
    });

    ngOnInit(): void {
        this.#channelsService.getChannel$()
            .pipe(
                first(Boolean),
                switchMap(channel => {
                    this.#channelId = channel.id;
                    this.load();
                    this.#memberExtSrv.membersPermissions.load(this.#channelId);
                    return this.#memberExtSrv.channelOptions.get$();
                }),
                filter(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(settings => {
                this.form.reset({
                    ...settings,
                    members_card_image: {
                        image: settings.members_card_image?.image_url
                    },
                    // eslint-disable-next-line @typescript-eslint/no-unnecessary-boolean-literal-compare
                    signup_email: settings.signup_email === false ? false : true // only false is false, null or undefined are true
                });
                this.getImageType(settings.members_card_image?.image_url || '');
                this.form.markAsPristine();
            });
        this.initFormHandlers();
    }

    ngOnDestroy(): void {
        this.#memberExtSrv.channelOptions.clear();
    }

    save(): void {
        this.save$().subscribe(() => this.load());
    }

    save$(): Observable<void> {
        if (this.form.valid && this.form.dirty) {
            const formValue = { ...this.form.value };
            if (formValue.members_card_image) {
                formValue.members_card_image = {
                    image: typeof formValue.members_card_image.image !== 'string' ?
                        formValue.members_card_image.image?.data : formValue.members_card_image.image
                };
            }
            return this.#memberExtSrv.channelOptions.save(this.#channelId, formValue as MembersOptions)
                .pipe(tap(() => this.#ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.load();
    }

    updateMembersBatchPrices(): void {
        this.#memberExtSrv.membersBatchPrices.update(this.#channelId).subscribe();
    }

    private load(): void {
        this.#memberExtSrv.channelOptions.load(this.#channelId);
    }

    private initFormHandlers(): void {
        this.form.controls.allow_tutor_form.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(allowTutorForm => {
                if (allowTutorForm) {
                    this.form.controls.tutee_max_age.enable();
                } else {
                    this.form.controls.tutee_max_age.disable();
                }
            });
    }

    private getImageType(imgUrl: string): void {
        const img = new Image();
        img.src = imgUrl;

        img.onload = () => {
            if (img.width > img.height) {
                this.form.controls.members_card_image.controls.type.reset('HORIZONTAL');
            } else {
                this.form.controls.members_card_image.controls.type.reset('VERTICAL');
            }
        };

        img.onerror = () => {
            this.form.controls.members_card_image.controls.type.reset('HORIZONTAL');
        };
    }

}
