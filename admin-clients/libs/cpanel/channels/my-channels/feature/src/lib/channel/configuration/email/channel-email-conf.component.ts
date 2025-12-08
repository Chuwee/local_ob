import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import {
    ChannelsService, ChannelsExtendedService, EmailServerType,
    EmailServerSecurityType, EmailServerConf, NotificationEmailTemplate, EmailServerTestRequest,
    ChannelsPipesModule
} from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService } from '@admin-clients/cpanel/core/data-access';
import { EphemeralMessageService, HelpButtonComponent } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import { ErrorIconDirective } from '@admin-clients/shared/utility/directives';
import { ErrorMessage$Pipe } from '@admin-clients/shared/utility/pipes';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, ElementRef, OnDestroy, OnInit } from '@angular/core';
import { FlexLayoutModule } from '@angular/flex-layout';
import {
    ReactiveFormsModule, UntypedFormArray,
    UntypedFormBuilder, UntypedFormControl, UntypedFormGroup, Validators
} from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatOption } from '@angular/material/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatError, MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelect } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { TranslatePipe } from '@ngx-translate/core';
import { combineLatest, Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, take, takeUntil, tap } from 'rxjs/operators';

@Component({
    selector: 'app-channel-email-conf',
    templateUrl: './channel-email-conf.component.html',
    styleUrls: ['./channel-email-conf.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        NgFor, NgIf, AsyncPipe, ReactiveFormsModule, FlexLayoutModule, TranslatePipe,
        FormContainerComponent, ChannelsPipesModule, HelpButtonComponent, ErrorIconDirective, ErrorMessage$Pipe,
        MatExpansionModule, MatRadioModule, MatProgressSpinner, MatError, MatTableModule,
        MatInput, MatSelect, MatCheckbox, MatSuffix, MatIcon, MatButton, MatLabel, MatFormField, MatOption
    ]
})
export class ChannelEmailConfComponent implements OnInit, OnDestroy, WritingComponent {
    private readonly _onDestroy = new Subject<void>();

    private _channelId: number;
    private _userEmail: string;

    isHandsetOrTablet$: Observable<boolean> = this._breakpointObserver
        .observe([Breakpoints.Handset, Breakpoints.Tablet])
        .pipe(map(result => result.matches));

    readonly emailServerType = EmailServerType;
    readonly conectionSecurityTypes = Object.values(EmailServerSecurityType);
    readonly displayedColumns = ['type', 'from', 'alias', 'cco'];
    form: UntypedFormGroup;
    serverForm: UntypedFormGroup;
    templatesForm: UntypedFormArray;
    reqInProgress$: Observable<boolean>;
    isTestEnabled: boolean;
    error$: Observable<HttpErrorResponse>;
    emailServerConfig$: Observable<EmailServerConf>;
    notificationEmailTemplates$: Observable<NotificationEmailTemplate[]>;

    constructor(
        private _elRef: ElementRef,
        private _ephemeralSrv: EphemeralMessageService,
        private _fb: UntypedFormBuilder,
        private _breakpointObserver: BreakpointObserver,
        private _channelSrv: ChannelsService,
        private _channeExtSrv: ChannelsExtendedService,
        private _auth: AuthenticationService
    ) {
    }

    ngOnInit(): void {
        this.reqInProgress$ = booleanOrMerge([
            this._channeExtSrv.isEmailServerConfigLoading$(),
            this._channeExtSrv.isEmailServerConfigUpdating$(),
            this._channeExtSrv.isNotificationEmailTemplatesLoading$(),
            this._channeExtSrv.isNotificationEmailTemplatesUpdating$(),
            this._channeExtSrv.isEmailServerConfigTesting$()
        ]);
        this.error$ = combineLatest([
            this._channeExtSrv.getEmailServerConfigError$(),
            this._channeExtSrv.getNotificationEmailTemplatesError$(),
            this._channeExtSrv.getEmailServerConfigTestingError$()
        ]).pipe(map(errors => errors?.length && errors[0] || null));
        this._auth.getLoggedUser$()
            .pipe(first(user => user !== null))
            .subscribe(user => this._userEmail = user.email);
        this.emailServerConfig$ = this._channeExtSrv.getEmailServerConfig$();
        this.notificationEmailTemplates$ = this._channeExtSrv.getNotificationEmailTemplates$();
        this._channelSrv.getChannel$().pipe(take(1)).subscribe(channel => this.loadComponentData(channel.id));
        this.initForm();
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._channeExtSrv.clearEmailServerConfig();
        this._channeExtSrv.clearNotificationEmailTemplates();
    }

    save$(): Observable<void[]> {
        if (this.isValid()) {
            return combineLatest([
                this._channeExtSrv.updateEmailServerConfig(this._channelId, this.getFormServerConfValue()),
                this._channeExtSrv.updateNotificationEmailTemplates(this._channelId, this.getFormTemplatesValues(this.templatesForm.value))
            ])
                .pipe(tap(() => {
                    this._ephemeralSrv.showSaveSuccess();
                }));
        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadComponentData();
        });
    }

    cancel(): void {
        this._channeExtSrv.loadEmailServerConfig(this._channelId);
    }

    passwordFocusIn(): void {
        const pswControl = this.serverForm.get('configuration.password') as UntypedFormControl;
        if (!pswControl.dirty) {
            pswControl.setValue('');
            pswControl.markAsPristine();
        }
    }

    passwordFocusOut(): void {
        const pswControl = this.serverForm.get('configuration.password') as UntypedFormControl;
        if (!pswControl.dirty) {
            this._channeExtSrv.getEmailServerConfig$()
                .pipe(take(1))
                .subscribe(serverConfig => {
                    pswControl.setValue(serverConfig.configuration?.password ?? null);
                    pswControl.markAsPristine();
                });
        }
    }

    testEmail(): void {
        const serverConfig: EmailServerTestRequest = {
            delivery_email_address: this._userEmail,
            ...this.serverForm.get('configuration').value
        };
        this._channeExtSrv.sendEmailServerTest(this._channelId, serverConfig)
            .subscribe(resp => {
                if (resp) {
                    this._ephemeralSrv.showSuccess({ msgKey: 'CHANNELS.EMAIL_SERVER.TEST_SUCCESS' });
                }
            });
    }

    private loadComponentData(channelId: number = null): void {
        channelId ??= this._channelId;
        this._channelId = channelId;
        this._channeExtSrv.loadEmailServerConfig(channelId);
        this._channeExtSrv.loadNotificationEmailTemplates(channelId);
    }

    private initForm(): void {
        // form definition
        this.serverForm = this._fb.group({
            type: null,
            configuration: this._fb.group({
                server: [null, Validators.required],
                port: [null, Validators.required],
                user: null,
                password: null,
                security: [null, Validators.required],
                requireAuth: null
            })
        });
        this.templatesForm = this._fb.array([]);
        this.form = this._fb.group({
            server: this.serverForm,
            templates: this.templatesForm
        });
        // form behaviours
        this.serverForm.get('type').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(type => {
                if (type === EmailServerType.onebox) {
                    this.serverForm.get('configuration').disable();
                } else {
                    this.serverForm.get('configuration').enable();
                }
            });
        this.serverForm.get('configuration').valueChanges
            .pipe(takeUntil(this._onDestroy))
            .subscribe(config => {
                this.isTestEnabled = this.serverForm.get('type').value !== EmailServerType.onebox && !!config.server && !!config.port;
            });
        // form data load-set
        combineLatest([
            this._channeExtSrv.getEmailServerConfig$(),
            this._channeExtSrv.getNotificationEmailTemplates$()
        ])
            .pipe(
                filter(sources => sources.every(source => !!source)),
                takeUntil(this._onDestroy)
            )
            .subscribe(([emailServerConfig, notificationEmailTemplates]) => {
                this.form.reset();
                if (!this.templatesForm.controls.length) {
                    this.templatesForm.clear();
                    notificationEmailTemplates.forEach(() =>
                        this.templatesForm.push(
                            this._fb.group({
                                type: null,
                                from: [null, [Validators.required, Validators.email]],
                                cco: null,
                                alias: null
                            }))
                    );
                }
                this.form.setValue({
                    server: {
                        type: emailServerConfig.type,
                        configuration: {
                            server: emailServerConfig.configuration?.server || null,
                            port: emailServerConfig.configuration?.port || null,
                            user: emailServerConfig.configuration?.user || null,
                            password: emailServerConfig.configuration?.password || null,
                            security: emailServerConfig.configuration?.security || EmailServerSecurityType.none,
                            requireAuth: emailServerConfig.configuration?.require_auth || null
                        }
                    },
                    templates: notificationEmailTemplates.map(template => ({
                        type: template.type,
                        from: template.from ?? null,
                        cco: template.cco ?? null,
                        alias: template.alias ?? null
                    }))
                });
            });
    }

    private getFormServerConfValue(): EmailServerConf {
        const fv = this.serverForm.value;
        return {
            type: fv.type,
            configuration: fv.type === EmailServerType.onebox ? undefined :
                {
                    server: fv.configuration?.server ?? undefined,
                    port: fv.configuration?.port ?? undefined,
                    user: fv.configuration?.user ?? undefined,
                    password: (this.serverForm.get('configuration.password').dirty && fv.configuration?.password) ?? undefined,
                    security: fv.configuration?.security ?? undefined,
                    require_auth: fv.configuration?.requireAuth ?? undefined
                }
        };
    }

    private getFormTemplatesValues(templates: NotificationEmailTemplate[]): NotificationEmailTemplate[] {
        return templates.map(template => ({
            type: template.type,
            from: template.from ?? undefined,
            alias: template.alias ?? undefined,
            cco: template.cco ?? undefined
        }));
    }

    private isValid(): boolean {
        if (this.form.valid) {
            return true;
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(this._elRef.nativeElement);
        }
    }
}
