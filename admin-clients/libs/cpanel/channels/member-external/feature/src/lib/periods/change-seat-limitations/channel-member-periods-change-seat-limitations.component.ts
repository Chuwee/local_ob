import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService } from '@admin-clients/cpanel-channels-member-external-data-access';
import { CustomersService } from '@admin-clients/cpanel-viewers-customers-data-access';
import {
    EphemeralMessageService,
    DialogSize, MessageDialogService
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import {
    FormBuilder,
    Validators
} from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Observable, Subject, throwError } from 'rxjs';
import { filter, first, switchMap, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-channel-member-periods-change-seat-limitations',
    templateUrl: './channel-member-periods-change-seat-limitations.component.html',
    styleUrls: ['./channel-member-periods-change-seat-limitations.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelMemberExternalPeriodsChangeSeatLimitationsComponent implements OnInit, OnDestroy, WritingComponent {
    private _channelId: number;
    private _entityId: number;

    private readonly _onDestroy = new Subject<void>();
    private readonly _channelsService = inject(ChannelsService);
    private readonly _memberExtSrv = inject(ChannelMemberExternalService);
    private readonly _fb = inject(FormBuilder);
    private readonly _ephemeralSrv = inject(EphemeralMessageService);
    private readonly _msgDialogSrv = inject(MessageDialogService);
    private readonly _translateSrv = inject(TranslateService);
    private readonly _customerSrv = inject(CustomersService);

    readonly form = this._fb.group({
        enable_max_change_seat: false,
        max_change_seat: [0, [Validators.min(1), Validators.required]],
        show_change_seat_counter: false
    });

    readonly loading$ = booleanOrMerge([
        this._memberExtSrv.channelOptions.loading$(),
        this._customerSrv.changeSeatCounter.loading$()
    ]);

    selectedOnly = false;

    ngOnInit(): void {
        this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                switchMap(channel => {
                    this._channelId = channel.id;
                    this._entityId = channel.entity.id;
                    this.load();
                    return this._memberExtSrv.channelOptions.get$();
                }),
                filter(membersConfig => !!membersConfig),
                takeUntil(this._onDestroy)
            )
            .subscribe(membersConfig =>
                this.form.patchValue(membersConfig?.member_operation_periods?.CHANGE_SEAT));

        // Enable max value
        this.form.controls.enable_max_change_seat.valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.max_change_seat.disable();
                    this.form.controls.show_change_seat_counter.disable();
                } else {
                    this.form.controls.max_change_seat.enable();
                    this.form.controls.show_change_seat_counter.enable();
                }
            });

    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._memberExtSrv.channelOptions.clear();
    }

    save(): void {
        this.save$().subscribe(() => this.load());
    }

    save$(): Observable<void> {
        if (this.form.valid && this.form.dirty) {
            const formValue = this.form.value;
            // eslint-disable-next-line @typescript-eslint/naming-convention
            return this._memberExtSrv.channelOptions.save(this._channelId, { member_operation_periods: { CHANGE_SEAT: formValue } })
                .pipe(tap(() => this._ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.load();
    }

    resetChangeSeatCounter(): void {
        this._msgDialogSrv.showWarn({
            message: this._translateSrv.instant('CHANNELS.MEMBERS_CONFIG.CHANGE_SEAT.RESET_WARNING.MESSAGE'),
            title: 'CHANNELS.MEMBERS_CONFIG.CHANGE_SEAT.RESET_WARNING.TITLE',
            actionLabel: 'CHANNELS.MEMBERS_CONFIG.CHANGE_SEAT.RESET_WARNING.ACTION',
            size: DialogSize.SMALL
        })
            .pipe(
                filter(Boolean),
                switchMap(() => this._customerSrv.changeSeatCounter.reset(this._entityId))
            )
            .subscribe(() =>
                this._ephemeralSrv.showSuccess({
                    msgKey: 'CHANNELS.MEMBERS_CONFIG.CHANGE_SEAT.RESET_WARNING.SUCCESS'
                }));
    }

    private load(): void {
        this._memberExtSrv.channelOptions.load(this._channelId);
    }
}
