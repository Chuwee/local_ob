import { ListResponse, StateProperty } from '@OneboxTM/utils-state';
import { FormsField } from '@admin-clients/cpanel/common/utils';
import {
    ReleasedList, SeasonTicketLoyaltyPointList, SeasonTicketReleaseSeats, SeasonTicketTicketRedemption, SeasonTicketTransferSeats
} from '@admin-clients/cpanel/promoters/season-tickets/locality-management/data-access';
import { CustomerTypeAssignation, RateRestrictions } from '@admin-clients/cpanel/promoters/shared/data-access';
import { Presale } from '@admin-clients/cpanel/shared/data-access';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetSeasonTicketValidationsResponse } from '../models/get-season-ticket-validations.model';
import { GetSeasonTicketsResponse } from '../models/get-season-tickets-response.model';
import { SeasonTicketChangeSeatPrice } from '../models/season-ticket-change-seat-price.model';
import { SeasonTicketChangeSeats } from '../models/season-ticket-change-seats.model';
import { SeasonTicketPrice } from '../models/season-ticket-price.model';
import { SeasonTicketRate } from '../models/season-ticket-rate.model';
import { GetSeasonTicketStatusResponse } from '../models/season-ticket-status.model';
import {
    SeasonTicketSurcharge
} from '../models/season-ticket-surcharge.model';
import { SeasonTicketTaxes } from '../models/season-ticket-taxes.model';
import { SeasonTicket } from '../models/season-ticket.model';

@Injectable({
    providedIn: 'root'
})
export class SeasonTicketsState {
    // Season Ticket list
    readonly seasonTicketList = new StateProperty<GetSeasonTicketsResponse>();

    // Season Ticket
    readonly seasonTicket = new StateProperty<SeasonTicket>();

    // Season Ticket Forms
    readonly seasonTicketForms = new StateProperty<FormsField[][]>();

    private readonly _seasonTicketSave = new BaseStateProp<void>();
    readonly isSeasonTicketSaveInProgress$ = this._seasonTicketSave.getInProgressFunction();
    readonly setSeasonTicketSaveInProgress = this._seasonTicketSave.setInProgressFunction();

    // Season Ticket Status
    readonly seasonTicketStatus = new StateProperty<GetSeasonTicketStatusResponse>();

    private readonly _seasonTicketStatusSave = new BaseStateProp<void>();
    readonly isSeasonTicketStatusSaveInProgress$ = this._seasonTicketStatusSave.getInProgressFunction();
    readonly setSeasonTicketStatusSaveInProgress = this._seasonTicketStatusSave.setInProgressFunction();

    // Season Ticket Rates
    private readonly _seasonTicketRates = new BaseStateProp<SeasonTicketRate[]>();
    readonly getSeasonTicketRates$ = this._seasonTicketRates.getValueFunction();
    readonly setSeasonTicketRates = this._seasonTicketRates.setValueFunction();
    readonly isSeasonTicketRatesInProgress$ = this._seasonTicketRates.getInProgressFunction();
    readonly setSeasonTicketRatesInProgress = this._seasonTicketRates.setInProgressFunction();
    readonly setSeasonTicketRatesError = this._seasonTicketRates.setErrorFunction();

    // Season Ticket Surcharges
    private readonly _seasonTicketSurcharges: BaseStateProp<SeasonTicketSurcharge[]> = new BaseStateProp<SeasonTicketSurcharge[]>();
    readonly setSeasonTicketSurcharges = this._seasonTicketSurcharges.setValueFunction();
    readonly getSeasonTicketSurcharges$ = this._seasonTicketSurcharges.getValueFunction();
    readonly setSeasonTicketSurchargesLoading = this._seasonTicketSurcharges.setInProgressFunction();
    readonly isSeasonTicketSurchargesLoading$ = this._seasonTicketSurcharges.getInProgressFunction();
    readonly setSeasonTicketSurchargesError = this._seasonTicketSurcharges.setErrorFunction();

    private readonly _seasonTicketSurchargesSave = new BaseStateProp<void>();
    readonly setSeasonTicketSurchargesSaveInProgress = this._seasonTicketSurchargesSave.setInProgressFunction();
    readonly isSeasonTicketSurchargesSaveInProgress$ = this._seasonTicketSurchargesSave.getInProgressFunction();

    // Season Ticket Prices
    private readonly _seasonTicketPrices = new BaseStateProp<SeasonTicketPrice[]>();
    readonly getSeasonTicketPrices$ = this._seasonTicketPrices.getValueFunction();
    readonly setSeasonTicketPrices = this._seasonTicketPrices.setValueFunction();
    readonly isSeasonTicketPricesInProgress$ = this._seasonTicketPrices.getInProgressFunction();
    readonly setSeasonTicketPricesInProgress = this._seasonTicketPrices.setInProgressFunction();
    readonly setSeasonTicketPricesError = this._seasonTicketPrices.setErrorFunction();

    private readonly _seasonTicketPricesSave = new BaseStateProp<void>();
    readonly isSeasonTicketPricesSaveInProgress$ = this._seasonTicketPricesSave.getInProgressFunction();
    readonly setSeasonTicketPricesSaveInProgress = this._seasonTicketPricesSave.setInProgressFunction();

    // Season Ticket Change Seats Prices
    readonly seasonTicketChangeSeatsPrices = new StateProperty<SeasonTicketChangeSeatPrice[]>();
    readonly seasonTicketChangeSeatsPricesSave = new StateProperty<void>();

    // Season ticket validations
    private readonly _seasonTicketValidations = new BaseStateProp<GetSeasonTicketValidationsResponse>();
    readonly getSeasonTicketValidations$ = this._seasonTicketValidations.getValueFunction();
    readonly setSeasonTicketValidations = this._seasonTicketValidations.setValueFunction();
    readonly isSeasonTicketValidationsInProgress$ = this._seasonTicketValidations.getInProgressFunction();
    readonly setSeasonTicketValidationsInProgress = this._seasonTicketValidations.setInProgressFunction();

    readonly seasonTicketChangeSeat = new StateProperty<SeasonTicketChangeSeats>();
    readonly seasonTicketReleaseSeat = new StateProperty<SeasonTicketReleaseSeats>();
    readonly seasonTicketTransferSeat = new StateProperty<SeasonTicketTransferSeats>();
    readonly seasonTicketReleaseSeatList = new StateProperty<ListResponse<ReleasedList>>();
    readonly seasonTicketLoyaltyPoint = new StateProperty<SeasonTicketLoyaltyPointList>();
    readonly exportSeasonTicketReleaseSeatList = new StateProperty<void>();
    readonly seasonTicketTicketRedemption = new StateProperty<SeasonTicketTicketRedemption>();
    readonly seasonTicketRateRestriction = new StateProperty<ListResponse<RateRestrictions>>();
    readonly seasonTicketCustomerTypesAssignation = new StateProperty<CustomerTypeAssignation[]>();

    // Season ticket presales
    readonly seasonTicketPresales = new StateProperty<Presale[]>();

    // Season ticket external availability
    readonly seasonTicketExternalAvailability = new StateProperty<void>();
    readonly seasonTicketTaxes = new StateProperty<SeasonTicketTaxes>();
}
