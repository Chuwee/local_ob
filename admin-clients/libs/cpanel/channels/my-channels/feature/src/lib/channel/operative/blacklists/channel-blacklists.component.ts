import { Metadata } from '@OneboxTM/utils-state';
import {
    ChannelsService, ChannelBlacklistType, GetChannelBlacklistRequest, ChannelBlacklistItem, ChannelBlacklistStatusOpts
} from '@admin-clients/cpanel/channels/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService, ObMatDialogConfig
    , SearchablePaginatedListComponent
} from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { UploadFileDirective, DownloadFileDirective } from '@admin-clients/shared/utility/directives';
import { DateTimePipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import { UntypedFormBuilder, UntypedFormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { TranslatePipe } from '@ngx-translate/core';
import { Papa } from 'ngx-papaparse';
import { combineLatest, forkJoin, Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, takeUntil, tap } from 'rxjs/operators';
import { ChannelOperativeService } from '../channel-operative.service';
import { CreateBlacklistItemDialogComponent } from './create-blacklist-item-dialog/create-blacklist-item-dialog.component';

@Component({
    selector: 'app-channel-blacklists',
    templateUrl: './channel-blacklists.component.html',
    styleUrls: ['./channel-blacklists.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        FlexLayoutModule,
        MatSlideToggle,
        MatButton,
        MatIconButton,
        MatIcon,
        MatMenu,
        MatMenuItem,
        MatTooltip,
        MatProgressSpinner,
        MatMenuTrigger,
        TranslatePipe,
        FormContainerComponent,
        SearchablePaginatedListComponent,
        UploadFileDirective,
        DownloadFileDirective,
        DateTimePipe
    ]
})
export class ChannelBlacklistsComponent implements OnInit, OnDestroy, WritingComponent {
    private _onDestroy: Subject<void>;
    private _channelId: number;
    private readonly PAGE_LIMIT = 10;

    private _filters: { [key in ChannelBlacklistType]: GetChannelBlacklistRequest } = {
        [ChannelBlacklistType.email]: { limit: this.PAGE_LIMIT, offset: 0 },
        [ChannelBlacklistType.nif]: { limit: this.PAGE_LIMIT, offset: 0 }
    };

    readonly CSV_TPLS = {
        [ChannelBlacklistType.email]: {
            name: `${ChannelBlacklistType.email.toLowerCase()}-blacklist-template.csv`,
            data: 'email\r\ntest@mail.com\r\nanothertest@mail.com\r\nyetanothertest@domain.com'
        },
        [ChannelBlacklistType.nif]: {
            name: `${ChannelBlacklistType.nif.toLowerCase()}-blacklist-template.csv`,
            data: 'nif\r\n32734528P\r\n83107263Y\r\n95646539G'
        }
    };

    channelBlacklistTypes = ChannelBlacklistType;
    dateTimeFormats = DateTimeFormats;
    form: UntypedFormGroup;
    emailBlacklistData$: Observable<ChannelBlacklistItem[]>;
    emailBlacklistMetadata$: Observable<Metadata>;
    emailBlacklistInProgress$: Observable<boolean>;
    nifBlacklistData$: Observable<ChannelBlacklistItem[]>;
    nifBlacklistMetadata$: Observable<Metadata>;
    nifBlacklistInProgress$: Observable<boolean>;
    isInProgress$: Observable<boolean>;
    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(
            map(result => result.matches)
        );

    constructor(
        private _channelsService: ChannelsService,
        private _channelOperativeService: ChannelOperativeService,
        private _fb: UntypedFormBuilder,
        private _ephemeralMessageService: EphemeralMessageService,
        private _msgDialogService: MessageDialogService,
        private _breakpointObserver: BreakpointObserver,
        private _matDialog: MatDialog,
        private _csvParser: Papa
    ) { }

    ngOnInit(): void {
        this._onDestroy = new Subject<void>();

        this.initForm();

        this._channelsService.getChannel$()
            .pipe(first(channel => channel !== null))
            .subscribe(channel => {
                this._channelId = channel.id;
                this.loadBlacklistStatus(ChannelBlacklistType.email);
                this.loadBlacklistStatus(ChannelBlacklistType.nif);
            });

        combineLatest([
            this._channelOperativeService.getChannelBlacklistStatus$(ChannelBlacklistType.email),
            this._channelOperativeService.getChannelBlacklistStatus$(ChannelBlacklistType.nif)
        ])
            .pipe(
                filter(([emailStatus, nifStatus]) => !!emailStatus && !!nifStatus),
                takeUntil(this._onDestroy)
            )
            .subscribe(([emailStatus, nifStatus]) => this.updateFormData(emailStatus.status, nifStatus.status));

        this.emailBlacklistData$ = this._channelOperativeService.getChannelBlacklistData$(ChannelBlacklistType.email);
        this.emailBlacklistMetadata$ = this._channelOperativeService.getChannelBlacklistMetadata$(ChannelBlacklistType.email);
        this.emailBlacklistInProgress$ = this._channelOperativeService.isChannelBlacklistInProgress$(ChannelBlacklistType.email);

        this.nifBlacklistData$ = this._channelOperativeService.getChannelBlacklistData$(ChannelBlacklistType.nif);
        this.nifBlacklistMetadata$ = this._channelOperativeService.getChannelBlacklistMetadata$(ChannelBlacklistType.nif);
        this.nifBlacklistInProgress$ = this._channelOperativeService.isChannelBlacklistInProgress$(ChannelBlacklistType.nif);

        this.isInProgress$ = booleanOrMerge([
            this._channelOperativeService.isChannelBlacklistInProgress$(),
            this._channelOperativeService.isChannelBlacklistStatusInProgress$(),
            this._channelOperativeService.isChannelBlacklistItemInProgress$()
        ]);
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    save$(): Observable<[void, void]> {
        if (this.form.valid && this.form.dirty) {
            const data = this.form.value;
            return forkJoin([
                this._channelOperativeService.updateChannelBlacklistStatus(this._channelId, ChannelBlacklistType.email, {
                    status: data.emailsBlacklistEnabled ? ChannelBlacklistStatusOpts.enabled : ChannelBlacklistStatusOpts.disabled
                }),
                this._channelOperativeService.updateChannelBlacklistStatus(this._channelId, ChannelBlacklistType.nif, {
                    status: data.nifsBlacklistEnabled ? ChannelBlacklistStatusOpts.enabled : ChannelBlacklistStatusOpts.disabled
                })
            ]).pipe(
                tap(() => {
                    this._ephemeralMessageService.showSuccess({
                        msgKey: 'CHANNELS.OPTIONS.UPDATE_BLACKLISTS_STATUS_SUCCESS'
                    });
                })
            );
        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadBlacklistStatus(ChannelBlacklistType.email);
            this.loadBlacklistStatus(ChannelBlacklistType.nif);
        });
    }

    cancel(): void {
        this.loadAllData();
    }

    filterChangeHandler(type: ChannelBlacklistType, filters: GetChannelBlacklistRequest): void {
        this._filters[type] = {
            ...this._filters[type],
            ...filters
        };
        this.loadBlacklist(type);
    }

    openCreateBlacklistItemDialog(type: ChannelBlacklistType): void {
        this._matDialog.open(CreateBlacklistItemDialogComponent, new ObMatDialogConfig({ type }))
            .beforeClosed()
            .subscribe(isSaved => {
                if (isSaved) {
                    this.loadBlacklist(type);
                }
            });
    }

    openDeleteBlacklistItemDialog(type: ChannelBlacklistType, value: string): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'CHANNELS.BLACKLISTS.DELETE_BLACKLIST_ITEM_TITLE',
            message: 'CHANNELS.BLACKLISTS.DELETE_BLACKLIST_ITEM_MSG',
            messageParams: { itemValue: value },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(isConfirmed => {
                if (isConfirmed) {
                    this._channelOperativeService.deleteChannelBlacklistItem(this._channelId, type, value)
                        .subscribe(() => {
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'CHANNELS.OPTIONS.DELETE_BLACKLIST_ITEM_SUCCESS',
                                msgParams: { itemValue: value }
                            });
                            this.loadBlacklist(type);
                        });
                }
            });
    }

    handleFileInput(type: ChannelBlacklistType, files: FileList): void {
        const file = files[0];
        if (file) {
            const creationDate = new Date().toISOString();
            file.text().then(res => {
                const blacklistItems = this._csvParser.parse(res, {
                    header: true,
                    transformHeader: _ => 'value',
                    delimiter: ';',
                    skipEmptyLines: true
                }).data.map((res: { value: string }) => ({ ...res, creation_date: creationDate }));

                this._channelOperativeService.createChannelBlacklist(this._channelId, type, blacklistItems)
                    .subscribe(() => {
                        this._ephemeralMessageService.showSaveSuccess();
                        this.loadBlacklist(type);
                    });
            });
        }
    }

    openDeleteBlacklistDialog(type: ChannelBlacklistType): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'CHANNELS.BLACKLISTS.DELETE_BLACKLIST_TITLE',
            message: 'CHANNELS.BLACKLISTS.DELETE_BLACKLIST_MSG',
            messageParams: { blacklistType: type },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(isConfirmed => {
                if (isConfirmed) {
                    this._channelOperativeService.deleteChannelBlacklist(this._channelId, type)
                        .subscribe(() => {
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'CHANNELS.OPTIONS.DELETE_BLACKLIST_SUCCESS',
                                msgParams: { blacklistType: type }
                            });
                            this.loadBlacklist(type);
                        });
                }
            });
    }

    private initForm(): void {
        this.form = this._fb.group({
            emailsBlacklistEnabled: false,
            nifsBlacklistEnabled: false
        });
    }

    private updateFormData(emailsStatus: ChannelBlacklistStatusOpts, nifsStatus: ChannelBlacklistStatusOpts): void {
        this.form.patchValue({
            emailsBlacklistEnabled: emailsStatus === ChannelBlacklistStatusOpts.enabled,
            nifsBlacklistEnabled: nifsStatus === ChannelBlacklistStatusOpts.enabled
        });
        this.form.markAsPristine();
    }

    private loadBlacklist(type: ChannelBlacklistType): void {
        this._channelOperativeService.loadChannelBlacklist(this._channelId, type, this._filters[type]);
    }

    private loadBlacklistStatus(type: ChannelBlacklistType): void {
        this._channelOperativeService.loadBlacklistStatus(this._channelId, type);
    }

    private loadAllData(): void {
        this.loadBlacklist(ChannelBlacklistType.email);
        this.loadBlacklist(ChannelBlacklistType.nif);
        this.loadBlacklistStatus(ChannelBlacklistType.email);
        this.loadBlacklistStatus(ChannelBlacklistType.nif);
    }
}
