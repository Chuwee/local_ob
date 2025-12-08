import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import {
    ChannelMemberExternalApi, ChannelMemberExternalService, ChannelMemberExternalState, SurchargesForm, MemberPeriods, Surcharges
} from '@admin-clients/cpanel-channels-member-external-data-access';
import { MessageDialogService, EphemeralMessageService, CurrencyInputComponent } from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe, LocalCurrencyPipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable, Subject, throwError } from 'rxjs';
import { filter, finalize, first, switchMap, takeUntil, tap } from 'rxjs/operators';

@Component({
    imports: [
        LocalCurrencyPipe, MaterialModule, CommonModule, ReactiveFormsModule, FormContainerComponent,
        CurrencyInputComponent, TranslatePipe, ErrorMessage$Pipe, ErrorIconDirective
    ],
    providers: [
        ChannelMemberExternalApi,
        ChannelMemberExternalService,
        ChannelMemberExternalState
    ],
    selector: 'app-members-surcharges',
    templateUrl: './surcharges.component.html',
    styleUrls: ['./surcharges.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class MembersSurchargesComponent implements OnInit, OnDestroy, WritingComponent {
    private _channelsService = inject(ChannelsService);
    private _memberExtSrv = inject(ChannelMemberExternalService);
    private _fb = inject(FormBuilder);
    private _ephemeralSrv = inject(EphemeralMessageService);
    private _msgDialogService = inject(MessageDialogService);
    private _onDestroy = new Subject<void>();
    private _channelId: number;
    form: FormGroup<SurchargesForm>;
    inProgress$ = booleanOrMerge([
        this._channelsService.isChannelLoading$(),
        this._memberExtSrv.channelOptions.loading$()
    ]);

    periods = [MemberPeriods.buy, MemberPeriods.change, MemberPeriods.renewal, MemberPeriods.buyNew];

    ngOnInit(): void {

        this.form = this._fb.group(
            this.periods.reduce<SurchargesForm>((acc, curr) =>
                (acc[curr] = this._fb.control<number>(null, { validators: [Validators.min(0)] }), acc), {}
            )
        );

        this._channelsService.getChannel$()
            .pipe(
                first(Boolean),
                tap(channel => this._channelId = channel.id),
                tap(() => this.loadMembersConfig()),
                switchMap(() => this._memberExtSrv.channelOptions.get$()),
                filter(Boolean),
                takeUntil(this._onDestroy)
            )
            .subscribe(membersConfig =>
                this.form.reset(
                    this.periods.reduce<Surcharges>((acc, curr) =>
                        (acc[curr] = membersConfig?.member_operation_periods[curr].charge, acc), {}
                    )
                )
            );
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._memberExtSrv.channelOptions.clear();
    }

    save$(): Observable<void> {
        if (!this.form.valid || !this.form.dirty) {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg();
            return throwError(() => 'invalid form');
        }

        return this._memberExtSrv.channelOptions.saveCharges(this._channelId, this.form.value)
            .pipe(finalize(() => {
                this.loadMembersConfig();
                this._ephemeralSrv.showSaveSuccess();
            }));

    }

    save(): void {
        this.save$().subscribe();
    }

    cancel(): void {
        this.loadMembersConfig();
    }

    private loadMembersConfig(): void {
        this._memberExtSrv.channelOptions.load(this._channelId);
        this.form.markAsPristine();
    }
}
