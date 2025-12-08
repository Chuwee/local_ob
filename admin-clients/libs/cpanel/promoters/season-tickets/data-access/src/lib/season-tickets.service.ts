import { getListData, getMetadata, mapMetadata, StateManager } from '@OneboxTM/utils-state';
import { FormsField } from '@admin-clients/cpanel/common/utils';
import {
    PutSeasonTicketLoyaltyPoint, PutSeasonTicketReleaseSeats, PutSeasonTicketTicketRedemption, PutSeasonTicketTransferSeats
} from '@admin-clients/cpanel/promoters/season-tickets/locality-management/data-access';
import { PutCustomerTypeAssignation, RateRestrictions } from '@admin-clients/cpanel/promoters/shared/data-access';
import { PresalePost, PresalePut } from '@admin-clients/cpanel/shared/data-access';
import { WsMsgStatus, WsSessionMsg } from '@admin-clients/shared/core/data-access';
import { ExportRequest, ExportResponse } from '@admin-clients/shared/data-access/models';
import { DestroyRef, inject, Injectable } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Observable, of } from 'rxjs';
import { catchError, finalize, map, withLatestFrom } from 'rxjs/operators';
import { SeasonTicketsApi } from './api/season-tickets.api';
import { GetSeasonTicketValidationsResponse } from './models/get-season-ticket-validations.model';
import { GetSeasonTicketsRequest } from './models/get-season-tickets-request.model';
import { PostSeasonTicketRate } from './models/post-season-ticket-rate.model';
import { PostSeasonTicket } from './models/post-season-ticket.model';
import { PutSeasonTicketChangeSeatPrice } from './models/put-season-ticket-change-seat-price.model';
import { PutSeasonTicketChangeSeats } from './models/put-season-ticket-change-seats.model';
import { PutSeasonTicketPrice } from './models/put-season-ticket-price.model';
import { PutSeasonTicketRate } from './models/put-season-ticket-rate.model';
import { PutSeasonTicketStatus } from './models/put-season-ticket-status.model';
import { PutSeasonTicket } from './models/put-season-ticket.model';
import { SeasonTicketPrice } from './models/season-ticket-price.model';
import { SeasonTicketRate } from './models/season-ticket-rate.model';
import { SeasonTicketReleaseSeatListRequest } from './models/season-ticket-release-seat-list-request.model';
import { SeasonTicketGenerationStatus } from './models/season-ticket-status.model';
import { SeasonTicketSurcharge } from './models/season-ticket-surcharge.model';
import { PutSeasonTicketTaxes } from './models/season-ticket-taxes.model';
import { SeasonTicketsState } from './state/season-tickets.state';

@Injectable({
    providedIn: 'root'
})
export class SeasonTicketsService {
    readonly #api = inject(SeasonTicketsApi);
    readonly #state = inject(SeasonTicketsState);

    readonly seasonTicketChangeSeat = Object.freeze({
        load: (seasonTicketId: number): void => StateManager.load(
            this.#state.seasonTicketChangeSeat,
            this.#api.getSeasonTicketChangeSeats(seasonTicketId)
        ),
        update: (seasonTicketId: number, changeSeats: PutSeasonTicketChangeSeats) =>
            StateManager.inProgress(
                this.#state.seasonTicketChangeSeat,
                this.#api.putSeasonTicketChangeSeats(seasonTicketId, changeSeats)
            ),
        get$: () => this.#state.seasonTicketChangeSeat.getValue$(),
        error$: () => this.#state.seasonTicketChangeSeat.getError$(),
        loading$: () => this.#state.seasonTicketChangeSeat.isInProgress$(),
        clear: () => this.#state.seasonTicketChangeSeat.setValue(null)
    });

    readonly seasonTicketChangeSeatPrices = Object.freeze({
        load: (seasonTicketId: number): void => StateManager.load(
            this.#state.seasonTicketChangeSeatsPrices,
            this.#api.getSeasonTicketChangeSeatPrices(seasonTicketId)
        ),
        update: (seasonTicketId: number, seasonTicketPrices: PutSeasonTicketChangeSeatPrice[]) =>
            StateManager.inProgress(
                this.#state.seasonTicketChangeSeatsPricesSave,
                this.#api.putSeasonTicketChangeSeatPrices(seasonTicketId, seasonTicketPrices)
            ),
        get$: () => this.#state.seasonTicketChangeSeatsPrices.getValue$(),
        error$: () => this.#state.seasonTicketChangeSeatsPrices.getError$(),
        loading$: () => this.#state.seasonTicketChangeSeatsPrices.isInProgress$(),
        clear: () => this.#state.seasonTicketChangeSeatsPrices.setValue(null)
    });

    readonly seasonTicket = Object.freeze({
        load: (seasonTicketId: string): void => StateManager.load(
            this.#state.seasonTicket,
            this.#api.getSeasonTicket(seasonTicketId)
        ),
        get$: () => this.#state.seasonTicket.getValue$(),
        inProgress$: () => this.#state.seasonTicket.isInProgress$(),
        error$: () => this.#state.seasonTicket.getError$(),
        clear: () => this.#state.seasonTicket.setValue(null),
        save: (seasonTicketId: string, putSeasonTicket: PutSeasonTicket) => StateManager.inProgress(
            this.#state.seasonTicket,
            this.#api.putSeasonTicket(seasonTicketId, putSeasonTicket)
        ),
        create: (seasonTicket: PostSeasonTicket) => StateManager.inProgress(
            this.#state.seasonTicket,
            this.#api.postSeasonTicket(seasonTicket)
        ),
        delete: (seasonTicketId: number) => StateManager.inProgress(
            this.#state.seasonTicket,
            this.#api.deleteSeasonTicket(seasonTicketId)
        )
    });

    readonly seasonTicketStatus = Object.freeze({
        load: (seasonTicketId: string): void => StateManager.load(
            this.#state.seasonTicketStatus,
            this.#api.getSeasonTicketStatus(seasonTicketId)
        ),
        get$: () => this.#state.seasonTicketStatus.getValue$(),
        isGenerationStatusReady$: () => this.#state.seasonTicketStatus.getValue$()
            .pipe(map(stStatus => !!stStatus?.status && stStatus?.generation_status === SeasonTicketGenerationStatus.ready)),
        isGenerationStatusInProgress$: () => this.#state.seasonTicketStatus.getValue$()
            .pipe(map(stStatus => !stStatus?.status && stStatus?.generation_status === SeasonTicketGenerationStatus.inProgress)),
        isGenerationStatusError$: () => this.#state.seasonTicketStatus.getValue$()
            .pipe(map(stStatus => !stStatus?.status && stStatus?.generation_status === SeasonTicketGenerationStatus.error)),
        inProgress$: () => this.#state.seasonTicketStatus.isInProgress$(),
        error$: () => this.#state.seasonTicketStatus.getError$(),
        clear: () => this.#state.seasonTicketStatus.setValue(null),
        save: (seasonTicketId: number, putSeasonTicketStatus: PutSeasonTicketStatus) => StateManager.inProgress(
            this.#state.seasonTicketStatus,
            this.#api.putSeasonTicketStatus(seasonTicketId, putSeasonTicketStatus)
        )
    });

    readonly seasonTicketReleaseSeat = Object.freeze({
        load: (seasonTicketId: number): void => StateManager.load(
            this.#state.seasonTicketReleaseSeat,
            this.#api.getSeasonTicketReleaseSeat(seasonTicketId)
        ),
        update: (seasonTicketId: number, releaseSeats: PutSeasonTicketReleaseSeats) =>
            StateManager.inProgress(
                this.#state.seasonTicketReleaseSeat,
                this.#api.putSeasonTicketReleaseSeat(seasonTicketId, releaseSeats)
            ),
        list: Object.freeze({
            load: (seasonTicketId: number, request: SeasonTicketReleaseSeatListRequest): void => StateManager.load(
                this.#state.seasonTicketReleaseSeatList,
                this.#api.getSeasonTicketReleaseSeatList(seasonTicketId, request).pipe(mapMetadata())
            ),
            getData$: () => this.#state.seasonTicketReleaseSeatList.getValue$().pipe(getListData()),
            getMetadata$: () => this.#state.seasonTicketReleaseSeatList.getValue$().pipe(mapMetadata(), getMetadata()),
            error$: () => this.#state.seasonTicketReleaseSeatList.getError$(),
            loading$: () => this.#state.seasonTicketReleaseSeatList.isInProgress$(),
            clear: () => this.#state.seasonTicketReleaseSeatList.setValue(null),
            export: (seasonTicketId: number, request: ExportRequest): Observable<ExportResponse> => StateManager.inProgress(
                this.#state.exportSeasonTicketReleaseSeatList,
                this.#api.exportSeasonTicketReleaseSeatList(seasonTicketId, request)
            )
        }),
        get$: () => this.#state.seasonTicketReleaseSeat.getValue$(),
        error$: () => this.#state.seasonTicketReleaseSeat.getError$(),
        loading$: () => this.#state.seasonTicketReleaseSeat.isInProgress$(),
        clear: () => this.#state.seasonTicketReleaseSeat.setValue(null)
    });

    readonly seasonTicketTransferSeat = Object.freeze({
        load: (seasonTicketId: number): void => StateManager.load(
            this.#state.seasonTicketTransferSeat,
            this.#api.getSeasonTicketTransferSeat(seasonTicketId)
        ),
        update: (seasonTicketId: number, transferSeats: PutSeasonTicketTransferSeats) =>
            StateManager.inProgress(
                this.#state.seasonTicketTransferSeat,
                this.#api.putSeasonTicketTransferSeat(seasonTicketId, transferSeats)
            ),
        get$: () => this.#state.seasonTicketTransferSeat.getValue$(),
        error$: () => this.#state.seasonTicketTransferSeat.getError$(),
        loading$: () => this.#state.seasonTicketTransferSeat.isInProgress$(),
        clear: () => this.#state.seasonTicketTransferSeat.setValue(null)
    });

    readonly ticketRedemption = Object.freeze({
        load: (seasonTicketId: number): void => StateManager.load(
            this.#state.seasonTicketTicketRedemption,
            this.#api.getSeasonTicketTicketRedemption(seasonTicketId)
        ),
        update: (seasonTicketId: number, ticketRedemption: PutSeasonTicketTicketRedemption) =>
            StateManager.inProgress(
                this.#state.seasonTicketTicketRedemption,
                this.#api.putSeasonTicketTicketRedemption(seasonTicketId, ticketRedemption)
            ),
        get$: () => this.#state.seasonTicketTicketRedemption.getValue$(),
        error$: () => this.#state.seasonTicketTicketRedemption.getError$(),
        loading$: () => this.#state.seasonTicketTicketRedemption.isInProgress$(),
        clear: () => this.#state.seasonTicketTicketRedemption.setValue(null)
    });

    readonly seasonTicketLoyaltyPoint = Object.freeze({
        load: (seasonTicketId: number): void => StateManager.load(
            this.#state.seasonTicketLoyaltyPoint,
            this.#api.getSeasonTicketLoyaltyPoint(seasonTicketId)
        ),
        update: (seasonTicketId: number, putSeasonTicketLoyaltyPoint: PutSeasonTicketLoyaltyPoint) =>
            StateManager.inProgress(
                this.#state.seasonTicketLoyaltyPoint,
                this.#api.putSeasonTicketLoyaltyPoint(seasonTicketId, putSeasonTicketLoyaltyPoint)
            ),
        get$: () => this.#state.seasonTicketLoyaltyPoint.getValue$(),
        error$: () => this.#state.seasonTicketLoyaltyPoint.getError$(),
        loading$: () => this.#state.seasonTicketLoyaltyPoint.isInProgress$(),
        clear: () => this.#state.seasonTicketLoyaltyPoint.setValue(null)
    });

    readonly seasonTicketList = Object.freeze({
        load: (request: GetSeasonTicketsRequest): void => StateManager.load(
            this.#state.seasonTicketList,
            this.#api.getSeasonTickets(request).pipe(mapMetadata())
        ),
        getData$: () => this.#state.seasonTicketList.getValue$().pipe(getListData()),
        getMetadata$: () => this.#state.seasonTicketList.getValue$().pipe(mapMetadata(), getMetadata()),
        clear: () => this.#state.seasonTicketList.setValue(null),
        loading$: () => this.#state.seasonTicketList.isInProgress$()
    });

    readonly seasonTicketPresales = Object.freeze({
        load: (seasonTicketId: number): void => StateManager.load(
            this.#state.seasonTicketPresales,
            this.#api.getSeasonTicketPresales(seasonTicketId)
        ),
        get$: () => this.#state.seasonTicketPresales.getValue$(),
        error$: () => this.#state.seasonTicketPresales.getError$(),
        loading$: () => this.#state.seasonTicketPresales.isInProgress$(),
        clear: () => this.#state.seasonTicketPresales.setValue(null),
        create: (seasonTicketId: number, body: PresalePost) => StateManager.inProgress(
            this.#state.seasonTicketPresales,
            this.#api.postSeasonTicketPresale(seasonTicketId, body)
        ),
        update: (seasonTicketId: number, presaleId: string, body: PresalePut) => StateManager.inProgress(
            this.#state.seasonTicketPresales,
            this.#api.putSeasonTicketPresale(seasonTicketId, presaleId, body)
        ),
        delete: (seasonTicketId: number, presaleId: string) => StateManager.inProgress(
            this.#state.seasonTicketPresales,
            this.#api.deleteSeasonTicketPresale(seasonTicketId, presaleId)
        )
    });

    readonly ratesRestrictions = Object.freeze({
        load: (seasonTicketId: number) => StateManager.load(
            this.#state.seasonTicketRateRestriction,
            this.#api.getSeasonTicketRatesRestrictions(seasonTicketId)
        ),
        get$: () => this.#state.seasonTicketRateRestriction.getValue$().pipe(map(res => res?.data || [])),
        inProgress$: () => this.#state.seasonTicketRateRestriction.isInProgress$(),
        update: (seasonTicketId: number, rateId: number, restrictions: Partial<RateRestrictions>) => StateManager.inProgress(
            this.#state.seasonTicketRateRestriction,
            this.#api.putSeasonTicketRateRestrictions(seasonTicketId, rateId, restrictions)
        ),
        delete: (seasonTicketId: number, rateId: number) => StateManager.inProgress(
            this.#state.seasonTicketRateRestriction,
            this.#api.deleteSeasonTicketRateRestrictions(seasonTicketId, rateId)
        ),
        clear: () => this.#state.seasonTicketRateRestriction.setValue(null),
        error$: () => this.#state.seasonTicketRateRestriction.getError$()
    });

    readonly customerTypesAssignation = Object.freeze({
        load: (seasonTicketId: number) => StateManager.load(
            this.#state.seasonTicketCustomerTypesAssignation,
            this.#api.getSeasonTicketCustomerTypeAssignation(seasonTicketId)
        ),
        get$: () => this.#state.seasonTicketCustomerTypesAssignation.getValue$(),
        inProgress$: () => this.#state.seasonTicketCustomerTypesAssignation.isInProgress$(),
        update: (seasonTicketId: number, assignations: PutCustomerTypeAssignation[]) => StateManager.inProgress(
            this.#state.seasonTicketCustomerTypesAssignation,
            this.#api.putSeasonTicketCustomerTypeAssignation(seasonTicketId, assignations)
        ),
        clear: () => this.#state.seasonTicketCustomerTypesAssignation.setValue(null),
        error$: () => this.#state.seasonTicketCustomerTypesAssignation.getError$()
    });

    readonly seasonTicketForms = Object.freeze({
        load: (seasonTicketId: string, formType: string) => StateManager.load(
            this.#state.seasonTicketForms,
            this.#api.getSeasonTicketForms(seasonTicketId, formType)
        ),
        update: (seasonTicketId: string, formType: string, formsData: FormsField[][]) => StateManager.inProgress(
            this.#state.seasonTicketForms,
            this.#api.updateSeasonTicketForms(seasonTicketId, formType, formsData)
        ),
        get$: () => this.#state.seasonTicketForms.getValue$(),
        error$: () => this.#state.seasonTicketForms.getError$(),
        inProgress$: () => this.#state.seasonTicketForms.isInProgress$(),
        clear: () => this.#state.seasonTicketForms.setValue(null)
    });

    readonly seasonTicketTaxes = Object.freeze({
        load: (seasonTicketId: number): void => StateManager.load(
            this.#state.seasonTicketTaxes,
            this.#api.getSeasonTicketTaxes(seasonTicketId)
        ),
        update: (seasonTicketId: number, seasonTicketTaxes: PutSeasonTicketTaxes) =>
            StateManager.inProgress(
                this.#state.seasonTicketTaxes,
                this.#api.putSeasonTicketTaxes(seasonTicketId, seasonTicketTaxes)
            ),
        get$: () => this.#state.seasonTicketTaxes.getValue$(),
        error$: () => this.#state.seasonTicketTaxes.getError$(),
        inProgress$: () => this.#state.seasonTicketTaxes.isInProgress$()
    });

    loadSeasonTicketRates(seasonTicketId: string): void {
        this.#state.setSeasonTicketRatesError(null);
        this.#state.setSeasonTicketRatesInProgress(true);
        this.#api.getSeasonTicketRates(seasonTicketId)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketRatesError(error);
                    return of(null);
                }),
                finalize(() => this.#state.setSeasonTicketRatesInProgress(false))
            ).subscribe((seasonTicketRates: SeasonTicketRate[]) =>
                this.#state.setSeasonTicketRates(seasonTicketRates));
    }

    clearSeasonTicketRates(): void {
        this.#state.setSeasonTicketRates(null);
    }

    getSeasonTicketRates$(): Observable<SeasonTicketRate[]> {
        return this.#state.getSeasonTicketRates$();
    }

    isSeasonTicketRatesInProgress$(): Observable<boolean> {
        return this.#state.isSeasonTicketRatesInProgress$();
    }

    saveSeasonTicketRates(seasonTicketId: string, seasonTicketRates: PutSeasonTicketRate[]): Observable<void> {
        this.#state.setSeasonTicketRatesInProgress(true);
        return this.#api.putSeasonTicketRates(seasonTicketId, seasonTicketRates)
            .pipe(
                finalize(() => this.#state.setSeasonTicketRatesInProgress(false))
            );
    }

    createSeasonTicketRate(seasonTicketId: string, postSeasonTicketRate: PostSeasonTicketRate): Observable<{ id: number; name: string }> {
        this.#state.setSeasonTicketRatesInProgress(true);
        return this.#api.postSeasonTicketRate(seasonTicketId, postSeasonTicketRate)
            .pipe(
                map((result: { id: number }) => ({
                    id: result.id,
                    name: postSeasonTicketRate.name
                })),
                finalize(() => this.#state.setSeasonTicketRatesInProgress(false))
            );
    }

    deleteSeasonTicketRate(seasonTicketId: string, rateId: number): Observable<void> {
        this.#state.setSeasonTicketRatesInProgress(true);
        return this.#api.deleteSeasonTicketRate(seasonTicketId, rateId).pipe(
            finalize(() => this.#state.setSeasonTicketRatesInProgress(false))
        );
    }

    loadSeasonTicketPrices(seasonTicketId: number): void {
        this.#state.setSeasonTicketPricesError(null);
        this.#state.setSeasonTicketPricesInProgress(true);
        this.#api.getSeasonTicketPrices(seasonTicketId)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketPricesError(error);
                    return of(null);
                }),
                finalize(() => this.#state.setSeasonTicketPricesInProgress(false))
            )
            .subscribe((seasonTicketPrices: SeasonTicketPrice[]) =>
                this.#state.setSeasonTicketPrices(seasonTicketPrices)
            );
    }

    clearSeasonTicketPrices(): void {
        this.#state.setSeasonTicketPrices(null);
    }

    getSeasonTicketPrices$(): Observable<SeasonTicketPrice[]> {
        return this.#state.getSeasonTicketPrices$();
    }

    isSeasonTicketPricesInProgress$(): Observable<boolean> {
        return this.#state.isSeasonTicketPricesInProgress$();
    }

    saveSeasonTicketPrices(seasonTicketId: number, seasonTicketPrices: PutSeasonTicketPrice[]): Observable<void> {
        this.#state.setSeasonTicketPricesSaveInProgress(true);
        return this.#api.putEventPrices(seasonTicketId, seasonTicketPrices)
            .pipe(finalize(() => this.#state.setSeasonTicketPricesSaveInProgress(false)));
    }

    isSeasonTicketPricesSavingInProgress$(): Observable<boolean> {
        return this.#state.isSeasonTicketPricesSaveInProgress$();
    }

    loadSeasonTicketSurcharges(seasonTicketId: number): void {
        this.#state.setSeasonTicketSurchargesError(null);
        this.#state.setSeasonTicketSurchargesLoading(true);
        this.#api.getSeasonTicketSurcharges(seasonTicketId)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketSurchargesError(error);
                    return of(null);
                }),
                finalize(() => this.#state.setSeasonTicketSurchargesLoading(false))
            )
            .subscribe(surcharges =>
                this.#state.setSeasonTicketSurcharges(surcharges)
            );
    }

    getSeasonTicketSurcharges$(): Observable<SeasonTicketSurcharge[]> {
        return this.#state.getSeasonTicketSurcharges$();
    }

    isSeasonTicketSurchargesLoading$(): Observable<boolean> {
        return this.#state.isSeasonTicketSurchargesLoading$();
    }

    clearSeasonTicketSurcharges(): void {
        this.#state.setSeasonTicketSurcharges(null);
    }

    isSeasonTicketSurchargesSaving$(): Observable<boolean> {
        return this.#state.isSeasonTicketSurchargesSaveInProgress$();
    }

    saveSeasonTicketSurcharges(seasonTicketId: string, surcharges: SeasonTicketSurcharge[]): Observable<void> {
        this.#state.setSeasonTicketSurchargesError(null);
        this.#state.setSeasonTicketSurchargesSaveInProgress(true);
        return this.#api.postSeasonTicketSurcharges(seasonTicketId, surcharges)
            .pipe(
                catchError(error => {
                    this.#state.setSeasonTicketSurchargesError(error);
                    throw error;
                }),
                finalize(() => this.#state.setSeasonTicketSurchargesSaveInProgress(false))
            );
    }

    loadSeasonTicketValidations(seasonTicketId: string, hasLinkableSeats?: boolean, hasAssignedSessions?: boolean,
        hasPendingRenewals?: boolean): void {
        this.#state.setSeasonTicketValidationsInProgress(true);
        this.#api.getSeasonTicketValidations(seasonTicketId, hasLinkableSeats, hasAssignedSessions, hasPendingRenewals)
            .pipe(finalize(() => this.#state.setSeasonTicketValidationsInProgress(false)))
            .subscribe(validationsResponse => this.#state.setSeasonTicketValidations(validationsResponse));
    }

    getSeasonTicketValidations$(): Observable<GetSeasonTicketValidationsResponse> {
        return this.#state.getSeasonTicketValidations$();
    }

    isSeasonTicketValidationsInProgress$(): Observable<boolean> {
        return this.#state.isSeasonTicketValidationsInProgress$();
    }

    clearSeasonTicketValidations(): void {
        this.#state.setSeasonTicketValidations(null);
    }

    setSeasonTicketUpdatingCapacityUpdater(sessionMessages$: Observable<WsSessionMsg>, stopFetching$: DestroyRef): void {
        sessionMessages$
            .pipe(
                withLatestFrom(this.#state.seasonTicket.getValue$()),
                takeUntilDestroyed(stopFetching$)
            )
            .subscribe(([msg, session]) => {
                if (session?.id === msg.data.id) {
                    session.updating_capacity = msg.status === WsMsgStatus.inProgress;
                    this.#state.seasonTicket.setValue(session);
                }
            });
    }

    refreshExternalAvailability(seasonTicketId: number): Observable<void> {
        this.#state.seasonTicketExternalAvailability.setInProgress(true);
        return this.#api.putExternalAvailability(seasonTicketId)
            .pipe(finalize(() => this.#state.seasonTicketExternalAvailability.setInProgress(false)));
    }

    isRefreshExternalAvailabilityInProgress$(): Observable<boolean> {
        return this.#state.seasonTicketExternalAvailability.isInProgress$();
    }
}
