import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import { AuthenticationService, UserRoles } from '@admin-clients/cpanel/core/data-access';
import {
    EventChannel, EventChannelRequestStatus, EventChannelsService, EventChannelsLoadCase, UpdateEventChannelsRequest,
    EventChannelToFavoriteRequest, getReleaseStatusIndicator, getSaleStatusIndicator
} from '@admin-clients/cpanel/promoters/events/channels/data-access';
import { EventCommunicationService } from '@admin-clients/cpanel/promoters/events/communication/data-access';
import { EventsService, Event } from '@admin-clients/cpanel/promoters/events/data-access';
import { ArchivedEventMgrComponent } from '@admin-clients/cpanel/promoters/events/feature';
import { EventSessionsService, SessionsFilterFields, SessionStatus, SessionType }
    from '@admin-clients/cpanel/promoters/events/sessions/data-access';
import { EntitiesBaseService, Entity, EventType } from '@admin-clients/shared/common/data-access';
import {
    MessageDialogService,
    EphemeralMessageService,
    MessageType,
    DialogSize,
    HelpButtonComponent, DateTimeModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import {
    dateIsAfter, dateIsBefore, dateIsSameOrAfter, dateIsSameOrBefore, dateTimeValidator
} from '@admin-clients/shared/utility/utils';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { AsyncPipe, NgClass } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component,
    DestroyRef, inject, OnDestroy, OnInit, QueryList, signal, ViewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    AbstractControl,
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormGroup,
    ValidationErrors,
    ValidatorFn,
    Validators
} from '@angular/forms';
import { MatExpansionPanel } from '@angular/material/expansion';
import { MatSelectChange } from '@angular/material/select';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, shareReplay, skip, switchMap, take, tap } from 'rxjs/operators';
import { EventChannelsStateMachine } from '../event-channels-state-machine';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        FormContainerComponent, MaterialModule, HelpButtonComponent, TranslatePipe,
        ReactiveFormsModule, DateTimeModule, ArchivedEventMgrComponent, AsyncPipe, NgClass
    ],
    selector: 'app-event-channel-general-data',
    templateUrl: './event-channel-general-data.component.html',
    styleUrls: ['./event-channel-general-data.component.scss'],
    animations: [
        trigger('expandQuotaSelection', [
            state('expanded', style({ height: '*' })),
            state('collapsed', style({ height: '0' })),
            transition('expanded <=> collapsed', [animate('0.1s')])
        ])
    ]
})
export class EventChannelGeneralDataComponent implements OnInit, OnDestroy, WritingComponent {
    readonly #destroyRef = inject(DestroyRef);
    readonly #onDestroy = new Subject<void>();
    readonly #ref = inject(ChangeDetectorRef);
    readonly #eventChannelsService = inject(EventChannelsService);
    readonly #eventService = inject(EventsService);
    readonly #entitiesService = inject(EntitiesBaseService);
    readonly #eventChannelSM = inject(EventChannelsStateMachine);
    readonly #messageDialogService = inject(MessageDialogService);
    readonly #translate = inject(TranslateService);
    readonly #fb = inject(UntypedFormBuilder);
    readonly #ephemeralSrv = inject(EphemeralMessageService);
    readonly #authSrv = inject(AuthenticationService);
    readonly #sessionsSrv = inject(EventSessionsService);
    readonly #eventCommunicationSrv = inject(EventCommunicationService);

    #eventChannelId: number;
    #eventId: number;
    #favoriteChannelClickedFromDetail = false;

    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    allowBooking: boolean;
    allowSecondaryMarketSale: boolean;
    eventChannel$: Observable<EventChannel>;
    archivedEvent$: Observable<boolean>;
    favoriteChannelClickedFromDetail$: Observable<boolean>;
    isNotOperatorUser$: Observable<boolean>;
    form: UntypedFormGroup;
    eventChannelRequestStatus = EventChannelRequestStatus;
    activityEvent: boolean;

    getReleaseStatusIndicator = getReleaseStatusIndicator;
    getSaleStatusIndicator = getSaleStatusIndicator;

    readonly $isWhiteLabelExternal = signal<boolean>(false);

    ngOnInit(): void {
        this.#initForms();
        this.archivedEvent$ = this.#eventService.event.get$()
            .pipe(map(event => event.archived));
        this.eventChannel$ = combineLatest([
            this.#eventChannelsService.eventChannel.get$().pipe(filter(Boolean)),
            this.#eventService.event.get$(),
            this.#entitiesService.getEntity$().pipe(filter(Boolean))
        ])
            .pipe(
                tap(([eventChannel, event, entity]) => {
                    eventChannel = this.#addParsedQuotas(eventChannel);

                    this.#eventChannelId = eventChannel.channel.id;
                    this.#eventId = event.id;
                    this.activityEvent = event.type === EventType.activity || event.type === EventType.themePark;
                    this.allowBooking = this.isBookingAllowed(event, eventChannel.channel.type);
                    this.allowSecondaryMarketSale = this.isSecondaryMarketAllowed(entity, eventChannel);
                    this.$isWhiteLabelExternal.set(eventChannel.channel?.whitelabel_type === 'EXTERNAL');
                    // TODO: Uncoment when event have all properties
                    // this.eventDatesForm = this.formBuildEvent(event);
                    this.#formUpdateEventChannel(eventChannel);
                    this.#updateBookingValidators();
                    this.#updateSecondaryMarketSaleValidators();
                    this.#formUpdateQuota(eventChannel);

                    setTimeout(() => {
                        this.#ref.markForCheck();
                        this.form.markAsPristine();
                        this.form.markAllAsTouched();
                    });
                }),
                map(([eventChannel]) => eventChannel)
            );
        this.isNotOperatorUser$ = this.#authSrv.getLoggedUser$()
            .pipe(
                first(user => !!user),
                map(user => !(AuthenticationService.isSomeRoleInUserRoles(user, [UserRoles.OPR_MGR]))),
                shareReplay(1)
            );
        //If isSaving and favorite button has been clicked
        this.favoriteChannelClickedFromDetail$ = this.#eventChannelsService.isChannelToFavoriteSaving()
            .pipe(
                map(isSaving => {
                    const clickedFromDetail = this.#favoriteChannelClickedFromDetail;
                    this.#favoriteChannelClickedFromDetail = false;
                    return isSaving && clickedFromDetail;
                }),
                shareReplay(1)
            );
    }

    ngOnDestroy(): void {
        this.#onDestroy.next(null);
        this.#onDestroy.complete();
    }

    isBookingAllowed(event: Event, type: ChannelType): boolean {
        return event?.settings?.bookings?.enable
            && (ChannelType.boxOffice === type || ChannelType.webBoxOffice === type ||
                ChannelType.webB2B === type || ChannelType.external === type);
    }

    isSecondaryMarketAllowed(entity: Entity, eventChannel: EventChannel): boolean {
        return entity?.settings?.allow_secondary_market && eventChannel?.channel?.is_v4;
    }

    handleRequestChannel(eventChannel: EventChannel): void {
        if (!this.$isWhiteLabelExternal()) {
            this.requestChannel(eventChannel);
        } else {
            this.#handleWhiteLabelExternalRequest(eventChannel);
        }
    }

    requestChannel(eventChannel: EventChannel): void {
        const eventId = eventChannel.event.id;
        const channelId = eventChannel.channel.id;

        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            showCancelButton: true,
            title: this.#translate.instant('EVENTS.CHANNEL.REQUEST'),
            message: this.#translate.instant('EVENTS.CHANNEL.REQUEST_MSG',
                { channelName: eventChannel.channel.name })
        })
            .subscribe(action => {
                if (action) {
                    this.#eventChannelsService
                        .requestEventChannel(eventId, channelId)
                        .pipe(first())
                        .subscribe(() => {
                            this.#eventChannelSM.setCurrentState({
                                state: EventChannelsLoadCase.justLoadEventChannel,
                                idPath: this.#eventChannelId
                            });
                        });
                }
            });
    }

    checkPublishChange(checked: boolean): void {
        if (!checked) {
            this.#messageDialogService.showWarn({
                size: DialogSize.SMALL,
                showCancelButton: true,
                title: this.#translate.instant('TITLES.WARNING'),
                message: this.#translate.instant('EVENTS.CHANNEL.DISABLE_CHANNEL_PUBLISH')
            })
                .subscribe(action => {
                    if (!action) {
                        this.form.get('operativeForm.release.enabled').setValue(true);
                    } else {
                        this.form.get('operativeForm.sale.enabled').setValue(false);
                        this.form.get('operativeForm.booking.enabled').setValue(false);
                        this.form.get('operativeForm.secondary_market_sale.enabled').setValue(false);

                    }
                });
        }
    }

    checkUseEventChange(): void {
        this.#formUpdateUseEventDate();
    }

    checkSaleChange(checked: boolean): void {
        if (checked) {
            this.form.get('operativeForm.release.enabled').setValue(true);
        }
    }

    changeQuotaMap(templateId: number, quota: MatSelectChange): void {
        this.form.get('quotaForm').value.quotaMap[templateId] = quota.value;
        if (this.form.get('quotaForm').value.quotas.includes(templateId)) {
            this.form.markAsDirty();
        }
        const quotaMap = this.form.get('quotaForm').value.quotaMap;
        quotaMap[templateId] = quota.value;
        this.form.get('quotaForm').patchValue({ quotaMap });
    }

    cancel(): void {
        this.#eventChannelSM.setCurrentState({
            state: EventChannelsLoadCase.justLoadEventChannel,
            idPath: this.#eventChannelId
        });
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const operativeValues = this.form.value.operativeForm;
            if (!this.allowBooking) {
                operativeValues.booking = null;
            }
            if (!this.allowSecondaryMarketSale) {
                delete operativeValues.secondary_market_sale;
            }
            const eventChannel: UpdateEventChannelsRequest = {
                settings: { ...operativeValues },
                use_all_quotas: this.form.value.quotaForm.use_all_quotas,
                quotas: this.#getQuotas()
            };
            return this.#eventChannelsService.updateEventChannel(this.#eventId, this.#eventChannelId, eventChannel)
                .pipe(
                    tap(() => {
                        this.#ephemeralSrv.showSaveSuccess();
                        this.#eventChannelSM.setCurrentState({
                            state: EventChannelsLoadCase.justLoadEventChannel,
                            idPath: this.#eventChannelId
                        });
                    })
                );
        } else {
            this.form.markAllAsTouched();
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    updateFavoriteChannel(eventChannel: EventChannel): void {
        const eventId = eventChannel.event.id;
        const channelId = eventChannel.channel.id;
        const changeToFavorite: EventChannelToFavoriteRequest = {
            favorite: !eventChannel.channel.favorite
        };
        let finishedUpdatingFavoriteChannel$: Observable<void>;
        //Add favorite
        if (changeToFavorite.favorite) {
            this.#favoriteChannelClickedFromDetail = true;
            finishedUpdatingFavoriteChannel$ = this.#eventChannelsService.updateFavoriteChannel(eventId, channelId, changeToFavorite);
        } else {
            //Remove favorite
            finishedUpdatingFavoriteChannel$ = this.#messageDialogService.showWarn({
                size: DialogSize.SMALL,
                title: 'TITLES.WARNING',
                message: 'EVENTS.CHANNEL.REMOVE_FROM_FAVORITES_WARNING',
                actionLabel: 'FORMS.ACTIONS.OK'
            })
                .pipe(
                    switchMap(success => {
                        if (success) {
                            this.#favoriteChannelClickedFromDetail = true;
                            return this.#eventChannelsService.updateFavoriteChannel(eventId, channelId, changeToFavorite);
                        } else {
                            return throwError(() => null);
                        }
                    })
                );
        }
        finishedUpdatingFavoriteChannel$.subscribe(() => {
            this.#eventChannelsService.eventChannel.load(eventId, channelId);
            this.#eventChannelsService.eventChannelsList.load(eventId, {
                limit: 999,
                offset: 0,
                sort: 'name:asc'
            });
            this.#ephemeralSrv.show(
                {
                    type: changeToFavorite.favorite ? MessageType.info : MessageType.success,
                    msgKey: changeToFavorite.favorite ?
                        'EVENTS.CHANNEL.ADD_TO_FAVORITE_INFO' :
                        'EVENTS.CHANNEL.REMOVE_FROM_FAVORITES_SUCCESS'
                }
            );
        });
    }

    #initForms(): void {
        const fb = this.#fb;
        const operativeForm = fb.group({
            use_event_dates: [null],
            release: fb.group({
                enabled: [false],
                date: { value: null, disabled: true }
            }),
            sale: fb.group({
                enabled: [null],
                start_date: { value: null, disabled: true },
                end_date: { value: null, disabled: true }
            }),
            booking: fb.group({
                enabled: [null],
                start_date: { value: null, disabled: true },
                end_date: { value: null, disabled: true }
            }),
            secondary_market_sale: fb.group({
                enabled: [null],
                start_date: { value: null, disabled: true },
                end_date: { value: null, disabled: true }
            })
        });
        operativeForm.get('release.date').setValidators([
            Validators.required,
            // dateTimeValidator(
            //    dateIsSameOrAfter, 'releaseBeforeSeasonRelease', event.settings.operative.sales_period.channel_publishing_date
            //    this._translate.instant('EVENTS.CHANNEL.RELEASE').toLowerCase()
            // ),
            dateTimeValidator(
                dateIsSameOrBefore, 'releaseAfterSaleStart', operativeForm.get('sale.start_date'),
                this.#translate.instant('EVENTS.SESSION.SALE_START').toLowerCase()
            )
        ]);
        operativeForm.get('sale.start_date').setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsSameOrAfter, 'saleStartBeforeRelease', operativeForm.get('release.date'),
                this.#translate.instant('EVENTS.CHANNEL.RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'saleStartAfterSaleEnd', operativeForm.get('sale.end_date'),
                this.#translate.instant('EVENTS.SESSION.SALE_END').toLowerCase()
            )
        ]);
        operativeForm.get('sale.end_date').setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsAfter, 'saleEndAfterSaleStart', operativeForm.get('sale.start_date'),
                this.#translate.instant('EVENTS.SESSION.SALE_START').toLowerCase()
            )
        ]);

        const quotaForm = fb.group({
            use_all_quotas: [null, Validators.required],
            quotas: [null],
            quotaMap: [null]
        }, { validators: this.#getQuotaValidator() });

        this.form = fb.group({ operativeForm, quotaForm });
    }

    #addParsedQuotas(eventChannel: EventChannel): EventChannel {
        const quot = {};
        eventChannel.quotas?.forEach(quota => {
            if (!quot[quota.template_id]) {
                quot[quota.template_id] = {
                    template_id: quota.template_id,
                    quotas: [],
                    template_name: quota.template_name
                };
            }
            quot[quota.template_id].selected = quota.selected;
            quot[quota.template_id].quotas.push({ description: quota.description, id: quota.id });
            if (quota.selected) {
                quot[quota.template_id].quotaActive = quota.id;
            }
        });
        eventChannel.parsedQuotas = [];
        Object.keys(quot).forEach(templateId => {
            if (!quot[templateId].quotaActive) {
                quot[templateId].quotaActive = quot[templateId].quotas[0].id;
            }
            eventChannel.parsedQuotas.push(quot[templateId]);
        });
        return eventChannel;
    }

    #updateBookingValidators(): void {

        if (!this.allowBooking) {
            this.#removeBookingValidators();
            return;
        }

        this.#handleBookingValidators();

        const controlUseEventDates = this.form.get('operativeForm.use_event_dates');
        const controlEnableBooking = this.form.get('operativeForm.booking.enabled');
        controlUseEventDates.valueChanges
            .pipe(filter(() => this.allowBooking), takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.#handleBookingValidators);
        controlEnableBooking.valueChanges
            .pipe(filter(() => this.allowBooking), takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.#handleBookingValidators);
    }

    #handleBookingValidators: () => void = () => {
        const useEventDates = this.form.get('operativeForm.use_event_dates').value;
        const enableBooking = this.form.get('operativeForm.booking.enabled').value;
        if (useEventDates || !enableBooking) {
            this.#removeBookingValidators();
        } else {
            this.#addBookingValidators();
        }
    };

    #removeBookingValidators(): void {
        const controlStart = this.form.get('operativeForm.booking.start_date');
        const controlEnd = this.form.get('operativeForm.booking.end_date');
        controlStart.setValidators([]);
        controlEnd.setValidators([]);
        controlStart.setErrors(null);
        controlEnd.setErrors(null);
    }

    #addBookingValidators(): void {
        const controlStart = this.form.get('operativeForm.booking.start_date');
        const controlEnd = this.form.get('operativeForm.booking.end_date');

        controlStart.setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsSameOrAfter, 'bookingStartBeforeRelease', this.form.get('operativeForm.release.date'),
                this.#translate.instant('EVENTS.CHANNEL.RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'bookingStartAfterBookingEnd', controlEnd,
                this.#translate.instant('EVENTS.CHANNEL.BOOKING_END').toLowerCase()
            )
        ]);
        controlEnd.setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsAfter, 'bookingEndAfterBookingStart', controlStart,
                this.#translate.instant('EVENTS.CHANNEL.BOOKING_START').toLowerCase()
            )
        ]);
    }

    #updateSecondaryMarketSaleValidators(): void {
        if (!this.allowSecondaryMarketSale) {
            this.#removeSecondaryMarketSaleValidators();
            return;
        }

        this.#handleSecondaryMarketSaleValidators();

        const controlUseEventDates = this.form.get('operativeForm.use_event_dates');
        const controlEnableSecondaryMarketSale = this.form.get('operativeForm.secondary_market_sale.enabled');
        controlUseEventDates.valueChanges
            .pipe(filter(() => this.allowSecondaryMarketSale), takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.#handleSecondaryMarketSaleValidators);
        controlEnableSecondaryMarketSale.valueChanges
            .pipe(filter(() => this.allowSecondaryMarketSale), takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.#handleSecondaryMarketSaleValidators);
    }

    #handleSecondaryMarketSaleValidators: () => void = () => {
        const useEventDates = this.form.get('operativeForm.use_event_dates').value;
        const enableSecondaryMarketSale = this.form.get('operativeForm.secondary_market_sale.enabled').value;
        if (useEventDates || !enableSecondaryMarketSale) {
            this.#removeSecondaryMarketSaleValidators();
        } else {
            this.#addSecondaryMarketSaleValidators();
        }
    };

    #removeSecondaryMarketSaleValidators(): void {
        const controlStart = this.form.get('operativeForm.secondary_market_sale.start_date');
        const controlEnd = this.form.get('operativeForm.secondary_market_sale.end_date');
        controlStart.setValidators([]);
        controlEnd.setValidators([]);
        controlStart.setErrors(null);
        controlEnd.setErrors(null);
    }

    #addSecondaryMarketSaleValidators(): void {
        const controlStart = this.form.get('operativeForm.secondary_market_sale.start_date');
        const controlEnd = this.form.get('operativeForm.secondary_market_sale.end_date');
        controlStart.setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsSameOrAfter, 'secondaryMarketSaleStartDateBeforeReleaseDate', this.form.get('operativeForm.release.date'),
                this.#translate.instant('EVENTS.CHANNEL.RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'secondaryMarketSaleStartDateAfterSecondaryMarketSaleEndDate',
                this.form.get('operativeForm.secondary_market_sale.end_date'),
                this.#translate.instant('EVENTS.SESSION.SECONDARY_MARKET_SALE_END').toLowerCase()
            )
        ]);
        controlEnd.setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsAfter, 'secondaryMarketSaleEndDateBeforeSecondaryMarketSaleStartDate',
                this.form.get('operativeForm.secondary_market_sale.start_date'),
                this.#translate.instant('EVENTS.SESSION.SECONDARY_MARKET_SALE_START').toLowerCase()
            )
        ]);
    }

    #getQuotaValidator(): ValidatorFn {
        return (control: AbstractControl): ValidationErrors | null => {
            if (!control.get('use_all_quotas').value && !control.get('quotas').value?.length) {
                return { quotasListEmpty: true };
            }
            return null;
        };
    }

    // TODO: Uncoment when event have all properties
    // private formBuildEvent(event: Event): FormGroup {
    //     return this._fb.group({
    //         st_release_date: [{ value: event.settings.operative.sales_period.channel_publishing_date, disabled: true }],
    //         st_sale_start_date: [{ value: event.settings.operative.sales_period.sales_start_date, disabled: true }],
    //         st_sale_end_date: [{ value: event.settings.operative.sales_period.sales_ending_date, disabled: true }],
    //     });
    // }

    #formUpdateEventChannel(eventChannel: EventChannel): void {
        this.form.patchValue({
            operativeForm: {
                use_event_dates: eventChannel.settings.use_event_dates,
                secondary_market_sale: {
                    enabled: eventChannel.settings.secondary_market_sale.enabled,
                    start_date: eventChannel.settings.secondary_market_sale.start_date,
                    end_date: eventChannel.settings.secondary_market_sale.end_date
                },
                release: {
                    enabled: eventChannel.settings.release.enabled,
                    date: eventChannel.settings.release.date
                },
                sale: {
                    enabled: eventChannel.settings.sale.enabled,
                    start_date: eventChannel.settings.sale.start_date,
                    end_date: eventChannel.settings.sale.end_date
                },
                booking: {
                    enabled: eventChannel.settings.booking.enabled,
                    start_date: eventChannel.settings.booking.start_date,
                    end_date: eventChannel.settings.booking.end_date
                }
            }
        });
        this.#formUpdateUseEventDate();
    }

    #formUpdateUseEventDate(): void {
        const useEventDates = this.form.get('operativeForm.use_event_dates').value;
        if (!useEventDates) {
            this.form.get('operativeForm.release.date').enable();
            this.form.get('operativeForm.sale.start_date').enable();
            this.form.get('operativeForm.booking.start_date').enable();
            this.form.get('operativeForm.secondary_market_sale.start_date').enable();
            this.form.get('operativeForm.sale.end_date').enable();
            this.form.get('operativeForm.booking.end_date').enable();
            this.form.get('operativeForm.secondary_market_sale.end_date').enable();
        } else {
            this.form.get('operativeForm.release.date').disable();
            this.form.get('operativeForm.sale.start_date').disable();
            this.form.get('operativeForm.booking.start_date').disable();
            this.form.get('operativeForm.secondary_market_sale.start_date').disable();
            this.form.get('operativeForm.sale.end_date').disable();
            this.form.get('operativeForm.booking.end_date').disable();
            this.form.get('operativeForm.secondary_market_sale.end_date').disable();
        }
    }

    #formUpdateQuota(eventChannel: EventChannel): void {
        const quotaMap = {};
        eventChannel.parsedQuotas.forEach(quota => {
            quotaMap[quota.template_id] = quota.quotaActive;
        });
        const quotas = eventChannel.quotas.filter(quota => quota.selected)
            .map(quota => this.activityEvent ? quota.template_id : quota.id);
        this.form.get('quotaForm').patchValue({
            use_all_quotas: eventChannel.use_all_quotas,
            quotas,
            quotaMap
        }
        );
    }

    #getQuotas(): number[] {
        if (!this.form.value.quotaForm.use_all_quotas && this.form.value.quotaForm.quotas.length) {
            if (this.activityEvent) {
                return this.form.value.quotaForm.quotas.map(quotaId => this.form.value.quotaForm.quotaMap[quotaId]);
            } else {
                return [...new Set(this.form.value.quotaForm.quotas)] as [];
            }
        } else {
            return [];
        }
    }

    #handleWhiteLabelExternalRequest(eventChannel: EventChannel): void {
        const forceSquareImages = eventChannel.channel?.force_square_pictures ?? false;
        this.#loadSessionsAndImages(forceSquareImages);

        const hasSessionsReady$: Observable<boolean> = this.#sessionsSrv.getAllSessionsData$().pipe(
            map(sessions => sessions?.some(session => session?.status === SessionStatus.ready))
        );

        const hasImages$: Observable<boolean> = forceSquareImages ?
            this.#eventChannelsService.eventChannelSquareImages.get$().pipe(
                filter(Boolean), skip(1), take(1), map(images => !!images.length)
            ) :
            this.#eventCommunicationSrv.getEventChannelContentImages$().pipe(
                filter(Boolean), map(images => !!images?.length)
            );

        combineLatest([hasSessionsReady$, hasImages$])
            .pipe(filter(res => res.every(res => res !== undefined && res !== null)), take(1))
            .subscribe(([hasSessions, hasImages]) => {
                const warningMessage = this.#createWarningMessage(hasImages, hasSessions, forceSquareImages);
                if (!warningMessage) {
                    this.requestChannel(eventChannel);
                    return;
                }

                const isSquareImageMissing = warningMessage === 'SQUARE_IMAGE_MISSING';
                const title = isSquareImageMissing
                    ? 'EVENTS.CHANNEL.SQUARE_IMAGES.ALERT_TITLE_ERROR'
                    : 'EVENTS.CHANNEL.ERROR_TITLE';

                const message = isSquareImageMissing
                    ? 'EVENTS.CHANNEL.SQUARE_IMAGES.ALERT_MESSAGE_ERROR'
                    : `EVENTS.CHANNEL.ERROR_${warningMessage}`;

                this.#messageDialogService.showWarn({
                    size: DialogSize.SMALL,
                    showCancelButton: false,
                    title: this.#translate.instant(title),
                    message: this.#translate.instant(message),
                    actionLabel: 'ACTIONS.CONFIRM'
                });
            });
    }

    #createWarningMessage(hasImages: boolean, hasSessions: boolean, forceSquareImages: boolean): string {
        const rules = [
            { condition: forceSquareImages && !hasImages, text: 'SQUARE_IMAGE_MISSING' },
            { condition: !forceSquareImages && !hasImages && !hasSessions, text: 'DESCRIPTION' },
            { condition: !forceSquareImages && !hasImages, text: 'IMAGE_MISSING' },
            { condition: !hasSessions, text: 'SESSION_MISSING' }
        ];

        return rules.find(rule => rule.condition)?.text || '';
    }

    #loadSessionsAndImages(forceSquareImages: boolean): void {
        if (!forceSquareImages) {
            this.#eventCommunicationSrv.loadEventChannelContentImages(this.#eventId);
        } else {
            this.#eventChannelsService.eventChannelSquareImages.clear();
            this.#eventChannelsService.eventChannelSquareImages.load(this.#eventId, this.#eventChannelId);
        }

        this.#sessionsSrv.loadAllSessions(this.#eventId, {
            sort: `${SessionsFilterFields.startDate}:asc`,
            type: SessionType.session,
            fields: [SessionsFilterFields.status]
        });
    }

}
