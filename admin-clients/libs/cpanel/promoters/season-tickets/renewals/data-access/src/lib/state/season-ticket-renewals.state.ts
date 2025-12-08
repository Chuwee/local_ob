import { StateProperty } from '@OneboxTM/utils-state';
import { SeasonTicketRate } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { IdNameListResponse } from '@admin-clients/shared/data-access/models';
import { Injectable } from '@angular/core';
import { GetSeasonTicketExternalRenewalCandidateResponse } from '../models/get-season-ticket-external-renewal-candidate-response.model';
import { GetSeasonTicketRenewalCandidatesResponse } from '../models/get-season-ticket-renewal-candidates-response.model';
import { GetSeasonTicketRenewalsResponse } from '../models/get-season-ticket-renewals-response.model';
import { PutSeasonTicketRenewalsResponse } from '../models/put-season-ticket-renewals-response.model';
import { SeasonTicketRenewalAvailableSeat } from '../models/season-ticket-renewal-available-seat.model';
import { SeasonTicketRenewalCapacityTreeSector } from '../models/season-ticket-renewal-capacity-tree-sector.model';
import { SeasonTicketRenewalRate } from '../models/season-ticket-renewal-rate.model';

@Injectable()
export class SeasonTicketRenewalsState {
    readonly exportRenewals = new StateProperty<void>();
    readonly exportXmlSepa = new StateProperty<void>();
    // Season Ticket Renewal List
    readonly renewalsList = new StateProperty<GetSeasonTicketRenewalsResponse>();
    readonly automaticRenewalGeneration = new StateProperty<void>();
    readonly renewalsSubstatus = new StateProperty<PutSeasonTicketRenewalsResponse>();
    // Season Ticket Renewal Candidates
    readonly renewalCandidatesList = new StateProperty<GetSeasonTicketRenewalCandidatesResponse>();
    // Season Ticket External Renewal Candidates
    readonly externalRenewalCandidatesList = new StateProperty<GetSeasonTicketExternalRenewalCandidateResponse>();
    // Season Ticket Renewal Rates
    readonly renewalRates = new StateProperty<SeasonTicketRate[]>();
    // Season Ticket External Renewal Rates
    readonly externalRenewalRates = new StateProperty<SeasonTicketRenewalRate[]>();
    // Create Season Ticket Renewal
    readonly renewalsImport = new StateProperty<void>();
    // Season Ticket Renewal Entities
    readonly renewalsEntities = new StateProperty<IdNameListResponse>();
    // Season Ticket Renewals Capacity Tree
    readonly renewalsCapacityTree = new StateProperty<SeasonTicketRenewalCapacityTreeSector[]>();
    // Record of available Row seats
    readonly recordOfAvailableRowSeats = new StateProperty<Record<string, SeasonTicketRenewalAvailableSeat[]>>({});
    // Available Row seats from api in progress state
    readonly availableRowSeats = new StateProperty<void>();
    // Record of Nnz available seats
    readonly recordOfAvailableNnzSeats = new StateProperty<Record<string, SeasonTicketRenewalAvailableSeat[]>>({});
    // Available Nnz seats from api in progress state
    readonly availableNnzSeats = new StateProperty<void>();
    // Renewals Edits
    readonly renewalEdits = new StateProperty<void>();
}
