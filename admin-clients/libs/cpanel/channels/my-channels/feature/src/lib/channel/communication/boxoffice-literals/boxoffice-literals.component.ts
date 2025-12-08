import { ChannelContent, ChannelContentType } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsService, ChannelsExtendedService } from '@admin-clients/cpanel/channels/data-access';
import { EntityProfile, EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { booleanOrMerge } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, combineLatest, firstValueFrom, forkJoin, Observable, of, Subject } from 'rxjs';
import { filter, first, map, shareReplay, takeUntil, tap } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../container/channel-communication-notifier.service';

@Component({
    selector: 'app-boxoffice-literals',
    templateUrl: './boxoffice-literals.component.html',
    styleUrls: ['./boxoffice-literals.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class BoxofficeLiteralsComponent implements OnInit, OnDestroy {
    private _onDestroy = new Subject<void>();
    private _channelId: number;
    private _selectedLanguage = new BehaviorSubject<string>(null);

    private get _formLiteralValues(): UntypedFormGroup {
        return this.form.get('literalValues') as UntypedFormGroup;
    }

    private get _literalsSubpath(): string {
        return this._route.snapshot.routeConfig.path?.split('/')[1];
    }

    readonly columns = ['id', 'value'];

    form: UntypedFormGroup;
    isInProgress$: Observable<boolean>;
    languageList$: Observable<string[]>;
    literals$: Observable<ChannelContent[]>;
    selectedLanguage$ = this._selectedLanguage.asObservable().pipe(tap(lang => this.loadContents(lang)));
    nonTextLiterals$: Observable<ChannelContent[]>;
    entityProfiles$: Observable<EntityProfile[]>;
    channelContentTypes = ChannelContentType;
    selectedProfile: UntypedFormControl;

    constructor(
        private _fb: UntypedFormBuilder,
        private _entitiesService: EntitiesBaseService,
        private _channelsService: ChannelsService,
        private _channelsExtSrv: ChannelsExtendedService,
        private _messageDialogService: MessageDialogService,
        private _ephemeralSrv: EphemeralMessageService,
        private _route: ActivatedRoute,
        private _translate: TranslateService,
        private _communicationNotifierService: ChannelCommunicationNotifierService
    ) { }

    ngOnInit(): void {
        this.form = this._fb.group({
            literalValues: this._fb.group({})
        });
        this.selectedProfile = this._fb.control(null);

        this.isInProgress$ = booleanOrMerge([
            this._channelsExtSrv.isContentsInProgress$(),
            this._entitiesService.isEntityProfilesLoading$()
        ]);

        this.languageList$ = this._channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                tap(channel => this._channelId = channel.id),
                map(channel => channel.languages.selected),
                filter(languages => !!languages),
                tap(languages => {
                    this._selectedLanguage.next(languages[0]);
                }),
                shareReplay(1)
            );

        this._channelsService.getChannel$()
            .pipe(filter(channel => !!channel))
            .subscribe(channel => this._entitiesService.loadEntityProfiles(channel.entity.id));

        this.literals$ = combineLatest([
            this._channelsExtSrv.getContents$().pipe(filter(contents => !!contents)),
            this._entitiesService.getEntityProfiles$().pipe(filter(profiles => !!profiles))
        ]).pipe(
            tap(([contents, entityProfiles]) => {
                this.form.setControl('literalValues', this._fb.group({}));
                // eslint-disable-next-line @typescript-eslint/naming-convention
                contents.forEach(({ id, type, value, profiled_content }) => {
                    if (type === ChannelContentType.profiledTemplate) {
                        const profiledTextsMap: Record<string, any> = {};
                        if (entityProfiles) {
                            //Find the profiled_content with entityProfile id to get the literal value. If not found, not value
                            entityProfiles.forEach(entityProfile => {
                                //Profiled_content may not exist
                                const profiledText = profiled_content?.find(profiledElement => profiledElement.id === entityProfile.id);
                                profiledTextsMap[entityProfile.id] = profiledText?.value;
                            });
                        }
                        this._formLiteralValues.addControl(String(id), this._fb.group(profiledTextsMap));
                    } else {
                        this._formLiteralValues.addControl(String(id), this._fb.control(value));
                    }
                });
            }),
            map(([contents]) => contents),
            shareReplay(1)
        );

        this.nonTextLiterals$ = this.literals$
            .pipe(
                map(literals => literals.filter(({ type }) => type !== ChannelContentType.text)),
                shareReplay(1)
            );

        this._communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntil(this._onDestroy))
            .subscribe(() => this.loadContents(this._selectedLanguage.getValue()));

        this.entityProfiles$ = this._entitiesService.getEntityProfiles$().pipe(filter(profiles => !!profiles));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
        this._entitiesService.clearEntityProfiles();
    }

    filter = (q: string, { id, type, value }: ChannelContent): boolean =>
        type === ChannelContentType.text && (
            this._translate.instant('CHANNELS.BOXOFFICE_CONTENTS_IDS.' + id)?.toLowerCase().includes(q.toLowerCase()) ||
            value?.toLowerCase().includes(q.toLowerCase())
        );

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    async save(): Promise<void> {
        if (this.form.valid) {
            const currentLang = this._selectedLanguage.getValue();
            const literals = await firstValueFrom(this.literals$);
            const obs$: Observable<void>[] = [];
            const formLiteralsValues = this._formLiteralValues.value as Record<string, string | Record<string, string>>;
            const contents: ChannelContent[] = [];
            const profiledContents: Record<string, ChannelContent[]> = {};

            literals.forEach(literal => {
                if (formLiteralsValues[`${literal.id}`] != null && typeof formLiteralsValues[`${literal.id}`] === 'object') {
                    const profiledValues = formLiteralsValues[`${literal.id}`];
                    Object.entries(profiledValues).forEach(([id, value]) => {
                        if (value != null && literal.profiled_content?.find(c => c.id === Number(id))?.value !== value) {
                            profiledContents[literal.id] = profiledContents[literal.id] ?? [];
                            profiledContents[literal.id].push({ id: Number(id), language: currentLang, value });
                        }
                    });
                } else if (formLiteralsValues[`${literal.id}`] != null && literal.value !== formLiteralsValues[`${literal.id}`]) {
                    contents.push({ id: literal.id, value: formLiteralsValues[String(literal.id)] as string });
                }
            });

            if (contents.length) {
                obs$.push(this._channelsExtSrv.updateContents(this._channelId, this._literalsSubpath, contents, currentLang));
            }
            if (Object.keys(profiledContents).length) {
                Object.keys(profiledContents).forEach(contentId => {
                    obs$.push(this._channelsExtSrv.updateProfiledContents(
                        this._channelId, contentId, profiledContents[contentId]
                    ));
                });
            }
            if (!obs$.length) {
                obs$.push(of<void>(undefined));
            }

            forkJoin(obs$).subscribe(() => {
                this.loadContents(currentLang);
                this._ephemeralSrv.showSaveSuccess();
            });
        }
    }

    cancel(): void {
        this.loadContents(this._selectedLanguage.getValue());
    }

    changeLanguage(newLanguage: string): void {
        this.loadContents(newLanguage);
        this._selectedLanguage.next(newLanguage);
    }

    private loadContents(lang: string): void {
        this._channelsExtSrv.loadContents(this._channelId, this._literalsSubpath, lang);
        this.form.markAsPristine();
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this._messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
