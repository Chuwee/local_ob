import { StateProperty } from '@OneboxTM/utils-state';
import { ChannelCommission, ChannelSurcharge, ChannelPriceSimulation } from '@admin-clients/cpanel/channels/data-access';
import { ChannelB2bAssignations } from '@admin-clients/cpanel/promoters/shared/data-access';
import { BaseStateProp } from '@admin-clients/shared/utility/state';
import { Injectable } from '@angular/core';
import { GetSeasonTicketChannelsResponse } from '../models/get-season-ticket-channels-response.model';
import { SeasonTicketChannelLink } from '../models/season-ticket-channel-content.model';
import { SeasonTicketChannel } from '../models/season-ticket-channel.model';
import { SeasonTicketChannelsLoadCase } from '../models/season-ticket-channels-load.case';

@Injectable({
    providedIn: 'root'
})
export class SeasonTicketChannelsState {
    readonly channelSurcharges = new StateProperty<ChannelSurcharge[]>();

    // CHANNEL LIST
    readonly seasonTicketChannelList = new StateProperty<GetSeasonTicketChannelsResponse>();

    // CHANNEL
    private readonly _seasonTicketChannel =
        new BaseStateProp<SeasonTicketChannel>();

    readonly getSeasonTicketChannel$ =
        this._seasonTicketChannel.getValueFunction();

    readonly setSeasonTicketChannel =
        this._seasonTicketChannel.setValueFunction();

    readonly isSeasonTicketChannelInProgress$ =
        this._seasonTicketChannel.getInProgressFunction();

    readonly setSeasonTicketChannelInProgress =
        this._seasonTicketChannel.setInProgressFunction();

    readonly getSeasonTicketChannelError$ =
        this._seasonTicketChannel.getErrorFunction();

    readonly setSeasonTicketChannelError =
        this._seasonTicketChannel.setErrorFunction();

    // ListDetailState
    private readonly _listDetailState =
        new BaseStateProp<SeasonTicketChannelsLoadCase>(
            SeasonTicketChannelsLoadCase.none
        );

    readonly getListDetailState$ = this._listDetailState.getValueFunction();
    readonly setListDetailState = this._listDetailState.setValueFunction();

    // SEASON TICKET CHANNEL COMMISSIONS
    private _seasonTicketChannelCommissions = new BaseStateProp<
        ChannelCommission[]
    >();

    readonly setSeasonTicketChannelCommissions =
        this._seasonTicketChannelCommissions.setValueFunction();

    readonly getSeasonTicketChannelCommissions$ =
        this._seasonTicketChannelCommissions.getValueFunction();

    readonly setSeasonTicketChannelCommissionsLoading =
        this._seasonTicketChannelCommissions.setInProgressFunction();

    readonly isSeasonTicketChannelCommissionsLoading$ =
        this._seasonTicketChannelCommissions.getInProgressFunction();

    readonly setSeasonTicketChannelCommissionsError =
        this._seasonTicketChannelCommissions.setErrorFunction();

    readonly getSeasonTicketChannelCommissionsError$ =
        this._seasonTicketChannelCommissions.getErrorFunction();

    // SEASON TICKET CHANNEL CHANNEL SURCHARGES
    private readonly _seasonTicketChannelChannelSurcharges = new BaseStateProp<
        ChannelSurcharge[]
    >();

    readonly setSeasonTicketChannelChannelSurcharges =
        this._seasonTicketChannelChannelSurcharges.setValueFunction();

    readonly getSeasonTicketChannelChannelSurcharges$ =
        this._seasonTicketChannelChannelSurcharges.getValueFunction();

    readonly setSeasonTicketChannelChannelSurchargesLoading =
        this._seasonTicketChannelChannelSurcharges.setInProgressFunction();

    readonly isSeasonTicketChannelChannelSurchargesLoading$ =
        this._seasonTicketChannelChannelSurcharges.getInProgressFunction();

    readonly setSeasonTicketChannelChannelSurchargesError =
        this._seasonTicketChannelChannelSurcharges.setErrorFunction();

    readonly getSeasonTicketChannelChannelSurchargesError$ =
        this._seasonTicketChannelChannelSurcharges.getErrorFunction();

    // SEASON TICKET CHANNEL PRICE SIMULATION
    private readonly _seasonTicketChannelPriceSimulation = new BaseStateProp<
        ChannelPriceSimulation[]
    >();

    readonly setSeasonTicketChannelPriceSimulation =
        this._seasonTicketChannelPriceSimulation.setValueFunction();

    readonly getSeasonTicketChannelPriceSimulation$ =
        this._seasonTicketChannelPriceSimulation.getValueFunction();

    readonly setSeasonTicketChannelPriceSimulationLoading =
        this._seasonTicketChannelPriceSimulation.setInProgressFunction();

    readonly isSeasonTicketChannelPriceSimulationLoading$ =
        this._seasonTicketChannelPriceSimulation.getInProgressFunction();

    readonly setSeasonTicketChannelPriceSimulationError =
        this._seasonTicketChannelPriceSimulation.setErrorFunction();

    readonly getSeasonTicketChannelPriceSimulationError$ =
        this._seasonTicketChannelPriceSimulation.getErrorFunction();

    // SEASON TICKET CHANNEL PROMOTER SURCHARGES
    private readonly _seasonTicketChannelSurcharges = new BaseStateProp<
        ChannelSurcharge[]
    >();

    readonly setSeasonTicketChannelSurcharges =
        this._seasonTicketChannelSurcharges.setValueFunction();

    readonly getSeasonTicketChannelSurcharges$ =
        this._seasonTicketChannelSurcharges.getValueFunction();

    readonly setSeasonTicketChannelSurchargesLoading =
        this._seasonTicketChannelSurcharges.setInProgressFunction();

    readonly isSeasonTicketChannelSurchargesLoading$ =
        this._seasonTicketChannelSurcharges.getInProgressFunction();

    readonly setSeasonTicketChannelSurchargesError =
        this._seasonTicketChannelSurcharges.setErrorFunction();

    readonly getSeasonTicketChannelSurchargesError$ =
        this._seasonTicketChannelSurcharges.getErrorFunction();

    private readonly _seasonTicketChannelSurchargesSaving =
        new BaseStateProp<boolean>();

    readonly setSeasonTicketChannelSurchargesSaving =
        this._seasonTicketChannelSurchargesSaving.setInProgressFunction();

    readonly isSeasonTicketChannelSurchargesSaving$ =
        this._seasonTicketChannelSurchargesSaving.getInProgressFunction();

    // EVENT CHANNEL CONTENTS
    private _seasonTicketChannelLink =
        new BaseStateProp<SeasonTicketChannelLink>();

    readonly setSeasonTicketChannelLink =
        this._seasonTicketChannelLink.setValueFunction();

    readonly getSeasonTicketChannelLink$ =
        this._seasonTicketChannelLink.getValueFunction();

    readonly setSeasonTicketChannelLinkLoading =
        this._seasonTicketChannelLink.setInProgressFunction();

    readonly isSeasonTicketChannelLinkLoading$ =
        this._seasonTicketChannelLink.getInProgressFunction();

    readonly setSeasonTicketChannelLinkError =
        this._seasonTicketChannelLink.setErrorFunction();

    readonly getSeasonTicketChannelLinkError$ =
        this._seasonTicketChannelLink.getErrorFunction();

    private _ticketPdfPreviewDownloading = new BaseStateProp<boolean>();
    readonly setTicketPdfPreviewDownloading =
        this._ticketPdfPreviewDownloading.setInProgressFunction();

    readonly isTicketPdfPreviewDownloading$ =
        this._ticketPdfPreviewDownloading.getInProgressFunction();

    // B2B ASSIGNATIONS
    readonly b2bAssignations = new StateProperty<ChannelB2bAssignations>();
}
