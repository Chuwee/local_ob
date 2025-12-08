import { Injectable, OnDestroy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { bufferCount, combineLatest, concat, map, Observable, Subject } from 'rxjs';
import { filter, finalize, first, mapTo, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { SeasonTicket, SeasonTicketsService } from '@admin-clients/cpanel/promoters/season-tickets/data-access';
import {
    SeasonTicketCapacityDialogComponent, SeasonTicketMgrService, VenueTemplateLabelGroupType
} from '@admin-clients/cpanel/venues/feature/standard-venue-tpl-manager';
import { ObMatDialogConfig } from '@admin-clients/shared/common/ui/components';
import { IdName, SeasonTicketLinkNNZResponse, SeasonTicketUnlinkNNZResponse } from '@admin-clients/shared/data-access/models';
import {
    NotNumberedZone, Seat, SeatLinkable, VenueMap, VenueMapService
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { SeasonTicketCapacityApi } from './api/season-ticket-capacity.api';
import { SeasonTicketLinkableSeat, SeasonTicketLinkableSeats } from './models/season-ticket-linkable-seats.model';
import { SeasonTicketCapacityState } from './state/season-ticket-capacity.state';

@Injectable()
export class SeasonTicketCapacityService implements SeasonTicketMgrService, VenueMapService, OnDestroy {
    private readonly _onDestroy = new Subject<void>();

    constructor(
        private _seasonTicketCapacityApi: SeasonTicketCapacityApi,
        private _seasonTicketCapacityState: SeasonTicketCapacityState,
        private _seasonTicketService: SeasonTicketsService,
        private _matDialog: MatDialog
    ) {
        combineLatest([
            this._seasonTicketCapacityState.getVenueTplMap$(),
            this._seasonTicketCapacityState.getLinkableSeats$()
        ])
            .pipe(takeUntil(this._onDestroy))
            .subscribe(([venueMap, linkableSeats]) => this.updateLinkableSeats(venueMap, linkableSeats));
    }

    ngOnDestroy(): void {
        this._onDestroy.next(null);
        this._onDestroy.complete();
    }

    getSeasonTicket$(): Observable<IdName> {
        return this._seasonTicketService.seasonTicket.get$();
    }

    // VENUE MAP
    loadVenueMap(): void {
        this._seasonTicketCapacityState.setVenueTplMapLoading(true);
        this._seasonTicketService.seasonTicket.get$()
            .pipe(first(value => value !== null))
            .subscribe(seasonTicket => {
                if (!seasonTicket.updating_capacity) {
                    this._seasonTicketCapacityApi.getSeasonVenueMap(seasonTicket.id)
                        .pipe(finalize(() => this._seasonTicketCapacityState.setVenueTplMapLoading(false)))
                        .subscribe(venueMap => this._seasonTicketCapacityState.setVenueTplMap(venueMap));
                }
            });
    }

    getVenueMap$(): Observable<VenueMap> {
        return this._seasonTicketCapacityState.getVenueTplMap$();
    }

    clearVenueMap(): void {
        this._seasonTicketCapacityState.setVenueTplMap(null);
    }

    isVenueMapLoading$(): Observable<boolean> {
        return this._seasonTicketCapacityState.isVenueTplMapLoading$();
    }

    get isCapacityUpdateAsync(): boolean {
        return true;
    }

    updateVenueMap(_: unknown, seats: Seat[], notNumberedZones: NotNumberedZone[]): Observable<boolean> {
        return this._seasonTicketService.seasonTicket.get$()
            .pipe(
                filter(value => value !== null),
                switchMap((seasonTicket: SeasonTicket) => {
                    this._seasonTicketCapacityState.setLinkableSeats(null);
                    this._seasonTicketCapacityState.setUpdateSeatsInProgress(true);
                    const puts: Observable<void>[] = [];
                    const seatsNormalUpdate = seats
                        .filter(seat =>
                            [...seat.seatRecord.labelGroupRecord.values()]
                                .some(labelGroup => labelGroup !== VenueTemplateLabelGroupType.seasonTicketLinkable)
                        );
                    if (seatsNormalUpdate?.length) {
                        puts.push(this._seasonTicketCapacityApi.putSeats(seasonTicket.id, seatsNormalUpdate));
                    }
                    if (notNumberedZones?.length) {
                        puts.push(this._seasonTicketCapacityApi.putNotNumberedZones(seasonTicket.id, notNumberedZones));
                    }
                    const seatsLinkableUpdate = seats
                        .filter(seat => seat.linkable === SeatLinkable.linkable &&
                            [...seat.seatRecord.labelGroupRecord.values()]
                                .some(labelGroup => labelGroup === VenueTemplateLabelGroupType.seasonTicketLinkable)
                        );
                    if (seatsLinkableUpdate.length) {
                        puts.push(
                            this._seasonTicketCapacityApi.putSeatsLinkable(seasonTicket.id, seatsLinkableUpdate)
                                .pipe(
                                    tap((seasonTicketLinkableSeats: SeasonTicketLinkableSeats) => {
                                        this._seasonTicketCapacityState.setLinkableSeats(seasonTicketLinkableSeats);
                                        this._seasonTicketCapacityState.getVenueTplMap$()
                                            .pipe(take(1))
                                            .subscribe(venueMap => this.updateLinkableSeats(venueMap, seasonTicketLinkableSeats));
                                    }),
                                    mapTo(null)
                                ));
                    }
                    const seatsUnLinkableUpdate = seats
                        .filter(seat => seat.linkable === SeatLinkable.notLinkable &&
                            [...seat.seatRecord.labelGroupRecord.values()]
                                .some(labelGroup => labelGroup === VenueTemplateLabelGroupType.seasonTicketLinkable)
                        );
                    if (seatsUnLinkableUpdate?.length) {
                        puts.push(
                            this._seasonTicketCapacityApi.putSeatsUnLinkable(seasonTicket.id, seatsUnLinkableUpdate)
                                .pipe(mapTo(null))
                        );

                    }
                    return concat(...puts)
                        .pipe(
                            bufferCount(puts.length),
                            mapTo(null),
                            take(1),
                            switchMap(() => this.saveSuccess$()),
                            finalize(() => this._seasonTicketCapacityState.setUpdateSeatsInProgress(false))
                        );
                })
            );
    }

    isVenueMapSaving$(): Observable<boolean> {
        return this._seasonTicketCapacityState.isUpdateSeatsInProgress$();
    }

    clearLinkableSeats(): void {
        this._seasonTicketCapacityState.setLinkableSeats(null);
    }

    getLinkableSeats$(): Observable<SeasonTicketLinkableSeats> {
        return this._seasonTicketCapacityState.getLinkableSeats$();
    }

    putNnzLinkable(seasonTicketId: number, id: number, count: number): Observable<SeasonTicketLinkNNZResponse> {
        this._seasonTicketCapacityState.setUpdateSeatsInProgress(true);
        return this._seasonTicketCapacityApi.putNnzLinkable(seasonTicketId, id, count)
            .pipe(finalize(() => this._seasonTicketCapacityState.setUpdateSeatsInProgress(false)));
    }

    putNnzNotLinkable(seasonTicketId: number, id: number, count: number): Observable<SeasonTicketUnlinkNNZResponse> {
        this._seasonTicketCapacityState.setUpdateSeatsInProgress(true);
        return this._seasonTicketCapacityApi.putNnzNotLinkable(seasonTicketId, id, count)
            .pipe(finalize(() => this._seasonTicketCapacityState.setUpdateSeatsInProgress(false)));
    }

    private updateLinkableSeats(venueMap: VenueMap, linkableSeats: SeasonTicketLinkableSeats): void {
        if (venueMap && linkableSeats) {
            const notAssignableSeats = new Map<number, SeasonTicketLinkableSeat>();
            linkableSeats.results
                .filter(linkableSeat => !linkableSeat.result)
                .forEach(linkableSeat => notAssignableSeats.set(linkableSeat.id, linkableSeat));
            if (notAssignableSeats.size) {
                let notAssignableSeat: SeasonTicketLinkableSeat;
                venueMap.sectors
                    .forEach(sector => sector.rows?.forEach(row =>
                        row.seats.forEach(seat => {
                            if (notAssignableSeats.size && notAssignableSeats.has(seat.id)) {
                                notAssignableSeat = notAssignableSeats.get(seat.id);
                                seat.linkable = SeatLinkable.notAssignable;
                                seat.notAssignableReason = notAssignableSeat.reason;
                                notAssignableSeats.delete(seat.id);
                            }
                        })
                    ));
            }
        }
    }

    private saveSuccess$(): Observable<boolean> {
        return this.getLinkableSeats$()
            .pipe(
                take(1),
                map(linkableSeats => {
                    const notLinkedReasons = linkableSeats?.results
                        .filter(linkableSeat => !linkableSeat.result)
                        .map(linkableSeat => linkableSeat.reason)
                        .reduce(
                            (presentReasons, messageKey: string) => {
                                presentReasons.add({ messageKey });
                                return presentReasons;
                            },
                            new Set<{ messageKey: string }>()
                        );
                    if (notLinkedReasons?.size) {
                        this._matDialog.open(
                            SeasonTicketCapacityDialogComponent,
                            new ObMatDialogConfig({ notLinkedReasons })
                        );
                        return false;
                    } else {
                        return true;
                    }
                })
            );
    }
}
