import { ListResponse, StateManager } from '@OneboxTM/utils-state';
import { PageableFilter } from '@admin-clients/shared/data-access/models';
import { VenueTemplate } from '@admin-clients/shared/venues/data-access/venue-tpls';
import { inject, Injectable } from '@angular/core';
import { combineLatest, forkJoin, Observable, of, Subject } from 'rxjs';
import { filter, finalize, map, startWith, takeUntil } from 'rxjs/operators';
import { StdVenueTplsApi } from './api/std-venue-tpls.api';
import { SectorElement } from './models/api-sector.model';
import { VenueTemplateAvetCompetition } from './models/avet/venue-template-avet-competition.model';
import { VenueTemplateView } from './models/venue-template-view.model';
import { NotNumberedZone, Seat, Sector, VenueMap } from './models/vm-item.model';
import { StdVenueTplsState } from './state/std-venue-tpls.state';
import { VenueMapService } from './venue-map-service.token';

@Injectable()
export class StdVenueTplService implements VenueMapService {

    private readonly _stdVenueTplsApi = inject(StdVenueTplsApi);
    private readonly _stdVenueTplsState = inject(StdVenueTplsState);

    private readonly _templateChange = new Subject<void>();
    private readonly _viewChange = new Subject<void>();
    private readonly _viewsChange = new Subject<void>();

    get isCapacityUpdateAsync(): boolean {
        return false;
    }

    clearVenueTemplateData(): void {
        this._templateChange.next();
        this._viewsChange.next();
        this._viewChange.next();
        this._stdVenueTplsState.svg.setValue(null);
        this._stdVenueTplsState.view.setValue(null);
        this._stdVenueTplsState.venueMap.setValue(null);
    }

    // venue map

    loadVenueMap({ tplId }: { tplId: number }): void {
        this._stdVenueTplsState.venueMap.setInProgress(true);
        this._stdVenueTplsApi.getVenueTplVenueMap(tplId)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._stdVenueTplsState.venueMap.setInProgress(false))
            )
            .subscribe(venueMap => this._stdVenueTplsState.venueMap.setValue(venueMap));
    }

    getVenueMap$(): Observable<VenueMap> {
        return this._stdVenueTplsState.venueMap.getValue$();
    }

    isVenueMapLoading$(): Observable<boolean> {
        return this._stdVenueTplsState.venueMap.isInProgress$();
    }

    clearVenueMap(): void {
        this._stdVenueTplsState.venueMap.setValue(null);
    }

    updateVenueMap({ tplId }: { tplId: number }, seats: Seat[], notNumberedZones: NotNumberedZone[]): Observable<boolean> {
        this._stdVenueTplsState.venueMapUpdate.setInProgress(true);
        return forkJoin([
            seats.length && this._stdVenueTplsApi.putSeats(tplId, seats)
            || of(null).pipe(startWith(true)),
            notNumberedZones.length && this._stdVenueTplsApi.putNotNumberedZones(tplId, notNumberedZones)
            || of(null).pipe(startWith(true))
        ])
            .pipe(
                map(() => true),
                finalize(() => this._stdVenueTplsState.venueMapUpdate.setInProgress(false))
            );
    }

    isVenueMapSaving$(): Observable<boolean> {
        return this._stdVenueTplsState.venueMapUpdate.isInProgress$();
    }

    // VIEWS

    loadVenueTemplateViews(venueTplId: number, req?: PageableFilter, sessionId?: number): void {
        this._viewsChange.next();
        this._stdVenueTplsState.views.setInProgress(true);
        this._stdVenueTplsApi.getVenueTplViews(venueTplId, req, sessionId)
            .pipe(
                takeUntil(this._viewsChange),
                finalize(() => this._stdVenueTplsState.views.setInProgress(false)))
            .subscribe(views => this._stdVenueTplsState.views.setValue(views));
    }

    getVenueTemplateViews$(): Observable<ListResponse<VenueTemplateView>> {
        return this._stdVenueTplsState.views.getValue$();
    }

    isVenueTemplateViewsLoading$(): Observable<boolean> {
        return this._stdVenueTplsState.views.isInProgress$();
    }

    updateVenueTemplateVipViews(venueTplId: number, views: Partial<VenueTemplateView>[], sessionId?: number): Observable<void> {
        this._stdVenueTplsState.views.setInProgress(true);
        return this._stdVenueTplsApi.putVenueTplVipViews(venueTplId, views, sessionId)
            .pipe(finalize(() => this._stdVenueTplsState.views.setInProgress(false)));
    }

    // VIEW

    loadVenueTemplateRootView(venueTemplateId: number): void {
        this.loadVenueTemplateView(venueTemplateId, 'root');
    }

    loadVenueTemplateView(venueTemplateId: number, viewId: number | 'root'): void {
        this._viewChange.next();
        this._stdVenueTplsState.view.setInProgress(true);
        this._stdVenueTplsApi.getVenueTplView(venueTemplateId, viewId)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._stdVenueTplsState.view.setInProgress(false)))
            .subscribe(view => this._stdVenueTplsState.view.setValue(view));
    }

    getVenueTemplateView$(): Observable<VenueTemplateView> {
        return this._stdVenueTplsState.view.getValue$();
    }

    isVenueTemplateViewLoading$(): Observable<boolean> {
        return this._stdVenueTplsState.view.isInProgress$();
    }

    // SVG

    loadVenueTemplateSVG(url: string): void {
        if (url) {
            this._stdVenueTplsState.svg.setInProgress(true);
            this._stdVenueTplsApi.getVenueTplSVG(url)
                .pipe(
                    takeUntil(this._viewChange),
                    finalize(() => this._stdVenueTplsState.svg.setInProgress(false))
                )
                .subscribe(svg => this._stdVenueTplsState.svg.setValue(svg));
        } else {
            // no url > void svg
            this._stdVenueTplsState.svg.setValue('<svg></svg>');
        }
    }

    getVenueTemplateSVG$(): Observable<string> {
        return this._stdVenueTplsState.svg.getValue$();
    }

    isVenueTemplateSVGLoading$(): Observable<boolean> {
        return this._stdVenueTplsState.svg.isInProgress$();
    }

    // Sectors

    loadSectors = (venueTemplateId: number): void => {
        StateManager.load(
            this._stdVenueTplsState.sectors,
            this._stdVenueTplsApi.getSectors(venueTemplateId)
        );
    };

    getSectors$ = (): Observable<SectorElement[]> => this._stdVenueTplsState.sectors.getValue$();

    // sector

    loadSector(venueTemplateId: number, sectorId: number): Observable<Sector> {
        this._stdVenueTplsState.sector.setInProgress(true);
        return combineLatest([
            this._stdVenueTplsApi.getSector(venueTemplateId, sectorId),
            this._stdVenueTplsApi.getSectorNotNumberedZones(venueTemplateId, sectorId)
        ]).pipe(
            filter(results => results.every(result => result !== null)),
            map(([loadedSector, nnzs]) => {
                loadedSector.notNumberedZones = nnzs;
                return loadedSector;
            }),
            finalize(() => this._stdVenueTplsState.sector.setInProgress(false))
        );
    }

    isSectorLoading$(): Observable<boolean> {
        return this._stdVenueTplsState.sector.isInProgress$();
    }

    createSector(venueTemplateId: number, newSectorName: string): Observable<{ id: number }> {
        this._stdVenueTplsState.sectorUpdate.setInProgress(true);
        return this._stdVenueTplsApi.postSector(venueTemplateId, { name: newSectorName })
            .pipe(finalize(() => this._stdVenueTplsState.sectorUpdate.setInProgress(false)));
    }

    cloneSector(venueTemplateId: number, sourceSectorId: number, newSectorName: string): Observable<{ id: number }> {
        this._stdVenueTplsState.sectorUpdate.setInProgress(true);
        return this._stdVenueTplsApi.cloneSector(venueTemplateId, sourceSectorId, newSectorName)
            .pipe(finalize(() => this._stdVenueTplsState.sectorUpdate.setInProgress(false)));
    }

    updateSectorName(venueTemplateId: number, sector: Sector): Observable<void> {
        this._stdVenueTplsState.sectorUpdate.setInProgress(true);
        return this._stdVenueTplsApi.putSector(venueTemplateId, sector)
            .pipe(finalize(() => this._stdVenueTplsState.sectorUpdate.setInProgress(false)));
    }

    deleteSector(venueTemplateId: number, sectorId: number): Observable<void> {
        this._stdVenueTplsState.sectorUpdate.setInProgress(true);
        return this._stdVenueTplsApi.deleteSector(venueTemplateId, sectorId)
            .pipe(finalize(() => this._stdVenueTplsState.sectorUpdate.setInProgress(false)));
    }

    isSectorSaving$(): Observable<boolean> {
        return this._stdVenueTplsState.sectorUpdate.isInProgress$();
    }

    // NOT NUMBERED ZONE

    loadZone(venueTemplateId: number, zoneId: number): Observable<NotNumberedZone> {
        this._stdVenueTplsState.nnz.setInProgress(true);
        return this._stdVenueTplsApi.getNotNumberedZone(venueTemplateId, zoneId)
            .pipe(
                filter(zone => zone !== null),
                finalize(() => this._stdVenueTplsState.nnz.setInProgress(false))
            );
    }

    isZoneLoading$(): Observable<boolean> {
        return this._stdVenueTplsState.nnz.isInProgress$();
    }

    createZone(venueTemplateId: number, nnz: Omit<NotNumberedZone, 'id'>): Observable<{ id: number }> {
        this._stdVenueTplsState.nnzUpdate.setInProgress(true);
        return this._stdVenueTplsApi.postNotNumberedZone(venueTemplateId, nnz)
            .pipe(finalize(() => this._stdVenueTplsState.nnzUpdate.setInProgress(false)));
    }

    cloneZone(venueTemplateId: number, sourceZoneId: number, sectorId: number, newZoneName: string): Observable<{ id: number }> {
        this._stdVenueTplsState.nnzUpdate.setInProgress(true);
        return this._stdVenueTplsApi.cloneNotNumberedZone(venueTemplateId, sourceZoneId, sectorId, newZoneName)
            .pipe(finalize(() => this._stdVenueTplsState.nnzUpdate.setInProgress(false)));
    }

    updateZone(venueTemplateId: number, zone: NotNumberedZone): Observable<void> {
        this._stdVenueTplsState.nnzUpdate.setInProgress(true);
        return this._stdVenueTplsApi.putNotNumberedZone(venueTemplateId, zone)
            .pipe(finalize(() => this._stdVenueTplsState.nnzUpdate.setInProgress(false)));
    }

    deleteZone(venueTemplateId: number, zoneId: number): Observable<void> {
        this._stdVenueTplsState.nnzUpdate.setInProgress(true);
        return this._stdVenueTplsApi.deleteNotNumberedZone(venueTemplateId, zoneId)
            .pipe(finalize(() => this._stdVenueTplsState.nnzUpdate.setInProgress(false)));
    }

    isNnzSaving$(): Observable<boolean> {
        return this._stdVenueTplsState.nnzUpdate.isInProgress$();
    }

    // CAPACITY INCREASE

    capIncrZonesIncrease(venueTemplate: VenueTemplate, zones: NotNumberedZone[]): Observable<void> {
        this._stdVenueTplsState.nnzIncrease.setInProgress(true);
        return this._stdVenueTplsApi.putNotNumberedZonesBulk(venueTemplate.id, zones)
            .pipe(finalize(() => this._stdVenueTplsState.nnzIncrease.setInProgress(false)));
    }

    capIncrZonesCreation(venueTemplate: VenueTemplate, zones: NotNumberedZone[]): Observable<void> {
        this._stdVenueTplsState.nnzIncrease.setInProgress(true);
        return this._stdVenueTplsApi.postNotNumberedZonesBulk(venueTemplate.id, zones)
            .pipe(finalize(() => this._stdVenueTplsState.nnzIncrease.setInProgress(false)));
    }

    isNnzCapacityIncreaseSaving$(): Observable<boolean> {
        return this._stdVenueTplsState.nnzIncrease.isInProgress$();
    }

    // AVET COMPETITIONS

    clearVenueTemplateAvetCompetitions(): void {
        this._stdVenueTplsState.avetCompetitions.setValue(null);
    }

    loadVenueTemplateAvetCompetitions(venueTemplateId: number, eventEntityId: number, skipUsed: boolean): void {
        this._stdVenueTplsState.avetCompetitions.setInProgress(true);
        this._stdVenueTplsApi.getVenueTplAvetCompetitions(venueTemplateId, eventEntityId, skipUsed)
            .pipe(
                takeUntil(this._templateChange),
                finalize(() => this._stdVenueTplsState.avetCompetitions.setInProgress(false))
            )
            .subscribe(competitions => this._stdVenueTplsState.avetCompetitions.setValue(competitions));
    }

    getVenueTemplateAvetCompetitions$(): Observable<VenueTemplateAvetCompetition[]> {
        return this._stdVenueTplsState.avetCompetitions.getValue$();
    }

    isVenueTemplateAvetCompetitionInProgress$(): Observable<boolean> {
        return this._stdVenueTplsState.avetCompetitions.isInProgress$();
    }
}
