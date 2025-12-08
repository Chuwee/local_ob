import { scrollIntoFirstInvalidFieldOrErrorMsg, FormControlErrorsComponent } from '@OneboxTM/feature-form-control-errors';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    GetTotalRecipientsRequest, NotificationChannelsScope, NotificationContent,
    NotificationContentType, NotificationDetail, NotificationFieldsRestriction,
    NotificationRecipients, NotificationSessionsScope, NotificationStatus,
    NotificationsService, PutNotificationEmail, exportDataRecipients
} from '@admin-clients/cpanel/notifications/data-access';
import { eventChannelsProviders } from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventListElement, EventsService } from '@admin-clients/cpanel/promoters/events/data-access';
import { EntitiesBaseService, TableColConfigService } from '@admin-clients/shared/common/data-access';
import {
    DialogSize, EphemeralMessageService, MessageDialogService,
    ObMatDialogConfig, ExportDialogComponent, GoBackComponent,
    SelectServerSearchComponent, RichTextAreaComponent, DateTimeModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { DateTimeFormats, ExportFormat } from '@admin-clients/shared/data-access/models';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import {
    booleanOrMerge,
    dateIsBefore, dateIsSameOrBefore, dateTimeGroupValidator, dateTimeValidator
} from '@admin-clients/shared/utility/utils';
import { NgIf, AsyncPipe, NgClass } from '@angular/common';
import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit, QueryList, inject } from '@angular/core';
import { ExtendedModule } from '@angular/flex-layout/extended';
import { FlexModule } from '@angular/flex-layout/flex';
import { UntypedFormBuilder, UntypedFormGroup, Validators, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { Router } from '@angular/router';
import { TranslateService, TranslatePipe } from '@ngx-translate/core';
import moment from 'moment';
import { combineLatest, debounceTime, filter, forkJoin, map, Observable, Subject, takeUntil, throwError } from 'rxjs';
import { pairwise, switchMap, tap } from 'rxjs/operators';
import { NotificationChannelsComponent } from '../channels/notification-channels.component';
import { NotificationSummaryComponent } from '../notification-summary/notification-summary.component';
import { SendEmailDialogComponent } from '../send-email-dialog/send-email-dialog.component';
import { SendTestEmailDialogComponent } from '../send-test-email-dialog/send-test-email-dialog.component';
import { NotificationSessionsComponent } from '../sessions/notification-sessions.component';

@Component({
    selector: 'app-notification-details',
    templateUrl: './notification-details.component.html',
    styleUrls: ['../../list/notifications-list.component.scss', './notification-details.component.scss'],
    providers: [
        eventChannelsProviders
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        NgIf, FlexModule, GoBackComponent, MaterialModule, NgClass,
        ExtendedModule, FormContainerComponent, FormsModule, ReactiveFormsModule, FormControlErrorsComponent,
        SelectServerSearchComponent, NotificationSessionsComponent, NotificationChannelsComponent,
        DateTimeModule, RichTextAreaComponent, NotificationSummaryComponent, TranslatePipe, AsyncPipe
    ]
})
export class NotificationDetailsComponent implements OnInit, OnDestroy, AfterViewInit {
    private _tableSrv = inject(TableColConfigService);

    private _onDestroy = new Subject<void>();
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;
    private _recipientsFilter: GetTotalRecipientsRequest;

    readonly dateTimeFormats = DateTimeFormats;

    isLoadingOrSaving$: Observable<boolean>;
    notificationDetail$: Observable<NotificationDetail>;
    isOperatorUser$: Observable<boolean>;
    form: UntypedFormGroup;
    generalDataForm: UntypedFormGroup;
    contentsForm: UntypedFormGroup;
    recipientsForm: UntypedFormGroup;
    notificationDetail: NotificationDetail;
    notificationStatus = NotificationStatus;
    language: string;
    events$: Observable<EventListElement[]>;
    entityId: number;
    eventId$: Observable<number>;
    totalRecipients$: Observable<number>;
    totalRecipientsLoading$: Observable<boolean>;
    maxEmailsPermited: number;
    maxEmailsExceeded: boolean;
    isExportLoading$: Observable<boolean>;

    errors = {
        saveNotificationSessions: false,
        saveNotificationChannels: false
    };

    constructor(
        private _fb: UntypedFormBuilder,
        private _ref: ChangeDetectorRef,
        private _notificationsService: NotificationsService,
        private _translateService: TranslateService,
        private _authSrv: AuthenticationService,
        private _ephemeralMessageService: EphemeralMessageService,
        private _msgDialogService: MessageDialogService,
        private _router: Router,
        private _entitiesService: EntitiesBaseService,
        private _eventsService: EventsService,
        private _matDialog: MatDialog
    ) { }

    ngOnInit(): void {
        this.isLoadingOrSaving$ = booleanOrMerge([
            this._notificationsService.isNotificationDetailsLoading$(),
            this._notificationsService.isNotificationContentsLoading$()]);

        this.notificationDetail$ = this._notificationsService.getNotificationDetail$().pipe(
            filter(notDetail => !!notDetail),
            tap(notDetail => {
                this.notificationDetail = notDetail;
                this.entityId = notDetail.entity.id;
            })
        );

        this.isOperatorUser$ = this._authSrv.hasLoggedUserSomeRoles$([UserRoles.OPR_MGR]);

        this.events$ = this._eventsService.eventsList.getData$()
            .pipe(
                filter(events => !!events),
                map(events => events.map(event => new EventListElement(event)))
            );

        this.totalRecipients$ = this._notificationsService.getTotalRecipients$().pipe(
            filter(recipients => !!recipients),
            tap(recipients => this.maxEmailsExceeded = (recipients.total > this.maxEmailsPermited) || (recipients.total > 50000)),
            map(recipients => recipients.total));

        this.totalRecipientsLoading$ = this._notificationsService.isTotalRecipientsLoading$();
        this.isExportLoading$ = this._notificationsService.isExportRecipientsLoading$();

        this.initForms();
    }

    ngAfterViewInit(): void {
        this.refreshFormDataHandler();
    }

    ngOnDestroy(): void {
        this._notificationsService.clearNotificationDetail();
        this._notificationsService.clearTotalRecipients();
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    initForms(): void {
        this.generalDataForm = this._fb.group({
            name: [null, [
                Validators.required,
                Validators.maxLength(NotificationFieldsRestriction.notificationNameLength)
            ]]
        });

        this.contentsForm = this._fb.group({
            subject: [null, [
                Validators.maxLength(NotificationFieldsRestriction.notificationSubjectLength)
            ]],
            body: [null, [
                Validators.maxLength(NotificationFieldsRestriction.notificationBodyLength)
            ]]
        });

        this.recipientsForm = this._fb.group({
            event: [null, [Validators.required]],
            sessions: this._fb.group({
                type: [null, Validators.required],
                selected: []
            }),
            channels: this._fb.group({
                type: [null, Validators.required],
                selected: [[], Validators.required]
            }),
            purchase_date: this._fb.group({ from: null, to: null },
                { validators: [dateTimeGroupValidator(dateIsBefore, 'startDateAfterEndDate', 'from', 'to')] }
            ),
            exclude_commercial_mailing_not_allowed: [null]
        });

        this.form = this._fb.group({
            generalDataForm: this.generalDataForm,
            contentsForm: this.contentsForm,
            recipientsForm: this.recipientsForm
        });
        const today = moment();
        this.recipientsForm.get('purchase_date.to').setValidators([
            dateTimeValidator(
                dateIsSameOrBefore, 'releaseAfterSaleStart', today,
                this._translateService.instant('NOTIFICATIONS.EMAIL_NOTIFICATION.TODAY_ERROR').toLowerCase()
            )]);

        //Reset selected channels and sessions on event change
        this.eventId$ = combineLatest([this.form.get('recipientsForm.event').valueChanges, this.notificationDetail$])
            .pipe(
                debounceTime(100),
                takeUntil(this._onDestroy),
                filter(([event, notificationDetail]) => !!event && !!notificationDetail),
                map(([event, notificationDetail]) => {
                    this.recipientsForm.patchValue({
                        sessions: {
                            type: notificationDetail.recipients?.filter?.session_ids?.length ?
                                NotificationSessionsScope.restricted : NotificationSessionsScope.all,
                            selected: this.checkSelectedSessions(notificationDetail)
                        },
                        channels: {
                            type: notificationDetail.recipients?.filter?.channel_ids?.length ?
                                NotificationChannelsScope.restricted : NotificationChannelsScope.all,
                            selected: this.checkSelectedChannels(notificationDetail)
                        }
                    });
                    return event?.id;
                }));

        this.form.get('recipientsForm').valueChanges
            .pipe(
                pairwise(),
                filter(([prev, recipients]) =>
                    !!prev && (!!recipients.sessions?.type && !!recipients.channels?.type)
                    && (JSON.stringify(prev) !== JSON.stringify(recipients)) && this.recipientsForm.valid
                ),
                takeUntil(this._onDestroy)
            )
            .subscribe(([prev, recipients]) => {
                const sessions = recipients.sessions.selected?.map(session => session.id);
                const channels = recipients.channels.selected?.map(channel => channel.id);
                this._recipientsFilter = {
                    limit: 0,
                    offset: 0,
                    entity_id: this.entityId,
                    event_id: [recipients.event.id],
                    purchase_from: recipients.purchase_date.from,
                    purchase_to: recipients.purchase_date.to,
                    exclude_commercial_mailing_not_allowed: recipients.exclude_commercial_mailing_not_allowed
                };
                //Only load sessions and channels if event didn't change
                if (prev.event.id === recipients.event.id) {
                    this._recipientsFilter = {
                        ...this._recipientsFilter,
                        session_id: sessions,
                        channel_id: channels
                    };
                }
                this._notificationsService.loadNumberOfRecipients(this._recipientsFilter);
            });
    }

    save(): void {
        this.save$().subscribe(() => {
            this.form.markAsPristine();
        });
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const code = this.notificationDetail.code;
            return forkJoin(this.buildUpdateRequests$(code))
                .pipe(
                    map(() => {
                        this._notificationsService.loadNotificationDetail(code);
                        this._ephemeralMessageService.showSaveSuccess();
                    }));
        }
        this.form.markAllAsTouched();
        scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
        return throwError(() => 'invalid form');
    }

    cancel(): void {
        this._notificationsService.loadNotificationDetail(this.notificationDetail.code);
    }

    openDeleteNotificationDialog(): void {
        this._msgDialogService.showWarn({
            size: DialogSize.SMALL,
            title: 'TITLES.DELETE_EMAIL_NOTIFICATION',
            message: 'NOTIFICATIONS.EMAIL_NOTIFICATION.DELETE_WARNING_MESSAGE',
            messageParams: { notificationName: this.notificationDetail.name },
            actionLabel: 'FORMS.ACTIONS.DELETE',
            showCancelButton: true
        })
            .subscribe(success => {
                if (success) {
                    this._notificationsService.deleteNotification(this.notificationDetail.code)
                        .subscribe(() => {
                            this._ephemeralMessageService.showSuccess({
                                msgKey: 'NOTIFICATIONS.EMAIL_NOTIFICATION.DELETE_SUCCESS',
                                msgParams: { notificationName: this.notificationDetail.name }
                            });
                            this._router.navigate(['/notifications']);
                        });
                }
            });
    }

    exportRecipients(): void {
        const config = new ObMatDialogConfig({
            exportData: exportDataRecipients,
            exportFormat: ExportFormat.csv,
            selectedFields: this._tableSrv.getColumns('EXP_NOTIFICATION_DETAILS')
        });
        this._matDialog.open(ExportDialogComponent, config)
            .beforeClosed()
            .pipe(filter(Boolean))
            .subscribe(exportList => {
                const req = {
                    entity_id: this._recipientsFilter?.entity_id || this.notificationDetail.entity.id,
                    event_id: this._recipientsFilter?.event_id || this.notificationDetail.recipients?.filter?.event_ids,
                    session_id: this._recipientsFilter?.session_id || this.notificationDetail.recipients?.filter?.session_ids,
                    channel_id: this._recipientsFilter?.channel_id || this.notificationDetail.recipients?.filter?.channel_ids,
                    purchase_from: this._recipientsFilter?.purchase_from || this.notificationDetail.recipients?.filter?.purchase_date?.from,
                    purchase_to: this._recipientsFilter?.purchase_to || this.notificationDetail.recipients?.filter?.purchase_date?.to,
                    exclude_comercial_mailing_not_allowed: this._recipientsFilter?.exclude_commercial_mailing_not_allowed
                        || this.notificationDetail.recipients?.filter?.exclude_commercial_mailing_not_allowed
                };

                this._tableSrv.setColumns('EXP_NOTIFICATION_DETAILS', exportList.fields.map(resultData => resultData.field));
                this._notificationsService.exportRecipients(req, exportList, this.notificationDetail.code)
                    .pipe(filter(result => !!result.export_id))
                    .subscribe(() => {
                        this._ephemeralMessageService.showSuccess({ msgKey: 'ACTIONS.EXPORT.OK.MESSAGE' });
                    });
            });
    }

    openSendEmailModal(): void {
        this._matDialog.open(SendEmailDialogComponent, new ObMatDialogConfig({
            eventId: this.recipientsForm.get('event').value?.id, totalRecipients$: this.totalRecipients$,
            subject: this.contentsForm.get('subject').value, notCode: this.notificationDetail.code,
            sessions: this.recipientsForm.get('sessions').value,
            channels: this.recipientsForm.get('channels').value
        }))
            .beforeClosed()
            .pipe(filter(result => !!result))
            .subscribe(() => {
                this._ephemeralMessageService.showSuccess({ msgKey: 'NOTIFICATIONS.EMAIL_PROCESSED_SUCCESSFULLY' });
                this._notificationsService.loadNotificationDetail(this.notificationDetail.code);
            });
    }

    openSendTestEmailModal(): void {
        this._matDialog.open(SendTestEmailDialogComponent, new ObMatDialogConfig({
            notCode: this.notificationDetail.code
        }))
            .beforeClosed()
            .pipe(filter(result => !!result))
            .subscribe(() => {
                this._ephemeralMessageService.showSuccess({ msgKey: 'NOTIFICATIONS.TEST_EMAIL_SENT_SUCCESSFULLY' });
                this._notificationsService.loadNotificationDetail(this.notificationDetail.code);
            });
    }

    loadEventsFiltered(e): void {
        this.loadEvents(e.q);
    }

    private buildUpdateRequests$(code: string): Observable<void>[] {
        const obs$ = [];

        if (this.generalDataForm.dirty || this.recipientsForm.dirty) {
            const channelIds = this.recipientsForm.get('channels.selected').value.map(channel => channel.id);
            const putChannelIds = this.checkChannelScope() ? [] : channelIds;

            const sessionIds = this.recipientsForm.get('sessions.selected').value.map(session => session.id);
            const putSessionIds = this.checkSessionsScope() ? [] : sessionIds;

            const putRecipients: NotificationRecipients = {
                filter: {
                    event_ids: [this.recipientsForm.get('event').value?.id],
                    session_ids: putSessionIds,
                    channel_ids: putChannelIds,
                    purchase_date: {
                        from: this.recipientsForm.get('purchase_date.from').value || null,
                        to: this.recipientsForm.get('purchase_date.to').value || null,
                        override: true
                    },
                    exclude_commercial_mailing_not_allowed: this.recipientsForm.get('exclude_commercial_mailing_not_allowed').value || false
                }
            };

            const notification: PutNotificationEmail = {
                code,
                name: this.generalDataForm.value.name,
                recipients: putRecipients
            };
            obs$.push(this._notificationsService.updateNotification(notification));
        }
        if (this.contentsForm.dirty) {
            const notificationContents: NotificationContent[] = [
                {
                    value: !this.contentsForm.value.subject ? '' : this.contentsForm.value.subject,
                    type: NotificationContentType.subject,
                    language: this.language
                },
                {
                    value: !this.contentsForm.value.body ? '' : this.contentsForm.value.body,
                    type: NotificationContentType.body,
                    language: this.language
                }
            ];
            obs$.push(this._notificationsService.updateNotificationContents(code, notificationContents));
        }

        return obs$;
    }

    private refreshFormDataHandler(): void {
        this._notificationsService.getNotificationDetail$()
            .pipe(
                filter(detail => !!detail),
                switchMap(detail => this.mapToDetailLangAndContents$(detail)),
                takeUntil(this._onDestroy)
            )
            .subscribe(({ notificationDetail, language, notificationContents }) => {
                this.language = language;
                this.patchForm(notificationDetail, notificationContents, language);
            });
    }

    private mapToDetailLangAndContents$(notificationDetail: NotificationDetail):
        Observable<{ notificationDetail: NotificationDetail; language: string; notificationContents: NotificationContent[] }> {
        this._notificationsService.loadNotificationContents(notificationDetail.code);
        this.loadEvents();

        if (this.language) {
            return this._notificationsService.getNotificationContents$().pipe(
                filter(contents => !!contents),
                map(notificationContents => ({ notificationDetail, language: this.language, notificationContents }))
            );
        }

        this._entitiesService.loadEntity(notificationDetail.entity.id);
        return combineLatest([
            this._entitiesService.getEntity$(),
            this._notificationsService.getNotificationContents$()
        ]).pipe(
            filter(([entity, contents]) => !!entity && !!contents),
            tap(([entity]) => {
                this.maxEmailsPermited = entity.settings?.notifications?.email?.send_limit;
            }),
            map(([entity, notificationContents]) => ({
                notificationDetail,
                language: entity.settings?.languages?.default,
                notificationContents
            }))
        );
    }

    private patchForm(detail: NotificationDetail, content: NotificationContent[], language: string): void {
        const subject = content.find(content => this.hasLanguageAndType(content, language, NotificationContentType.subject))?.value;
        const body = content.find(content => this.hasLanguageAndType(content, language, NotificationContentType.body))?.value;
        const eventId = detail.recipients?.filter?.event_ids?.[0];

        this.generalDataForm.patchValue({ name: detail.name });
        this.contentsForm.patchValue({ subject, body });
        this.recipientsForm.patchValue({
            event: eventId && { id: eventId } || null
        });
        this.recipientsForm.patchValue({
            purchase_date: {
                from: detail.recipients?.filter?.purchase_date?.from,
                to: detail.recipients?.filter?.purchase_date?.to
            },
            exclude_commercial_mailing_not_allowed: detail.recipients?.filter?.exclude_commercial_mailing_not_allowed
        }, { emitEvent: false });

        this._ref.detectChanges();
        this.form.markAsPristine();
    }

    private hasLanguageAndType(content: NotificationContent, language: string, type: NotificationContentType): boolean {
        return content.language === language && content.type === type;
    }

    private loadEvents(searchValue = ''): void {
        this._eventsService.eventsList.load({
            limit: 100,
            offset: 0,
            q: searchValue,
            status: [],
            entityId: this.entityId
        });
        this._ref.detectChanges();
    }

    private checkSelectedSessions(notificationDetail): boolean {
        return notificationDetail.recipients?.filter?.event_ids[0] === this.recipientsForm.get('event').value?.id ?
            notificationDetail.recipients?.filter?.session_ids.map(id => ({ id })) : [];
    }

    private checkSelectedChannels(notificationDetail): boolean {
        return notificationDetail.recipients?.filter?.event_ids[0] === this.recipientsForm.get('event').value?.id ?
            notificationDetail.recipients?.filter?.channel_ids.map(id => ({ id })) : [];
    }

    private checkChannelScope(): boolean {
        return this.recipientsForm.get('channels.type').value === NotificationChannelsScope.all;
    }

    private checkSessionsScope(): boolean {
        return this.recipientsForm.get('sessions.type').value === NotificationSessionsScope.all;
    }
}
