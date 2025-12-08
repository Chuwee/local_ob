import { cloneObject, mergeObjects } from '@admin-clients/shared/utility/utils';
import {
    NotNumberedZone, SeatStatus, Sector, StdVenueTplService, VENUE_MAP_SERVICE, VenueMapService, VenueTemplateItemType
} from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { VenueTemplate, VenueTemplatesService } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { Inject, Injectable, Optional } from '@angular/core';
import { combineLatest, Observable, of } from 'rxjs';
import { map, mapTo, switchMap, take, tap, withLatestFrom } from 'rxjs/operators';
import { VenueTemplateLabel } from '../models/label-group/venue-template-label-group-list.model';
import { VenueTemplateEditorState } from '../models/venue-template-editor-state.enum';
import { StandardVenueTemplateBaseService } from '../services/standard-venue-template-base.service';
import { StandardVenueTemplateChangesService } from '../services/standard-venue-template-changes.service';

@Injectable()
export class VenueTemplateTreeService {

    constructor(
        @Inject(VENUE_MAP_SERVICE) @Optional() private _venueMapSrv: VenueMapService,
        private _venueTemplatesSrv: VenueTemplatesService,
        private _stdVenueTplSrv: StdVenueTplService,
        private _standardVenueTemplateSrv: StandardVenueTemplateBaseService,
        private _standardVenueTemplateChangesSrv: StandardVenueTemplateChangesService
    ) {
        this._venueMapSrv ??= this._stdVenueTplSrv;
    }

    loadAndUpdateSector(venueTemplateId: number, sectorId: number): Observable<Sector> {
        return this._stdVenueTplSrv.loadSector(venueTemplateId, sectorId)
            .pipe(
                withLatestFrom(this._venueMapSrv.getVenueMap$()),
                map(([sector, venueMap]) => {
                        /* if the sector is found in the old collection it means that is and edition, in this case,
                         it only updates the name of the sector, that is the only editable field of a sector */
                        const targetSector = venueMap.sectors.find(s => s.id === sector.id) || sector;
                        targetSector.name = sector.name;
                        targetSector.notNumberedZones?.forEach(zone => zone.sectorName = sector.name);
                        if (!venueMap.sectors.includes(targetSector)) {
                            venueMap.sectors.push(sector);
                        }
                        this._standardVenueTemplateSrv.updateVenueMapElements();
                        return sector;
                    }
                )
            );
    }

    loadAndUpdateZone(venueTemplateId: number, zoneId: number): Observable<NotNumberedZone> {
        return this._stdVenueTplSrv.loadZone(venueTemplateId, zoneId)
            .pipe(
                withLatestFrom(this._venueMapSrv.getVenueMap$()),
                map(([zone, venueMap]) => {
                    const oldZone = venueMap.sectors.find(sector => sector.id === zone.sector).notNumberedZones.find(z => z.id === zone.id);
                    mergeObjects(oldZone, zone);
                    oldZone.record = undefined;
                    oldZone.record = { initialState: cloneObject(oldZone) };
                    return oldZone;
                })
            );
    }

    loadAndAddZone(venueTemplateId: number, zoneId: number): Observable<NotNumberedZone> {
        return this._stdVenueTplSrv.loadZone(venueTemplateId, zoneId)
            .pipe(
                withLatestFrom(this._venueMapSrv.getVenueMap$()),
                map(([zone, venueMap]) => {
                    const sector = venueMap.sectors.find(sector => sector.id === zone.sector);
                    zone.sectorName = sector.name;
                    zone.record = { initialState: cloneObject(zone) };
                    sector.notNumberedZones.push(zone);
                    this._standardVenueTemplateSrv.updateVenueMapElements();
                    return zone;
                })
            );
    }

    createSectorCapacityIncrease(name: string): Observable<number> {
        return this._venueMapSrv.getVenueMap$()
            .pipe(
                take(1),
                map(venueMap => {
                    const newSector: Sector = {
                        itemType: VenueTemplateItemType.sector,
                        id: VenueTemplateTreeService.searchNotUsedId(venueMap.sectors.map(sector => sector.id)),
                        name,
                        record: {
                            pendingToCreate: true
                        }
                    };
                    venueMap.sectors.push(newSector);
                    this._standardVenueTemplateChangesSrv.getModifiedItems$()
                        .pipe(take(1))
                        .subscribe(modifiedItems => modifiedItems.sectors.add(newSector.id));
                    return newSector.id;
                }));
    }

    updateSectorCapacityIncrease(sectorId: number, name: string): void {
        this._venueMapSrv.getVenueMap$()
            .pipe(take(1))
            .subscribe(venueMap => {
                const sectorToUpdate = venueMap.sectors.find(sector => sector.id === sectorId);
                sectorToUpdate.name = name;
                this._standardVenueTemplateChangesSrv.getModifiedItems$()
                    .pipe(take(1))
                    .subscribe(modifiedItems => modifiedItems.sectors.add(sectorToUpdate.id));
                return sectorToUpdate.id;
            });
    }

    createNnzCapacityIncrease(sectorId: number, name: string, capacity: number, defaultQuotaLabel: VenueTemplateLabel): Observable<number> {
        return combineLatest([
            this._venueMapSrv.getVenueMap$(),
            this._standardVenueTemplateSrv.getVenueItems$()
        ])
            .pipe(
                take(1),
                map(([venueMap, venueItems]) => {
                    const newNNZ: NotNumberedZone = {
                        itemType: VenueTemplateItemType.notNumberedZone,
                        id: VenueTemplateTreeService.searchNotUsedId(Array.from(venueItems.nnzs.keys())),
                        sector: sectorId,
                        name,
                        capacity: 0
                    };
                    newNNZ.record = {
                        initialState: cloneObject(newNNZ),
                        pendingToCreate: true
                    };
                    newNNZ.capacity = capacity;
                    newNNZ.statusCounters = [{
                        itemType: VenueTemplateItemType.notNumberedZoneStatusCounter,
                        status: SeatStatus.free,
                        count: capacity
                    }];
                    newNNZ.quotaCounters = [{
                        itemType: VenueTemplateItemType.notNumberedZoneQuotaCounters,
                        quota: +defaultQuotaLabel.id,
                        count: capacity,
                        available: capacity
                    }];
                    const sector = venueMap.sectors.find(sector => sector.id === sectorId);
                    if (!sector.notNumberedZones) {
                        sector.notNumberedZones = [];
                    }
                    sector.notNumberedZones.push(newNNZ);
                    this._standardVenueTemplateChangesSrv.setModifiedItem(newNNZ);
                    this._standardVenueTemplateSrv.updateVenueMapElements();
                    return newNNZ.id;
                }));
    }

    updateNnzCapacityIncrease(
        nnz: NotNumberedZone,
        name: string,
        capacity: number,
        quotaCounterDelta: { quota: number; countDelta: number }
    ): void {
        // editable fields
        nnz.name = name;
        nnz.capacity = capacity;
        this.setStatusCounters(nnz);
        this.setQuotaCounters(quotaCounterDelta, nnz);
        // sets new modified items
        this._standardVenueTemplateChangesSrv.setModifiedItem(nnz);
        // updates counters
        this._standardVenueTemplateSrv.updateVenueMapElements();
    }

    deleteSector(venueTemplate: VenueTemplate, sector: Sector): Observable<void> {
        return this._standardVenueTemplateSrv.getCurrentState$()
            .pipe(
                take(1),
                switchMap(state => {
                    if (state !== VenueTemplateEditorState.capacityIncrease) {
                        return this._stdVenueTplSrv.deleteSector(venueTemplate.id, sector.id)
                            .pipe(tap(() => this.removeStateSector(sector)));
                    } else {
                        this.removeStateSector(sector);
                        return of(null);
                    }
                })
            );
    }

    deleteZone(venueTemplate: VenueTemplate, zone: NotNumberedZone): Observable<void> {
        return this._standardVenueTemplateSrv.getCurrentState$()
            .pipe(
                take(1),
                switchMap(state => {
                    if (state !== VenueTemplateEditorState.capacityIncrease) {
                        return this._stdVenueTplSrv.deleteZone(venueTemplate.id, zone.id)
                            .pipe(tap(() => this.removeStateNnz(zone)));
                    } else {
                        this.removeStateNnz(zone);
                        return of(null);
                    }
                })
            );
    }

    /*
     This sequence has 4 steps,
     1. sector update (name change, independent)
     2. sector creation (could have inner not numbered zones, and requires to create its sectors before, independent)
     3. NNZ update (name and/or capacity, independent)
     4. NNZ creation, could be on new sectors, for this reason, must wait the sector creation to contain the right sector ids (dependent)
     */
    commitIncrease(): Observable<void> {
        return combineLatest([
            this._venueTemplatesSrv.venueTpl.get$(),
            this._standardVenueTemplateChangesSrv.getModifiedItems$(),
            this._standardVenueTemplateSrv.getVenueItems$(),
            this._stdVenueTplSrv.getVenueMap$()
        ]).pipe(
            take(1),
            switchMap(([venueTemplate, modifiedItems, venueItems, venueMap]) => {
                // save steps
                const saveSteps: Observable<void>[] = [];
                // modified zoneIds and sectorIds to modified sector array
                const sectors = Array.from(modifiedItems.sectors).map(sectorId => venueMap.sectors.find(s => s.id === sectorId));
                const zones = Array.from(modifiedItems.nnzs).map(zoneId => venueItems.nnzs.get(zoneId));
                // sectors edition
                sectors.filter(s => !s.record?.pendingToCreate)
                    .forEach(sector => saveSteps.push(this._stdVenueTplSrv.updateSectorName(venueTemplate.id, sector)));
                // sectors creation
                sectors.filter(s => s.record?.pendingToCreate)
                    .forEach(sector => saveSteps.push(
                        this._stdVenueTplSrv.createSector(venueTemplate.id, sector.name).pipe(
                            tap(idWrapper => {
                                sector.id = idWrapper.id;
                                sector.notNumberedZones.forEach(zone => zone.sector = sector.id);
                            }),
                            mapTo(null)
                        )));
                // zones update
                const zonesToIncrease = zones.filter(zone => !zone.record?.pendingToCreate);
                if (zonesToIncrease.length) {
                    saveSteps.push(this._stdVenueTplSrv.capIncrZonesIncrease(
                        venueTemplate,
                        this.getNnzsWithIncreasedQuotaCounters(zonesToIncrease)
                    ));
                }
                let result: Observable<void>;
                if (saveSteps.length) {
                    result = combineLatest(saveSteps).pipe(mapTo(null));
                } else {
                    result = of(null);
                }
                // zone creation data preparation, calls must be delayed, to wait the potential creation of the zones sectors.
                return result.pipe(mapTo({ venueTemplate, zonesToCreate: zones.filter(zone => zone.record?.pendingToCreate) }));
            }),
            switchMap(({ venueTemplate, zonesToCreate }) => {
                if (zonesToCreate.length) {
                    return this._stdVenueTplSrv.capIncrZonesCreation(
                        venueTemplate,
                        zonesToCreate
                    );
                } else {
                    return of(null);
                }
            })
        );
    }

    private getNnzsWithIncreasedQuotaCounters(nnzs: NotNumberedZone[]): NotNumberedZone[] {
        return nnzs.map(nnz => {
            const initialStateQuotaCountersMap = new Map(nnz.record.initialState.quotaCounters?.map(quotaCounter =>
                [quotaCounter.quota.toString(), quotaCounter]));
            const nnzQuotaCountersMap = new Map(nnz.quotaCounters.map(quotaCounter =>
                [quotaCounter.quota.toString(), quotaCounter]));
            let foundQuotaCounterId: string;
            nnzQuotaCountersMap.forEach((nnzQuotaCounter, id) => {
                if (
                    (initialStateQuotaCountersMap.has(id) && initialStateQuotaCountersMap.get(id).count !== nnzQuotaCounter.count) ||
                    !initialStateQuotaCountersMap.has(id)
                ) {
                    foundQuotaCounterId = id;
                }
            });
            return {
                ...nnz,
                quotaCounters: [nnzQuotaCountersMap.get(foundQuotaCounterId)]
            };
        });
    }

    private removeStateNnz(zone: NotNumberedZone): void {
        this._venueMapSrv.getVenueMap$()
            .pipe(take(1))
            .subscribe(venueMap => {
                const sector = venueMap.sectors.find(s => s.notNumberedZones.find(z => z.id === zone.id));
                sector.notNumberedZones = sector.notNumberedZones.filter(z => z.id !== zone.id);
                this._standardVenueTemplateChangesSrv.setModifiedItem(zone, false);
                this._standardVenueTemplateSrv.updateVenueMapElements();
            });
    }

    private removeStateSector(sector: Sector): void {
        combineLatest([
            this._venueTemplatesSrv.venueTpl.get$(),
            this._venueMapSrv.getVenueMap$()
        ])
            .pipe(take(1))
            .subscribe(([venueTemplate, venueMap]) => {
                sector.notNumberedZones?.forEach(zone => this.deleteZone(venueTemplate, zone));
                venueMap.sectors.splice(venueMap.sectors.indexOf(sector), 1);
                this._standardVenueTemplateChangesSrv.setModifiedItem(sector, false);
                this._standardVenueTemplateSrv.updateVenueMapElements();
            });
    }

    private static searchNotUsedId(idsToSkip: number[]): number {
        let result = 1;
        let alreadyUsed = true;
        while (alreadyUsed) {
            if (idsToSkip.includes(result)) {
                result++;
            } else {
                alreadyUsed = false;
            }
        }
        return result;
    }

    private setStatusCounters(nnz: NotNumberedZone): void {
        // the free status counter is used to show the sector capacity, extra capacity is added to the sc, if it is present,
        // if not it creates it, capacity can be reduced when an increment still isn't saved, could let an empty free sc. sm ok wd evrd-i
        const scCapacity = nnz.statusCounters.map(sc => sc.count).reduce((prev, curr) => prev + curr, 0);
        if (scCapacity !== nnz.capacity) {
            const freeSC = nnz.statusCounters.find(sc => sc.status === SeatStatus.free)
                || {
                    itemType: VenueTemplateItemType.notNumberedZoneStatusCounter,
                    status: SeatStatus.free,
                    count: 0
                };
            freeSC.count += nnz.capacity - scCapacity;
            if (!nnz.statusCounters.includes(freeSC)) {
                nnz.statusCounters.push(freeSC);
            }
        }
    }

    private setQuotaCounters(quotaCounterDelta: { quota: number; countDelta: number }, nnz: NotNumberedZone): void {
        nnz.quotaCounters = cloneObject(nnz.record.initialState.quotaCounters);
        const quotaCounter = nnz.quotaCounters?.find(quotaCounter => quotaCounter.quota === quotaCounterDelta.quota) ||
            {
                quota: quotaCounterDelta.quota,
                count: 0,
                itemType: VenueTemplateItemType.notNumberedZoneQuotaCounters
            };
        quotaCounter.count += quotaCounterDelta.countDelta;
        if (!nnz.quotaCounters) {
            nnz.quotaCounters = [quotaCounter];
        } else if (!nnz.quotaCounters.includes(quotaCounter)) {
            nnz.quotaCounters.push(quotaCounter);
        }
    }
}
