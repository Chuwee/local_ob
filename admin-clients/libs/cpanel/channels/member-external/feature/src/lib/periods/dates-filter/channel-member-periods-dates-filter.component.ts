import { ChannelsService } from '@admin-clients/cpanel/channels/data-access';
import { AccessUser, ChannelMemberExternalService, MemberPeriods } from '@admin-clients/cpanel-channels-member-external-data-access';
import {
    DateTimeModule, DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig, SearchTableComponent
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, UntypedFormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { RouterModule } from '@angular/router';
import { TranslatePipe } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, first, map, startWith, take } from 'rxjs/operators';
import { ChannelMemberPeriodsDatesFilterDialogComponent } from './dates-filter-dialog/channel-member-periods-dates-filter-dialog.component';
import { ImportPeriodsDatesDialogComponent } from './import-dialog/import-periods-dates-dialog.component';

@Component({
    selector: 'app-channel-member-periods-dates-filter',
    templateUrl: './channel-member-periods-dates-filter.component.html',
    styleUrls: ['./channel-member-periods-dates-filter.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        MaterialModule,
        TranslatePipe,
        ReactiveFormsModule,
        RouterModule,
        FlexLayoutModule,
        AsyncPipe,
        SearchTableComponent, DateTimePipe, DateTimeModule
    ]
})
export class ChannelMemberPeriodsDatesFilterComponent implements OnInit, OnDestroy {
    readonly #onDestroy = inject(DestroyRef);
    readonly #channelsService = inject(ChannelsService);
    readonly #memberExtSrv = inject(ChannelMemberExternalService);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #messageDialogSrv = inject(MessageDialogService);
    readonly #fb = inject(FormBuilder);
    readonly #matDialog = inject(MatDialog);
    readonly #breakpointObserver = inject(BreakpointObserver);

    @Input() form: FormGroup<{
        enabled: FormControl;
        default_access: FormControl;
        access: FormGroup<{
            user: FormControl;
            date: FormControl;
        }>;
    }>;

    @Input() period: MemberPeriods;
    readonly columns = ['id', 'date', 'actions'];
    readonly loading$ = this.#memberExtSrv.datesFilter.loading$();
    readonly dateTimeFormats = DateTimeFormats;
    readonly isHandsetOrTablet$: Observable<boolean> = this.#breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    datesFilter$: Observable<AccessUser[]>;
    #channelId: number;

    get memberAccessFormGroup(): FormGroup {
        return this.form.get('access') as FormGroup;
    }

    ngOnInit(): void {
        this.#channelsService.getChannel$()
            .pipe(
                first(Boolean),
                takeUntilDestroyed(this.#onDestroy)
            )
            .subscribe(channel => {
                this.#channelId = channel.id;
            });

        this.#memberExtSrv.datesFilter.get$().pipe(filter(Boolean))
            .subscribe(accessList => {
                accessList.access.forEach(access => {
                    this.memberAccessFormGroup.setControl(String(access.user), this.#fb.group({
                        user: String(access.user),
                        date: access.date
                    }));
                });
                this.form.controls.enabled.patchValue(accessList.enabled);
                this.form.controls.default_access.patchValue(accessList.default_access);
                this.form.markAsPristine();
                this.form.updateValueAndValidity();
            });

        this.datesFilter$ = this.memberAccessFormGroup.valueChanges
            .pipe(
                startWith([]),
                takeUntilDestroyed(this.#onDestroy),
                map(memberAccessFormGroup => {
                    const access: AccessUser[] = [];
                    Object.keys(memberAccessFormGroup).forEach(user => {
                        access.push({ user, date: memberAccessFormGroup[user].date });
                    });
                    return access;
                })
            );

        this.form.controls.enabled.valueChanges
            .pipe(takeUntilDestroyed(this.#onDestroy))
            .subscribe(value => {
                if (!value) {
                    this.form.controls.default_access.removeValidators([Validators.required]);
                    this.form.controls.default_access.disable();
                } else {
                    this.form.controls.default_access.addValidators([Validators.required]);
                    this.form.controls.default_access.enable();
                }
            });
    }

    ngOnDestroy(): void {
        this.#memberExtSrv.datesFilter.clear();
    }

    open(): void {
        this.#matDialog.closeAll();
        this.#matDialog.open(ChannelMemberPeriodsDatesFilterDialogComponent, new ObMatDialogConfig({
            channelId: this.#channelId
        }))
            .beforeClosed()
            .pipe(
                filter(Boolean),
                take(1)
            )
            .subscribe(data => {
                data.access.user.forEach(user => {
                    this.memberAccessFormGroup.setControl(String(user), this.#fb.group({
                        user: String(user),
                        date: data.access.date
                    }));
                });

                this.form.markAsDirty();
                this.form.updateValueAndValidity();
            });
    }

    openDeleteRow(row: AccessUser): void {
        this.#messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'MEMBER_EXTERNAL.BUY_SEAT.CONFIGS.LIMIT_PORTAL_ACCESS.WARNING_TITLE',
            message: 'MEMBER_EXTERNAL.BUY_SEAT.CONFIGS.LIMIT_PORTAL_ACCESS.WARNING_MESSAGE',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(ok => {
                if (ok) {
                    const accessUsersToDelete = Object.values(this.form.controls.access.value)
                        .find(access => access.user === row.user && access.date === row.date);

                    (this.memberAccessFormGroup as UntypedFormGroup).removeControl(String(accessUsersToDelete.user));
                    this.form.markAsDirty();
                    this.form.updateValueAndValidity();
                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'MEMBER_EXTERNAL.BUY_SEAT.CONFIGS.LIMIT_PORTAL_ACCESS.DELETE_OK'
                    });
                }
            });
    }

    openDeleteList(): void {
        this.#messageDialogSrv.showWarn({
            size: DialogSize.SMALL,
            title: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.DELETE_LIST_WARNING_TITLE',
            message: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.DELETE_LIST_WARNING_MESSAGE',
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(ok => {
                if (ok) {
                    Object.values(this.form.controls.access.value).forEach(accessUser =>
                        (this.memberAccessFormGroup as UntypedFormGroup).removeControl(String(accessUser.user))
                    );
                    this.form.markAsDirty();
                    this.form.updateValueAndValidity();

                    this.#ephemeralSrv.showSuccess({
                        msgKey: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.DELETE_LIST_OK'
                    });
                }
            });
    }

    importPeriodsDates(): void {
        const data = new ObMatDialogConfig<{ fields: string[] }>({
            fields: ['user', 'date', 'time']
        });
        this.#matDialog.open(
            ImportPeriodsDatesDialogComponent,
            data
        )
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(periodsDates => {
                periodsDates.forEach(periodDate => {
                    this.memberAccessFormGroup.setControl(String(periodDate.user), this.#fb.group({
                        user: String(periodDate.user),
                        date: new Date(periodDate.date + ' ' + periodDate.time)
                    }));
                });

                this.form.markAsDirty();
                this.form.updateValueAndValidity();
                this.#ephemeralSrv.showSuccess({
                    msgKey: 'MEMBER_EXTERNAL.LIMIT_PORTAL_ACCESS.IMPORT_OK'
                });
            });
    }

    filterUser(q: string, elem: AccessUser): boolean {
        return elem.user.includes(q);
    }
}
