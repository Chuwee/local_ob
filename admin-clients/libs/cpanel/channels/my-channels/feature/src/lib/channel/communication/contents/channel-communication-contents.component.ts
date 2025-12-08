import { ChannelContent, ChannelContentType } from '@admin-clients/cpanel/channels/communication/data-access';
import { ChannelsService, ChannelsExtendedService } from '@admin-clients/cpanel/channels/data-access';
import { EntitiesBaseService } from '@admin-clients/shared/common/data-access';
import { MessageDialogService, EphemeralMessageService } from '@admin-clients/shared/common/ui/components';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { DateTimeFormats } from '@admin-clients/shared/data-access/models';
import { htmlContentMaxLengthValidator } from '@admin-clients/shared/utility/utils';
import { ChangeDetectionStrategy, Component, DestroyRef, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed, toObservable, toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormArray, UntypedFormBuilder, UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { filter, first, map, shareReplay, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { ChannelCommunicationNotifierService } from '../container/channel-communication-notifier.service';

const SECONDARY_MARKET_LEGAL_TEXT_ID = 83;

@Component({
    selector: 'app-channel-communication-contents',
    templateUrl: './channel-communication-contents.component.html',
    styleUrls: ['./channel-communication-contents.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class ChannelCommunicationContentsComponent implements OnInit, OnDestroy, WritingComponent {
    #channelId: number;
    #$selectedLanguage = signal<string>(null);
    #fb = inject(UntypedFormBuilder);
    #entitiesService = inject(EntitiesBaseService);
    #channelsService = inject(ChannelsService);
    #channelsExtSrv = inject(ChannelsExtendedService);
    #messageDialogService = inject(MessageDialogService);
    #ephemeralSrv = inject(EphemeralMessageService);
    #route = inject(ActivatedRoute);
    #communicationNotifierService = inject(ChannelCommunicationNotifierService);
    #destroyRef = inject(DestroyRef);

    get #formContentValues(): UntypedFormGroup {
        return this.form.get('contentValues') as UntypedFormGroup;
    }

    get #contentsSubpath(): string {
        return this.#route.snapshot.routeConfig.path?.split('/')[1];
    }

    readonly $entityHasSecondaryMarket = toSignal(this.#entitiesService.getEntity$()
        .pipe(map(entity => entity?.settings?.allow_secondary_market)));

    readonly SECONDARY_MARKET_LEGAL_TEXT_ID = 83;
    readonly V4_EXCLUDED_CONTENTS = [16, 17, 33, 52, 10, 11, 13, 28, 29, 30];

    form: UntypedFormGroup;
    selectedComContent: UntypedFormControl;
    isInProgress$: Observable<boolean>;
    languageList$: Observable<string[]>;
    selectedLanguage$ = toObservable(this.#$selectedLanguage);
    comContents$: Observable<ChannelContent[]>;
    channelContentTypes = ChannelContentType;
    comHistoricalContents$: Observable<{ creationDate?: string; value: string }[]>;
    selectedHistory: UntypedFormControl;
    dateTimeFormats = DateTimeFormats;

    ngOnInit(): void {
        this.form = this.#fb.group({
            contentValues: this.#fb.group({})
        });
        this.selectedComContent = this.#fb.control(null);
        this.selectedHistory = this.#fb.control(null);

        this.isInProgress$ = this.#channelsExtSrv.isContentsInProgress$();

        this.languageList$ = this.#channelsService.getChannel$()
            .pipe(
                first(channel => !!channel),
                tap(channel => this.#channelId = channel.id),
                map(channel => channel.languages.selected),
                filter(languages => !!languages),
                tap(languages => {
                    this.#$selectedLanguage.set(languages[0]);
                }),
                shareReplay(1)
            );

        this.comContents$ = this.selectedLanguage$
            .pipe(
                filter(lang => !!lang),
                switchMap(lang => {
                    this.loadContents(lang);
                    return this.#channelsExtSrv.getContents$();
                }),
                filter(contents => !!contents),
                withLatestFrom(this.#channelsService.getChannel$().pipe(map(channel => channel.settings.v4_enabled))),
                map(([contents, v4Enabled]) => {
                    this.form.setControl('contentValues', this.#fb.group({}));
                    // eslint-disable-set-line @typescript-eslint/naming-convention
                    contents.forEach(({ id, value, audited }) => {
                        if (audited) {
                            this.#formContentValues.addControl(String(id), this.#fb.array([value]));
                        } else {
                            this.#formContentValues.addControl(String(id), this.#fb.control(value));
                        }
                        if (id === 63) {
                            this.#formContentValues.controls[63].addValidators(htmlContentMaxLengthValidator(600));
                        }
                    });

                    const filteredContents = v4Enabled
                        ? contents.filter(content => !this.V4_EXCLUDED_CONTENTS.includes(content.id))
                        : contents;

                    const selectedContent = filteredContents
                        .find(content => content.id === this.selectedComContent.value?.id) || filteredContents[0];
                    this.selectedComContent.setValue(selectedContent);
                    this.form.markAsPristine();
                    return filteredContents;
                }),
                shareReplay(1)
            );

        this.comHistoricalContents$ = this.#channelsExtSrv.getHistoricalContent$()
            .pipe(
                filter(contents => !!contents),
                map(contents => {
                    const agg = contents.slice(); // deep copy
                    agg.push(this.selectedComContent.value); // agrego el value del content más actual
                    const result = agg.map((content, i, array) => ({
                        value: content.value,
                        creationDate: i !== 0 ? array[i - 1]?.creation_date : undefined
                    })); // shifteo las creation_date una posición
                    result.reverse(); // ordeno de más nuevo a más antiguo
                    this.selectedHistory.setValue(result[0]); // selecciono por defecto en valor actual de ese content

                    // añado formControls en el formArray por cada entrada historica del content seleccionado
                    const selectedHistoricalContent = this.selectedComContent.value as ChannelContent;
                    const contentFormArray = this.#formContentValues.get(String(selectedHistoricalContent.id)) as UntypedFormArray;
                    for (let i = 1; i < result.length; i++) {
                        contentFormArray.push(this.#fb.control({ value: result[i]?.value, disabled: true }));
                    }

                    return result;
                })
            );

        this.initFormChangesHandlers();

        this.#communicationNotifierService.getRefreshDataSignal$()
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe(() => this.loadContents(this.#$selectedLanguage()));
    }

    ngOnDestroy(): void {
        this.#channelsExtSrv.clearContents$();
    }

    canChangeLanguage: (() => Observable<boolean>) = () => this.validateIfCanChangeLanguage();

    save$(): Observable<void> {
        if (this.form.valid) {
            const currentLang = this.#$selectedLanguage();
            return this.comContents$
                .pipe(
                    first(),
                    switchMap(contents => {
                        const formLiteralValuesData = this.#formContentValues.value as Record<string, string | string[]>;
                        const data = contents.reduce<ChannelContent[]>((result, currentLiteral) => {
                            const isNotEmptyAndDistinct = formLiteralValuesData[String(currentLiteral.id)] != null &&
                                !Array.isArray(formLiteralValuesData[String(currentLiteral.id)]) &&
                                currentLiteral.value !== formLiteralValuesData[String(currentLiteral.id)];
                            const isNotEmptyAndDistinctFromArray = formLiteralValuesData[String(currentLiteral.id)] != null &&
                                Array.isArray(formLiteralValuesData[String(currentLiteral.id)]) &&
                                formLiteralValuesData[String(currentLiteral.id)][0] != null &&
                                currentLiteral.value !== formLiteralValuesData[String(currentLiteral.id)][0];

                            if (isNotEmptyAndDistinct || isNotEmptyAndDistinctFromArray) {
                                result.push({
                                    id: currentLiteral.id,
                                    value: isNotEmptyAndDistinct ?
                                        formLiteralValuesData[String(currentLiteral.id)] as string :
                                        formLiteralValuesData[String(currentLiteral.id)][0]
                                });
                            }

                            return result;
                        }, []);

                        if (data.length) {
                            return this.#channelsExtSrv.updateContents(this.#channelId, this.#contentsSubpath, data, currentLang);
                        } else {
                            return of<void>(undefined);
                        }
                    }),
                    tap(() => {
                        this.#ephemeralSrv.showSaveSuccess();
                    })
                );
        } else {
            return throwError(() => 'invalid form');
        }
    }

    save(): void {
        this.save$().subscribe(() => {
            this.loadContents(this.#$selectedLanguage());
        });
    }

    cancel(): void {
        this.loadContents(this.#$selectedLanguage());
    }

    changeLanguage(newLanguage: string): void {
        this.#$selectedLanguage.set(newLanguage);
    }

    private initFormChangesHandlers(): void {
        this.selectedComContent.valueChanges
            .pipe(takeUntilDestroyed(this.#destroyRef))
            .subscribe((content: ChannelContent) => {
                if (content?.audited) {
                    const currentLang = this.#$selectedLanguage();
                    this.#channelsExtSrv.loadHistoricalContent(this.#channelId, content.id, currentLang);
                }
            });
    }

    private loadContents(lang: string): void {
        this.#channelsExtSrv.loadContents(this.#channelId, this.#contentsSubpath, lang);
    }

    private validateIfCanChangeLanguage(): Observable<boolean> {
        if (this.form.dirty) {
            return this.#messageDialogService.defaultUnsavedChangesWarn();
        }
        return of(true);
    }
}
