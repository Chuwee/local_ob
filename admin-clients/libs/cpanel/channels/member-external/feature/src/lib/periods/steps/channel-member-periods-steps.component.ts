import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { MembersOptions, ChannelMemberExternalService, BuySeatFlow, MemberStep, MemberPeriods, NewMemberFlow, MemberOperationPeriods } from '@admin-clients/cpanel-channels-member-external-data-access';
import { EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ChangeDetectionStrategy, Component, inject, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatExpansionPanel } from '@angular/material/expansion';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subject, throwError } from 'rxjs';
import { filter, finalize, first, map, switchMap, takeUntil, tap } from 'rxjs/operators';
import { steps } from '../steps';

type PeriodOptions = MembersOptions['member_operation_periods'];

@Component({
    selector: 'app-channel-member-external-periods-steps',
    templateUrl: './channel-member-periods-steps.component.html',
    styleUrls: ['./channel-member-periods-steps.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelMemberExternalPeriodsStepsComponent implements OnInit, OnDestroy, WritingComponent {
    private _channelsService = inject(ChannelsService);
    private _memberExtSrv = inject(ChannelMemberExternalService);
    private _fb = inject(FormBuilder);
    private _ephemeralSrv = inject(EphemeralMessageService);
    private _onDestroy = new Subject<void>();
    private _channelId: number;
    private _route = inject(ActivatedRoute);
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    selectedBuySeatFlowType = this._fb.control<BuySeatFlow>(BuySeatFlow.internal, Validators.required);
    defaultValues = Object.values(MemberStep).map(value => value.toString());
    form = this._fb.group({
        [MemberPeriods.renewal]: this._fb.group({
            active: this._fb.control<boolean>(false),
            show_update_partner_user: this._fb.control<boolean>(false),
            ignored_steps: this._fb.control<string[]>(this.defaultValues)
        }),
        [MemberPeriods.change]: this._fb.group({
            active: this._fb.control<boolean>(false),
            show_update_partner_user: this._fb.control<boolean>(false),
            skip_periodicity_module: this._fb.control<boolean>(true),
            ignored_steps: this._fb.control<string[]>(this.defaultValues)
        }),
        [MemberPeriods.buy]: this._fb.group({
            active: this._fb.control<boolean>(false),
            buy_seat_flow: this._fb.control<BuySeatFlow>(BuySeatFlow.internal, Validators.required),
            show_conditions: this._fb.control<boolean>(false),
            show_update_partner_user: this._fb.control<boolean>(false),
            skip_periodicity_module: this._fb.control<boolean>(true),
            ignored_steps: this._fb.control<string[]>(this.defaultValues)
        }),
        [MemberPeriods.buyNew]: this._fb.group({
            active: this._fb.control<boolean>(false),
            show_conditions: this._fb.control<boolean>(false),
            new_member_flow: this._fb.control<string>('', Validators.required)
        })
    });

    inProgress$ = this._memberExtSrv.channelOptions.loading$();

    readonly steps = steps;
    readonly newMemberFlowOptions = Object.values(NewMemberFlow);
    readonly buySeatFlow = Object.values(BuySeatFlow);
    readonly configBuySeatFlowOptions = [BuySeatFlow.internal, BuySeatFlow.external];
    period$ = this._route.data.pipe(map(data => {
        this.form.disable();
        this.form.controls[data['period']].enable();
        return data['period'];
    }));

    ngOnInit(): void {
        this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                switchMap(channel => {
                    this._channelId = channel.id;
                    return this._memberExtSrv.channelOptions.get$();
                }),
                filter(channelOptions => !!channelOptions?.member_operation_periods),
                takeUntil(this._onDestroy)
            ).subscribe(channelOptions => this.form.reset(channelOptions.member_operation_periods));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    toggleStep(period: MemberPeriods, step: MemberStep, change: MatCheckboxChange): void {
        const ignoredStepsCtrl = this.form.get([period, 'ignored_steps']);
        const ignoredSteps: MemberStep[] = ignoredStepsCtrl.value || [];
        if (!change.checked) {
            ignoredStepsCtrl.setValue(ignoredSteps.concat(step));
        } else {
            ignoredStepsCtrl.setValue(ignoredSteps.filter(elem => elem !== step));
        }
        ignoredStepsCtrl.markAsDirty();
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<unknown> {
        if (this.form.valid && this.form.dirty) {
            return this._memberExtSrv.channelOptions.save(this._channelId,
                { member_operation_periods: this.prepare(this.form.value as PeriodOptions) })
                .pipe(tap(() => this.load()), finalize(() => this._ephemeralSrv.showSaveSuccess()));
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    cancel(): void {
        this.load();
    }

    isNewMemberPeriod(period: MemberPeriods): boolean {
        return period === MemberPeriods.buyNew;
    }

    isBuySeatPeriod(period: MemberPeriods): boolean {
        return period === MemberPeriods.buy;
    }

    newMemberFlow(period: string): string {
        return this.form.get([period, 'new_member_flow'])?.value ?? '';
    }

    getSelectedBuySeatFlowType(period: MemberPeriods): string {
        return period === MemberPeriods.buy ? this.selectedBuySeatFlowType.value : '';
    }

    private prepare(periods: PeriodOptions): PeriodOptions {
        return Object.keys(periods).reduce<PeriodOptions>((acc, period) => {
            const options: MemberOperationPeriods = periods[period];
            let ignoredSteps = options.ignored_steps || [];
            acc[period] = options;

            if (period === MemberPeriods.buyNew && options.new_member_flow !== NewMemberFlow.payment) {
                ignoredSteps = ignoredSteps.concat(MemberStep.newOrder);
                ignoredSteps = ignoredSteps.concat(MemberStep.newPayment);
            }

            acc[period].ignored_steps = ignoredSteps;

            return acc;
        }, {});
    }

    private load(): void {
        this._memberExtSrv.channelOptions.load(this._channelId);
    }
}
