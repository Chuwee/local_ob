import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService, MemberPeriods } from '@admin-clients/cpanel-channels-member-external-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { forkJoin, Observable, of, throwError } from 'rxjs';
import { filter, first, map, tap } from 'rxjs/operators';
import { ChannelMemberPeriodsAvatarConfigComponent } from '../avatar-config/channel-member-periods-avatar-config.component';

@Component({
    selector: 'app-channel-member-periods-new-member-configs',
    templateUrl: './channel-member-periods-new-member-configs.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        ReactiveFormsModule, TranslatePipe,
        AsyncPipe, FormContainerComponent,
        ChannelMemberPeriodsAvatarConfigComponent
    ]
})
export class ChannelMemberPeriodsNewMemberConfigsComponent implements OnInit {
    readonly #onDestroy = inject(DestroyRef);
    readonly #channelsService = inject(ChannelsService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #fb = inject(FormBuilder);

    readonly #$channelId = toSignal(this.#channelsService.getChannel$()
        .pipe(first(Boolean), map(channel => channel.id)));

    readonly form = this.#fb.group({
        avatar: this.#fb.group({
            enabled: false,
            mandatory: [{ value: null as boolean, disabled: true }, [Validators.required]]
        })
    });

    readonly loading$ = this.#memberExtSrv.channelOptions.loading$();
    readonly memberPeriods = MemberPeriods;

    ngOnInit(): void {
        this.#memberExtSrv.channelOptions.get$().pipe(
            filter(Boolean),
            takeUntilDestroyed(this.#onDestroy)
        ).subscribe(membersConfig => {
            this.form.controls.avatar.controls.enabled.setValue(
                membersConfig.member_operation_periods?.NEW_MEMBER?.avatar?.enabled, { emitEvent: false });
            this.form.controls.avatar.controls.mandatory.setValue(
                membersConfig.member_operation_periods?.NEW_MEMBER?.avatar?.mandatory ?? null as boolean, { emitEvent: false });
            if (this.form.controls.avatar.controls.enabled.value) {
                this.form.controls.avatar.controls.mandatory.enable();
            }
            this.form.markAsPristine();
        });
    }

    save(): void {
        this.save$().subscribe(() => this.#memberExtSrv.channelOptions.load(this.#$channelId()));
    }

    save$(): Observable<void[]> {
        if (this.form.valid && this.form.dirty) {
            const obs$: Observable<void>[] = [of(null)];
            if (this.form.controls.avatar.dirty) {
                const value = {
                    member_operation_periods: {
                        ['NEW_MEMBER']: this.form.getRawValue()
                    }
                };
                obs$.push(this.#memberExtSrv.channelOptions.save(this.#$channelId(), value));
            }

            return forkJoin(obs$).pipe(
                tap(() => this.#ephemeralSrv.showSaveSuccess())
            );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document);
            return throwError(() => 'Invalid form');
        }
    }

    cancel(): void {
        this.form.reset();
        this.#memberExtSrv.channelOptions.load(this.#$channelId());
    }
}
