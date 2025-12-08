import { SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import { TicketAllocationType } from '@admin-clients/shared/common/data-access';
import { DestroyRef, Injectable, inject } from '@angular/core';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';
import { Observable } from 'rxjs';
import { take, tap } from 'rxjs/operators';
import { PutSeasonTicketRenewalsResponse } from './models/put-season-ticket-renewals-response.model';
import { PutSeasonTicketRenewals, PutSeasonTicketRenewalsItem } from './models/put-season-ticket-renewals.model';
import { SeasonTicketRenewalMappingStatus } from './models/season-ticket-renewal-mapping-status.enum';
import { VmSeasonTicketRenewalEdit } from './models/vm-season-ticket-renewal-edit.model';
import { VmSeasonTicketRenewal } from './models/vm-season-ticket-renewal.model';
import { SeasonTicketRenewalsService } from './season-ticket-renewals.service';
import { getLocationInfo } from './season-ticket-renewals.utils';
import { SeasonTicketRenewalsListState } from './state/season-ticket-renewals-list.state';

@Injectable()
export class SeasonTicketRenewalsSaveService {
    readonly #seasonTicketSrv = inject(SeasonTicketsService);
    readonly #seasonTicketRenewalsSrv = inject(SeasonTicketRenewalsService);
    readonly #listState = inject(SeasonTicketRenewalsListState);
    readonly #destroyRef = inject(DestroyRef);

    readonly #$seasonTicket = toSignal(this.#seasonTicketSrv.seasonTicket.get$().pipe(take(1)));
    readonly #$renewals = toSignal(this.#listState.getRenewalsList$().pipe(takeUntilDestroyed(this.#destroyRef)));

    saveRenewalEdits(renewalEdits: VmSeasonTicketRenewalEdit[]): Observable<PutSeasonTicketRenewalsResponse> {
        const putRenewals = this.#getPutRenewals(renewalEdits);
        return this.#seasonTicketRenewalsSrv.renewalEdits
            .save(this.#$seasonTicket().id, putRenewals)
            .pipe(tap(putRenewalsResponse => this.#successSaveRenewalEditsHandler(renewalEdits, putRenewalsResponse)));
    }

    #getPutRenewals(renewalEdits: VmSeasonTicketRenewalEdit[]): PutSeasonTicketRenewals {
        return {
            items: renewalEdits.map(renewalEdit => {
                let putRenewalItem: PutSeasonTicketRenewalsItem = {
                    id: renewalEdit.id,
                    user_id: renewalEdit.user_id,
                    ...(renewalEdit.auto_renewal !== undefined && { auto_renewal: renewalEdit.auto_renewal })
                };

                putRenewalItem = this.#getPutRenewalItemWithSeatId(renewalEdit, putRenewalItem);
                putRenewalItem = this.#getPutRenewalItemWithRateId(renewalEdit, putRenewalItem);

                return putRenewalItem;
            })
        };
    }

    #getPutRenewalItemWithSeatId(renewalEdit: VmSeasonTicketRenewalEdit, putRenewalItem: PutSeasonTicketRenewalsItem):
        PutSeasonTicketRenewalsItem {
        if (this.#isSeatDirty(renewalEdit)) {
            return {
                ...putRenewalItem,
                seat_id: renewalEdit.assignedSeatId
            };
        }
        return putRenewalItem;
    }

    #isSeatDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        if (!!renewalEdit.actual_seat) {
            return Number.isInteger(renewalEdit.assignedSeatId) &&
                renewalEdit.assignedSeatId !== renewalEdit.actual_seat.seat_id;
        }
        return Number.isInteger(renewalEdit.assignedSeatId);
    }

    #getPutRenewalItemWithRateId(renewalEdit: VmSeasonTicketRenewalEdit, putRenewalItem: PutSeasonTicketRenewalsItem):
        PutSeasonTicketRenewalsItem {
        if (this.#isRateDirty(renewalEdit)) {
            return {
                ...putRenewalItem,
                rate_id: renewalEdit.assignedRateId
            };
        }
        return putRenewalItem;
    }

    #isRateDirty(renewalEdit: VmSeasonTicketRenewalEdit): boolean {
        return renewalEdit.assignedRate && (renewalEdit.assignedRate !== renewalEdit.actual_rate);
    }

    #successSaveRenewalEditsHandler(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        putRenewalsResponse: PutSeasonTicketRenewalsResponse
    ): void {
        const successfulRenewalEdits = this.#getSuccessfulRenewalEdits(renewalEdits, putRenewalsResponse);
        this.#conciliateRenewalsWithSuccessfulRenewalEdits(successfulRenewalEdits);
    }

    #getSuccessfulRenewalEdits(
        renewalEdits: VmSeasonTicketRenewalEdit[],
        putRenewalsResponse: PutSeasonTicketRenewalsResponse
    ): Record<string, VmSeasonTicketRenewalEdit> {
        return renewalEdits
            .reduce<Record<string, VmSeasonTicketRenewalEdit>>(
                (accRenewalEditedFound,
                    renewalEdit) => {
                    const renewalEditedFound = putRenewalsResponse.items.find(renewalResponse =>
                        renewalResponse.result && renewalResponse.id === renewalEdit.id);
                    if (renewalEditedFound) {
                        accRenewalEditedFound[renewalEditedFound.id] = renewalEdit;
                    }
                    return accRenewalEditedFound;
                }, {});
    }

    #conciliateRenewalsWithSuccessfulRenewalEdits(successfulRenewalEdits: Record<string, VmSeasonTicketRenewalEdit>): void {
        if (Object.keys(successfulRenewalEdits).length) {
            const renewals = this.#$renewals().map(renewal => {
                if (successfulRenewalEdits[renewal.id]) {
                    let newRenewal = this.#getRenewalWithActualSeatConciliation(renewal, successfulRenewalEdits[renewal.id]);
                    newRenewal = this.#getRenewalRateWithActualRateConciliation(newRenewal, successfulRenewalEdits[renewal.id]);
                    newRenewal = this.#getRenewalWithAutoRenewalConciliation(newRenewal, successfulRenewalEdits[renewal.id]);
                    return {
                        ...newRenewal,
                        isSelected: false
                    };
                } else {
                    return { ...renewal, isSelected: false };
                }
            });
            this.#listState.setRenewalsList(renewals);
        }
    }

    #getRenewalWithActualSeatConciliation(
        renewal: VmSeasonTicketRenewal,
        renewalEdit: VmSeasonTicketRenewalEdit
    ): VmSeasonTicketRenewal {
        if (Number.isInteger(renewalEdit.assignedSeatId) && Number.isInteger(renewalEdit.assignedRowId)) {
            const actualSeat = {
                sector: renewalEdit.assignedSector.sector_name,
                sector_id: renewalEdit.assignedSector.sector_id,
                row: renewalEdit.assignedRow.row_name,
                row_id: renewalEdit.assignedRow.row_id,
                seat: renewalEdit.assignedSeat.seat_name,
                seat_id: renewalEdit.assignedSeat.seat_id,
                seat_type: TicketAllocationType.numbered,
                price_zone: ''
            };
            const actualLocation = getLocationInfo(actualSeat);
            return {
                ...renewal,
                actual_seat: actualSeat,
                actualLocation,
                mapping_status: SeasonTicketRenewalMappingStatus.mapped
            };
        } else if (Number.isInteger(renewalEdit.assignedSeatId) && Number.isInteger(renewalEdit.assignedNnzId)) {
            const actualSeat = {
                sector: renewalEdit.assignedSector.sector_name,
                sector_id: renewalEdit.assignedSector.sector_id,
                not_numbered_zone: renewalEdit.assignedNnz.not_numbered_zone_name,
                not_numbered_zone_id: renewalEdit.assignedNnz.not_numbered_zone_id,
                seat: renewalEdit.assignedSeat.seat_name,
                seat_id: renewalEdit.assignedSeat.seat_id,
                seat_type: TicketAllocationType.notNumbered,
                price_zone: ''
            };
            const actualLocation = getLocationInfo(actualSeat);
            return {
                ...renewal,
                actual_seat: actualSeat,
                actualLocation,
                mapping_status: SeasonTicketRenewalMappingStatus.mapped
            };
        } else {
            return renewal;
        }
    }

    #getRenewalRateWithActualRateConciliation(
        renewal: VmSeasonTicketRenewal,
        renewalEdit: VmSeasonTicketRenewalEdit
    ): VmSeasonTicketRenewal {
        if (this.#isRateDirty(renewalEdit)) {
            return {
                ...renewal,
                actual_rate: renewalEdit.assignedRate
            };
        } else {
            return renewal;
        }
    }

    #getRenewalWithAutoRenewalConciliation(
        renewal: VmSeasonTicketRenewal,
        renewalEdit: VmSeasonTicketRenewalEdit
    ): VmSeasonTicketRenewal {
        if (renewalEdit.auto_renewal !== undefined) {
            return {
                ...renewal,
                auto_renewal: renewalEdit.auto_renewal
            };
        } else {
            return renewal;
        }
    }
}
