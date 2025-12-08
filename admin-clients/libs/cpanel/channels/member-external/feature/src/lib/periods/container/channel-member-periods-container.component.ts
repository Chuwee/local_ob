import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { ChannelMemberExternalService, MemberPeriods } from '@admin-clients/cpanel-channels-member-external-data-access';
import { DialogSize, EphemeralMessageService, MessageDialogConfig, MessageDialogService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { combineLatest, Observable, of } from 'rxjs';
import { delay, filter, finalize, first, map, switchMap, tap } from 'rxjs/operators';

const unsavedChangesDialogData: MessageDialogConfig = {
    actionLabel: 'FORMS.ACTIONS.UPDATE',
    showCancelButton: true,
    message: 'CHANNELS.MEMBER_EXTERNAL.STATUS_CHANGE_WARNING.DESCRIPTION',
    title: 'CHANNELS.MEMBER_EXTERNAL.STATUS_CHANGE_WARNING.TITLE',
    size: DialogSize.MEDIUM
};

@Component({
    selector: 'app-channel-member-external-periods-container',
    templateUrl: './channel-member-periods-container.component.html',
    styleUrls: ['./channel-member-periods-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelMemberExternalPeriodsContainerComponent implements OnInit, OnDestroy, WritingComponent {
    #channelsService = inject(ChannelsService);
    #route = inject(ActivatedRoute);
    #memberExtSrv = inject(ChannelMemberExternalService);
    #fb = inject(FormBuilder);
    #ephemeralSrv = inject(EphemeralMessageService);
    #onDestroy = inject(DestroyRef);
    #channelId: number;
    #childComponent: WritingComponent;

    readonly #msgDialogSrv = inject(MessageDialogService);

    form = this.#fb.group({
        active: this.#fb.control<boolean>(false)
    });

    period$ = this.#route.data.pipe(map(data => data['period']));

    elements$ = this.period$.pipe(filter(Boolean), map(value => {
        if (value === MemberPeriods.renewal) {
            return [
                {
                    label: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.STEPS',
                    param: 'steps'
                },
                {
                    label: 'MEMBER_EXTERNAL.RENEWAL.CONFIGS.TITLE',
                    param: 'configurations'
                },
                {
                    label: 'MEMBER_EXTERNAL.ADVANCED.TITLE',
                    param: 'advanced'
                }
            ];
        }
        if (value === MemberPeriods.buy) {
            return [
                {
                    label: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.STEPS',
                    param: 'steps'
                },
                {
                    label: 'MEMBER_EXTERNAL.BUY_SEAT.CONFIGS.TITLE',
                    param: 'configurations'
                },
                {
                    label: 'MEMBER_EXTERNAL.ADVANCED.TITLE',
                    param: 'advanced'
                }
            ];
        }
        if (value === MemberPeriods.change) {
            return [
                {
                    label: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.STEPS',
                    param: 'steps'
                },
                {
                    label: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.LIMITATIONS',
                    param: 'limitations'
                },
                {
                    label: 'MEMBER_EXTERNAL.CHANGE_SEAT.CONFIGS.TITLE',
                    param: 'configurations'
                },
                {
                    label: 'MEMBER_EXTERNAL.ADVANCED.TITLE',
                    param: 'advanced'
                }
            ];
        }
        if (value === MemberPeriods.buyNew) {
            return [
                {
                    label: 'CHANNELS.MEMBER_EXTERNAL.PERIODS_TABS.STEPS',
                    param: 'steps'
                },
                {
                    label: 'MEMBER_EXTERNAL.NEW_MEMBER.CONFIGS.TITLE',
                    param: 'configurations'
                }
            ];
        }
        console.warn('No period found');
        return null;
    }));

    inProgress$ = this.#memberExtSrv.channelOptions.loading$();
    status$: Observable<boolean> = this.period$.pipe(
        filter(Boolean),
        switchMap(period => this.#memberExtSrv.channelOptions.get$()
            .pipe(
                filter(channelOptions => !!channelOptions?.member_operation_periods),
                map(options => options?.member_operation_periods[period]?.active ?? false)
            )));

    ngOnInit(): void {
        this.#memberExtSrv.configurations.load();
        combineLatest([
            this.#channelsService.getChannel$().pipe(
                first(Boolean),
                switchMap(channel => {
                    this.#channelId = channel.id;
                    this.load();
                    return this.#memberExtSrv.channelOptions.get$();
                }),
                filter(channelOptions => !!channelOptions?.member_operation_periods),
                takeUntilDestroyed(this.#onDestroy)
            ),
            this.period$.pipe(filter(Boolean))
        ])
            .subscribe(([channelOptions, period]) => {
                this.form.reset({ active: channelOptions.member_operation_periods[period].active });

            });
    }

    ngOnDestroy(): void {
        this.#memberExtSrv.channelOptions.clear();
    }

    save(isActive: boolean): void {
        this.save$(isActive).subscribe();
    }

    handleStatusChange(isActive: boolean): void {
        if (this.#childComponent?.form?.dirty) {
            of(null).pipe(
                delay(100),
                tap(() => this.form.controls.active.patchValue(!isActive)),
                switchMap(() =>
                    this.#msgDialogSrv.showWarn(unsavedChangesDialogData)
                ),
                switchMap(saveAccepted =>
                    saveAccepted ? this.#childComponent.save$() : of(false)
                )
            ).subscribe();
        } else {
            this.save(isActive);
        }
    }

    save$(isActive): Observable<unknown> {
        return this.period$.pipe(filter(period => !!period), switchMap(period => {
            const value = {};
            value[period] = { active: isActive };
            return this.#memberExtSrv.channelOptions.save(this.#channelId,
                // eslint-disable-next-line @typescript-eslint/naming-convention
                { member_operation_periods: value })
                .pipe(tap(() => this.load()), finalize(() => this.#ephemeralSrv.showSaveSuccess()));
        }
        ));

    }

    cancel(): void {
        this.load();
    }

    childComponentChange(child: WritingComponent): void {
        this.#childComponent = child;
    }

    private load(): void {
        this.#memberExtSrv.channelOptions.load(this.#channelId);
    }
}
