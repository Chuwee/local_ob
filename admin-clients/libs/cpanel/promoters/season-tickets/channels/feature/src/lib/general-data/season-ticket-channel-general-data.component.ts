import { scrollIntoFirstInvalidFieldOrErrorMsg } from '@OneboxTM/feature-form-control-errors';
import { ChannelType } from '@admin-clients/cpanel/channels/data-access';
import {
    SeasonTicketChannelsLoadCase, UpdateSeasonTicketChannelsRequest, SeasonTicketChannelsService,
    SeasonTicketChannel
} from '@admin-clients/cpanel/promoters/season-tickets/channels/data-access';
import { SeasonTicket, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { EntitiesBaseService, Entity } from '@admin-clients/shared/common/data-access';
import {
    MessageDialogService,
    DialogSize,
    EphemeralMessageService,
    DateTimeModule
} from '@admin-clients/shared/common/ui/components';
import { MaterialModule } from '@admin-clients/shared/common/ui/ob-material';
import { WritingComponent } from '@admin-clients/shared/core/features';
import { FormContainerComponent } from '@admin-clients/shared/feature/form-container';
import {
    dateIsAfter, dateIsBefore, dateIsSameOrAfter, dateIsSameOrBefore, dateTimeValidator
} from '@admin-clients/shared/utility/utils';
import { CommonModule } from '@angular/common';
import {
    ChangeDetectionStrategy, ChangeDetectorRef, Component, DestroyRef, inject, OnDestroy, OnInit, QueryList, signal, ViewChild, ViewChildren
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
    ReactiveFormsModule,
    UntypedFormBuilder,
    UntypedFormControl,
    UntypedFormGroup,
    ValidationErrors,
    Validators
} from '@angular/forms';
import { MatAccordion, MatExpansionPanel } from '@angular/material/expansion';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { combineLatest, Observable, Subject, throwError } from 'rxjs';
import { filter, first, map, tap } from 'rxjs/operators';
import { getSaleStatusIndicator, getReleaseStatusIndicator } from '../models/season-sale-status-mapping-functions';
import { SeasonTicketChannelsStateMachine } from '../season-ticket-channel-state-machine';

@Component({
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [
        CommonModule,
        FormContainerComponent,
        TranslatePipe,
        MaterialModule,
        ReactiveFormsModule,
        DateTimeModule
    ],
    selector: 'app-season-ticket-channel-general-data',
    templateUrl: './season-ticket-channel-general-data.component.html',
    styleUrls: ['./season-ticket-channel-general-data.component.scss']
})

export class SeasonTicketChannelGeneralDataComponent implements OnInit, OnDestroy, WritingComponent {
    @ViewChildren(MatExpansionPanel)
    private _matExpansionPanelQueryList: QueryList<MatExpansionPanel>;

    @ViewChild(MatAccordion) private _accordion: MatAccordion;
    #formChanged = new Subject<void>();
    #seasonTicketChannelId: number;
    #seasonTicketId: number;
    #channelPublishingDate: string;

    #changeDetector = inject(ChangeDetectorRef);
    #entitiesService = inject(EntitiesBaseService);
    #seasonTicketChannelsService = inject(SeasonTicketChannelsService);
    #seasonTicketService = inject(SeasonTicketsService);
    #messageDialogService = inject(MessageDialogService);
    #translate = inject(TranslateService);
    #fb = inject(UntypedFormBuilder);
    #ephemeralMessageService = inject(EphemeralMessageService);
    #seasonTicketChannelSM = inject(SeasonTicketChannelsStateMachine);
    #destroyRef = inject(DestroyRef);
    $allowBooking = signal(true);
    $allowSecondaryMarketSale = signal(false);
    seasonTicketChannel$: Observable<SeasonTicketChannel>;
    form: UntypedFormGroup;
    seasonTicketDatesForm: UntypedFormGroup;
    getReleaseStatusIndicator = getReleaseStatusIndicator;
    getSaleStatusIndicator = getSaleStatusIndicator;

    get getUseSeasonTicketValue(): boolean {
        return this.form.get('operativeForm.use_season_ticket_dates').value;
    }

    ngOnInit(): void {
        this.initForms();
        this.seasonTicketChannel$ = combineLatest([
            this.#seasonTicketChannelsService.getSeasonTicketChannel$(),
            this.#seasonTicketService.seasonTicket.get$(),
            this.#entitiesService.getEntity$()
        ])
            .pipe(
                filter(([seasonTicketChannel, seasonTicket, _]: [SeasonTicketChannel, SeasonTicket, Entity]) => !!(
                    seasonTicketChannel !== null &&
                    seasonTicket !== null &&
                    seasonTicketChannel.channel &&
                    seasonTicketChannel.settings?.sale &&
                    seasonTicketChannel.settings.release &&
                    seasonTicketChannel.quotas &&
                    seasonTicketChannel.season_ticket &&
                    seasonTicket.settings?.operative?.sale
                )),
                map(([seasonTicketChannel, seasonTicket, entity]: [SeasonTicketChannel, SeasonTicket, Entity]) => {
                    this.#formChanged.next();
                    this.#seasonTicketChannelId = seasonTicketChannel.channel.id;
                    this.$allowBooking.set(this.#isBookingAllowed(seasonTicket, seasonTicketChannel.channel.type));
                    this.$allowSecondaryMarketSale.set(this.#isSecondaryMarketAllowed(entity, seasonTicket, seasonTicketChannel));
                    this.#seasonTicketId = seasonTicket.id;
                    this.#channelPublishingDate = seasonTicket.settings.operative.release.date;
                    this.#formUpdateSeasonTicket(seasonTicket);
                    this.#formUpdateSeasonTicketChannel(seasonTicketChannel);
                    this.#formUpdateQuota(seasonTicketChannel);
                    this.#updateBookingValidators();
                    this.#updateSecondaryMarketSaleValidators();
                    this.#changeDetector.markForCheck();
                    this.form.markAsPristine();
                    this.form.markAllAsTouched();
                    return seasonTicketChannel;
                })
            );
    }

    ngOnDestroy(): void {
        this.#formChanged.next();
        this.#formChanged.complete();
    }

    initForms(): void {
        const fb = this.#fb;
        const operativeForm = fb.group({
            use_season_ticket_dates: [null],
            release: fb.group({
                enabled: [false],
                date: [null, []]
            }),
            sale: fb.group({
                enabled: [null],
                start_date: [null],
                end_date: [null]
            }),
            booking: fb.group({
                enabled: [null],
                start_date: { value: null },
                end_date: { value: null }
            }),
            secondary_market_sale: fb.group({
                enabled: [null],
                start_date: { value: null },
                end_date: { value: null }
            })
        });
        operativeForm.get('release.date').setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsSameOrAfter, 'releaseBeforeSeasonRelease', this.#channelPublishingDate,
                this.#translate.instant('EVENTS.SESSION.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsSameOrBefore, 'releaseAfterSaleStart', operativeForm.get('sale.start_date'),
                this.#translate.instant('EVENTS.SESSION.SALE_START').toLowerCase()
            )
        ]);
        operativeForm.get('sale.start_date').setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsSameOrAfter, 'saleStartBeforeRelease', operativeForm.get('release.date'),
                this.#translate.instant('SEASON_TICKET.CHANNEL_RELEASE').toLowerCase()
            ),
            dateTimeValidator(
                dateIsBefore, 'saleStartAfterSaleEnd', operativeForm.get('sale.end_date'),
                this.#translate.instant('EVENTS.SESSION.SALE_END').toLowerCase()
            )
        ]);
        operativeForm.get('sale.end_date').setValidators([
            Validators.required,
            dateTimeValidator(
                dateIsAfter, 'saleEndBeforeSaleStart', operativeForm.get('sale.start_date'),
                this.#translate.instant('EVENTS.SESSION.SALE_START').toLowerCase()
            )
        ]);

        const quotaForm = fb.group({
            use_all_quotas: [null, Validators.required],
            quotas: fb.group({})
        }, { validators: this.#getQuotaValidator() });

        this.form = fb.group({ operativeForm, quotaForm });

        this.seasonTicketDatesForm = this.#fb.group({
            st_release_date: [{
                value: null,
                disabled: true
            }],
            st_sale_start_date: [{
                value: null,
                disabled: true
            }],
            st_sale_end_date: [{
                value: null,
                disabled: true
            }],
            st_booking_start_date: [{
                value: null,
                disabled: true
            }],
            st_booking_end_date: [{
                value: null,
                disabled: true
            }],
            st_secondary_market_sale_start_date: [{
                value: null,
                disabled: true
            }],
            st_secondary_market_sale_end_date: [{
                value: null,
                disabled: true
            }]
        });
    }

    requestChannel(seasonTicketChannel: SeasonTicketChannel): void {
        const seasonTicketId = seasonTicketChannel.season_ticket.id;
        const channelId = seasonTicketChannel.channel.id;

        this.#messageDialogService.showWarn({
            size: DialogSize.SMALL,
            showCancelButton: true,
            title: this.#translate.instant('SEASON_TICKET.CHANNELS_REQUEST'),
            message: this.#translate.instant('SEASON_TICKET.CHANNELS_REQUEST_MSG',
                { channelName: seasonTicketChannel.channel.name })
        })
            .subscribe(action => {
                if (action) {
                    this.#seasonTicketChannelsService
                        .requestSeasonTicketChannel(seasonTicketId, channelId)
                        .pipe(first())
                        .subscribe(() => {
                            this.#seasonTicketChannelSM.setCurrentState({
                                state: SeasonTicketChannelsLoadCase.justLoadSeasonTicketChannel,
                                idPath: channelId
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
                message: this.#translate.instant('SEASON_TICKET.DISABLE_CHANNEL_PUBLISH')
            })
                .subscribe(action => {
                    if (!action) {
                        this.form.get('operativeForm.release.enabled').setValue(true);
                    } else {
                        this.form.get('operativeForm.sale.enabled').setValue(false);
                        this.form.get('operativeForm.booking.enabled').setValue(false);
                    }
                });
        }
    }

    cancel(): void {
        this.#seasonTicketChannelSM.setCurrentState({
            state: SeasonTicketChannelsLoadCase.justLoadSeasonTicketChannel,
            idPath: this.#seasonTicketChannelId
        });
    }

    save(): void {
        this.save$().subscribe();
    }

    save$(): Observable<void> {
        if (this.form.valid) {
            const seasonTicketChannel: UpdateSeasonTicketChannelsRequest = {
                settings: { ...this.form.value.operativeForm },
                use_all_quotas: this.form.value.quotaForm.use_all_quotas,
                quotas: this.#getActiveQuotasIds()
            };
            return this.#seasonTicketChannelsService
                .updateSeasonTicketChannel(this.#seasonTicketId, this.#seasonTicketChannelId, seasonTicketChannel)
                .pipe(tap(() => {
                    this.#ephemeralMessageService.showSaveSuccess();
                    this.#seasonTicketChannelSM.setCurrentState({
                        state: SeasonTicketChannelsLoadCase.justLoadSeasonTicketChannel,
                        idPath: this.#seasonTicketChannelId
                    });
                }));
        } else {
            scrollIntoFirstInvalidFieldOrErrorMsg(document, this._matExpansionPanelQueryList);
            return throwError(() => 'invalid form');
        }
    }

    #getQuotaValidator() {
        return (fg: UntypedFormGroup): ValidationErrors | null => {
            if (fg.value.use_all_quotas) {
                return null;
            }
            const selected = Object.keys(fg.value.quotas)
                .filter(id => fg.value.quotas[id]);
            return selected.length >= 1 ? null : { quotas: true };
        };
    }

    #formUpdateSeasonTicketChannel(seasonTicketChannel: SeasonTicketChannel): void {
        this.form.patchValue({
            operativeForm: {
                use_season_ticket_dates: seasonTicketChannel.settings.use_season_ticket_dates,
                release: {
                    enabled: seasonTicketChannel.settings.release.enabled,
                    date: seasonTicketChannel.settings.release.date
                },
                sale: {
                    enabled: seasonTicketChannel.settings.sale.enabled,
                    start_date: seasonTicketChannel.settings.sale.start_date,
                    end_date: seasonTicketChannel.settings.sale.end_date
                },
                booking: {
                    enabled: seasonTicketChannel.settings.booking.enabled,
                    start_date: seasonTicketChannel.settings.booking?.start_date,
                    end_date: seasonTicketChannel.settings.booking?.end_date
                },
                secondary_market_sale: {
                    enabled: seasonTicketChannel.settings.secondary_market_sale.enabled,
                    start_date: seasonTicketChannel.settings.secondary_market_sale?.start_date,
                    end_date: seasonTicketChannel.settings.secondary_market_sale?.end_date
                }
            }
        });
    }

    #formUpdateSeasonTicket(seasonTicket: SeasonTicket): void {
        this.seasonTicketDatesForm.patchValue({
            st_release_date: seasonTicket.settings.operative.release.date,
            st_sale_start_date: seasonTicket.settings.operative.sale.start_date,
            st_sale_end_date: seasonTicket.settings.operative.sale.end_date,
            st_booking_start_date: seasonTicket.settings.operative.booking?.start_date,
            st_booking_end_date: seasonTicket.settings.operative.booking?.end_date,
            st_secondary_market_sale_start_date: seasonTicket.settings.operative.secondary_market_sale?.start_date,
            st_secondary_market_sale_end_date: seasonTicket.settings.operative.secondary_market_sale?.end_date
        });
    }

    #formUpdateQuota(seasonTicketChannel: SeasonTicketChannel): void {
        const quotaForm = this.form.get('quotaForm').get('quotas') as UntypedFormGroup;
        seasonTicketChannel.quotas.forEach(sg => quotaForm.removeControl(sg.id.toString()));
        seasonTicketChannel.quotas.forEach(sg => quotaForm.addControl(sg.id.toString(), new UntypedFormControl(sg.selected)));
        this.form.patchValue({
            quotaForm: {
                use_all_quotas: seasonTicketChannel.use_all_quotas
            }
        });
    }

    #getActiveQuotasIds(): number[] {
        return Object.keys(this.form.value.quotaForm.quotas)
            .filter(value => this.form.value.quotaForm.quotas[value])
            .map(value => +value);
    }

    #isBookingAllowed(seasonTicket: SeasonTicket, type: ChannelType): boolean {
        return seasonTicket?.settings?.bookings?.enable
            && (ChannelType.webB2B === type || ChannelType.webBoxOffice === type || ChannelType.boxOffice === type);
    }

    #updateBookingValidators(): void {

        if (!this.$allowBooking()) {
            this.#removeBookingValidators();
            return;
        }

        this.#handleBookingValidators();

        const controlUseSeasonTicketDates = this.form.get('operativeForm.use_season_ticket_dates');
        const controlEnableBooking = this.form.get('operativeForm.booking.enabled');
        controlUseSeasonTicketDates.valueChanges
            .pipe(filter(() => this.$allowBooking()), takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.#handleBookingValidators);
        controlEnableBooking.valueChanges
            .pipe(filter(() => this.$allowBooking()), takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.#handleBookingValidators);
    }

    #handleBookingValidators: () => void = () => {
        const useSeasonTicketDates = this.form.get('operativeForm.use_season_ticket_dates').value;
        const enableBooking = this.form.get('operativeForm.booking.enabled').value;

        if (useSeasonTicketDates || !enableBooking) {
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
        controlStart.updateValueAndValidity();
        controlEnd.updateValueAndValidity();
    }

    #isSecondaryMarketAllowed(entity: Entity, seasonTicket: SeasonTicket, seasonTicketChannel: SeasonTicketChannel): boolean {
        return entity?.settings?.allow_secondary_market && seasonTicket.entity.id === seasonTicketChannel.channel.entity.id
            && seasonTicketChannel.channel.is_v4 && seasonTicketChannel.channel.type === ChannelType.web;
    }

    #updateSecondaryMarketSaleValidators(): void {

        if (!this.$allowSecondaryMarketSale()) {
            this.#removeSecondaryMarketSaleValidators();
            return;
        }

        this.#handleSecondaryMarketSaleValidators();

        const controlUseSeasonTicketDates = this.form.get('operativeForm.use_season_ticket_dates');
        const controlEnableSecondaryMarketSale = this.form.get('operativeForm.secondary_market_sale.enabled');
        controlUseSeasonTicketDates.valueChanges
            .pipe(filter(() => this.$allowSecondaryMarketSale()), takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.#handleSecondaryMarketSaleValidators);
        controlEnableSecondaryMarketSale.valueChanges
            .pipe(filter(() => this.$allowSecondaryMarketSale()), takeUntilDestroyed(this.#destroyRef))
            .subscribe(this.#handleSecondaryMarketSaleValidators);
    }

    #handleSecondaryMarketSaleValidators: () => void = () => {
        const useSeasonTicketDates = this.form.get('operativeForm.use_season_ticket_dates').value;
        const enableSecondaryMarketSale = this.form.get('operativeForm.secondary_market_sale.enabled').value;
        if (useSeasonTicketDates || !enableSecondaryMarketSale) {
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
        controlStart.disable();
        controlEnd.disable();
    }

    #addSecondaryMarketSaleValidators(): void {
        const controlStart = this.form.get('operativeForm.secondary_market_sale.start_date');
        const controlEnd = this.form.get('operativeForm.secondary_market_sale.end_date');
        controlStart.enable();
        controlEnd.enable();
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
}
