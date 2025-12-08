/* eslint-disable @typescript-eslint/naming-convention */
import { buildHttpParams } from '@OneboxTM/utils-http';
import { ListResponse } from '@OneboxTM/utils-state';
import { APP_BASE_API } from '@admin-clients/shared/core/data-access';
import { Id, PageableFilter } from '@admin-clients/shared/data-access/models';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiNotNumberedZone } from '../models/api-not-numbered-zone.model';
import { SectorElement } from '../models/api-sector.model';
import { VenueTemplateAvetCompetition } from '../models/avet/venue-template-avet-competition.model';
import { VenueTemplateImage } from '../models/image/venue-template-image.model';
import { VenueTemplateViewLink } from '../models/venue-template-view-link.model';
import { VenueTemplateView } from '../models/venue-template-view.model';
import { VenueTemplateItemType } from '../models/vm-item-type.enum';
import { NotNumberedZone, Row, Seat, Sector, VenueMap } from '../models/vm-item.model';
import { decodeAndMapVenueMap } from './venue-map-mapper';

@Injectable({ providedIn: 'root' })
export class StdVenueTplsApi {

    private readonly BASE_API = inject(APP_BASE_API);
    private readonly VENUE_TEMPLATES_API = `${this.BASE_API}/mgmt-api/v1/venue-templates`;

    private readonly _http = inject(HttpClient);

    getVenueTplVenueMap(venueTplId: number): Observable<VenueMap> {
        return this._http.get(
            `${this.BASE_API}/mgmt-api/v1/venue-templates/${venueTplId}/map`,
            {
                headers: new HttpHeaders({ accept: 'application/x-protobuf' }),
                responseType: 'arraybuffer'
            })
            .pipe(map((arrayBuffer: ArrayBuffer) => decodeAndMapVenueMap(arrayBuffer)));
    }

    // VIEWS

    getVenueTplViews(venueTplId: number, request?: PageableFilter, sessionId?: number): Observable<ListResponse<VenueTemplateView>> {
        const params = request && buildHttpParams({ ...request, session_id: sessionId });
        return this._http.get<ListResponse<VenueTemplateView>>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/views`,
            { params }
        );
    }

    getVenueTplView(venueTplId: number, viewId: number | 'root'): Observable<VenueTemplateView> {
        return this._http.get<VenueTemplateView>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views/${viewId}`);
    }

    postVenueTplView(venueTplId: number, view: Omit<VenueTemplateView, 'id'>): Observable<Id> {
        return this._http.post<Id>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views`,
            {
                name: view.name,
                code: view.code,
                root: view.root,
                aggregated_view: view.aggregated_view,
                display_3D: view.display_3D,
                vip: view.vip,
                orientation: view.orientation
            });
    }

    putVenueTplView(venueTplId: number, view: VenueTemplateView): Observable<void> {
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views/${view.id}`, {
            name: view.name,
            code: view.code,
            root: view.root,
            aggregated_view: view.aggregated_view,
            display_3D: view.display_3D,
            vip: view.vip,
            orientation: view.orientation
        });
    }

    putVenueTplVipViews(venueTplId: number, views: Partial<VenueTemplateView>[], sessionId?: number): Observable<void> {
        const params = sessionId && { session_id: sessionId };
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views/vip`, views, { params });
    }

    putVenueTplViews(venueTplId: number, templateViews: VenueTemplateView[]): Observable<void> {
        const views = templateViews.map(templateView => ({
            id: templateView.id,
            name: templateView.name,
            code: templateView.code,
            aggregated_view: templateView.aggregated_view,
            display_3D: templateView.display_3D,
            vip: templateView.vip,
            orientation: templateView.orientation
        }));
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views`, views);
    }

    deleteVenueTplView(venueTplId: number, viewId: number): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views/${viewId}`);
    }

    getVenueTplSVG(url: string): Observable<string> {
        return this._http.get(url, {
            headers: new HttpHeaders().set('Content-Type', 'text/xml'),
            responseType: 'text'
        });
    }

    putVenueTplSVG(venueTplId: number, viewId: number, svg: string): Observable<void> {
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views/${viewId}/template`, svg);
    }

    postLink(venueTplId: number, viewId: number, targetViewId: number): Observable<Id> {
        return this._http.post<Id>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views/${viewId}/links`, { view_id: targetViewId });
    }

    deleteLink(venueTplId: number, viewId: number, linkId: number): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/views/${viewId}/links/${linkId}`);
    }

    putLinks(venueTplId: number, links: VenueTemplateViewLink[]): Observable<void> {
        const body = links.map(link => ({
            id: link.id,
            view_id: link.view_id
        }));
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/links`, body);
    }

    postSeats(venueTplId: number, rows: Partial<Row>[]): Observable<Id[]> {
        const seatsToCreate = rows.map(row => row.seats.map(seat => ({
            row_id: row.id,
            row_block: seat.rowBlock,
            name: seat.name,
            view_id: seat.view,
            position_x: seat.posX,
            position_y: seat.posY,
            weight: seat.weight,
            sort: seat.order
        })))
            .flatMap(seats => seats);
        return this._http.post<Id[]>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/seats`, seatsToCreate);
    }

    // Erradicate falsy non null values please
    putSeats(venueTplId: number, seats: Partial<Seat>[]): Observable<void> {
        const seatsToSave = seats.map(seat => ({
            id: seat.id,
            name: seat.name,
            weight: seat.weight,
            position_x: seat.posX,
            position_y: seat.posY,
            sort: seat.order,
            row_block: seat.rowBlock,
            external_id: seat.external,
            status: seat.status,
            blocking_reason: seat.blockingReason !== -1 ? seat.blockingReason : undefined,
            price_type: seat.priceType,
            quota: seat.quota,
            visibility: seat.visibility,
            accessibility: seat.accessibility,
            gate: seat.gate,
            dynamic_tag1: seat.firstCustomTag > 0 ? seat.firstCustomTag : undefined,
            dynamic_tag2: seat.secondCustomTag > 0 ? seat.secondCustomTag : undefined
        }));
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/seats`, seatsToSave);
    }

    deleteSeats(venueTplId: number, seatsToDelete: number[]): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/seats`,
            { params: buildHttpParams({ seat_ids: seatsToDelete }) }
        );
    }

    // ROW

    getRow(venueTplId: number, rowId: number): Observable<Row> {
        return this._http.get<Row>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/rows/${rowId}`);
    }

    putRow(venueTplId: number, row: Omit<Row, 'seats'>): Observable<void> {
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/rows/${row.id}`, { name: row.name });
    }

    postRow(venueTplId: number, row: Omit<Row, 'id' | 'seats'>): Observable<Id> {
        return this._http.post<Id>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/rows`,
            {
                name: row.name,
                order: row.order,
                sector_id: row.sector
            }
        );
    }

    postRows(venueTplId: number, rows: Omit<Row, 'id' | 'seats'>[]): Observable<number[]> {
        return this._http.post<number[]>(
            `${this.VENUE_TEMPLATES_API}/${venueTplId}/rows/bulk`,
            rows.map(row => ({
                name: row.name,
                order: row.order,
                sector_id: row.sector
            }))
        );
    }

    deleteRow(venueTplId: number, rowId: number): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/rows/${rowId}`);
    }

    // SECTORS

    getSectors = (id: number): Observable<SectorElement[]> =>
        this._http.get<SectorElement[]>(`${this.VENUE_TEMPLATES_API}/${id}/sectors/`);

    getSector(venueTemplateId: number, sectorId: number): Observable<Sector> {
        return this._http.get<Sector>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/sectors/` + sectorId)
            .pipe(
                map(apiSector => {
                    if (apiSector) {
                        return {
                            itemType: VenueTemplateItemType.sector,
                            id: apiSector.id,
                            name: apiSector.name,
                            code: apiSector.code,
                            default: apiSector.default,
                            rows: [],
                            notNumberedZones: []
                        } as Sector;
                    } else {
                        return null;
                    }
                })
            );
    }

    getSectorNotNumberedZones(venueTemplateId: number, sectorId: number): Observable<NotNumberedZone[]> {
        return this._http.get<ApiNotNumberedZone[]>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/sectors/` + sectorId + '/not-numbered-zones')
            .pipe(map(apiNNZs => apiNNZs?.map(apiNNZ => this.mapApiNotNumberedZone(apiNNZ))));
    }

    postSector(venueTemplateId: number, item: { name: string; code?: string }): Observable<Id> {
        return this._http.post<Id>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/sectors`, { name: item.name, code: item.code });
    }

    cloneSector(venueTemplateId: number, sourceSectorId: number, newSectorName: string): Observable<Id> {
        return this._http.post<Id>(
            `${this.VENUE_TEMPLATES_API}/${venueTemplateId}/sectors/` + sourceSectorId + '/clone',
            { name: newSectorName });
    }

    putSector(venueTemplateId: number, sector: { id: number; name: string; code?: string }): Observable<void> {
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/sectors/${sector.id}`,
            { name: sector.name, code: sector.code });
    }

    deleteSector(venueTemplateId: number, sectorId: number): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/sectors/` + sectorId);
    }

    getNotNumberedZone(venueTemplateId: number, zoneId: number): Observable<NotNumberedZone> {
        return this._http.get<ApiNotNumberedZone>(`${this.VENUE_TEMPLATES_API}/${venueTemplateId}/not-numbered-zones/${zoneId}`)
            .pipe(map(apiNNZ => this.mapApiNotNumberedZone(apiNNZ)));
    }

    postNotNumberedZone(venueTplId: number, zone: Omit<NotNumberedZone, 'id'>): Observable<Id> {
        return this._http.post<Id>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/not-numbered-zones/`, {
            name: zone.name,
            capacity: zone.capacity,
            sector_id: zone.sector,
            view_id: zone.view
        });
    }

    postNotNumberedZonesBulk(venueTplId: number, nnzs: NotNumberedZone[]): Observable<void> {
        return this._http.post<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/not-numbered-zones/bulk`,
            nnzs.map(nnz => ({
                name: nnz.name,
                sector_id: nnz.sector,
                capacity: nnz.capacity,
                quota_counters: [{ quota_id: nnz.quotaCounters[0].quota, count: nnz.quotaCounters[0].count }]
            }))
        );
    }

    cloneNotNumberedZone(venueTplId: number, sourceZoneId: number, sectorId: number, newZoneName: string): Observable<Id> {
        return this._http.post<Id>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/not-numbered-zones/${sourceZoneId}/clone`, {
            name: newZoneName,
            sector_id: sectorId
        });
    }

    putNotNumberedZone(venueTplId: number, zone: NotNumberedZone): Observable<void> {
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/not-numbered-zones/${zone.id}`, {
            name: zone.name,
            capacity: zone.capacity,
            quota_id: zone.quotaCounters?.[0].quota,
            sector_id: zone.sector
        });
    }

    putNotNumberedZones(venueTplId: number, nnzs: NotNumberedZone[]): Observable<void> {
        const zonesToSave = nnzs.map(nnz => ({
            id: nnz.id,
            status_counters: nnz.statusCounters,
            blocking_reason_counters: nnz.blockingReasonCounters,
            price_type: nnz.priceType,
            visibility: nnz.visibility,
            accessibility: nnz.accessibility,
            gate: nnz.gate,
            quota_counters: nnz.quotaCounters,
            dynamic_tag1: nnz.firstCustomTag > 0 ? nnz.firstCustomTag : undefined,
            dynamic_tag2: nnz.secondCustomTag > 0 ? nnz.secondCustomTag : undefined
        }));
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/not-numbered-zones`, zonesToSave);
    }

    putNotNumberedZonesBulk(venueTplId: number, nnzs: NotNumberedZone[]): Observable<void> {
        const zonesToSave = nnzs.map(nnz => ({
            id: nnz.id,
            name: nnz.name,
            capacity: nnz.capacity,
            quota_id: nnz.quotaCounters[0].quota
        }));
        return this._http.put<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/not-numbered-zones/bulk`, zonesToSave);
    }

    deleteNotNumberedZone(venueTplId: number, zoneId: number): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/not-numbered-zones/${zoneId}`);
    }

    // images

    getVenueTplImages(tplId: number): Observable<VenueTemplateImage[]> {
        return this._http.get<VenueTemplateImage[]>(`${this.VENUE_TEMPLATES_API}/${tplId}/images`);
    }

    postVenueTplImage(
        tplId: number, post: { filename: string; image_binary: string; temporary?: boolean }
    ): Observable<VenueTemplateImage> {
        return this._http.post<VenueTemplateImage>(`${this.VENUE_TEMPLATES_API}/${tplId}/images`, post);
    }

    deleteVenueTplImage(tplId: number, imageId: number): Observable<void> {
        return this._http.delete<void>(`${this.VENUE_TEMPLATES_API}/${tplId}/images/${imageId}`);
    }

    // AVET COMPETITIONS

    getVenueTplAvetCompetitions(venueTplId: number, eventEntityId: number, skipUsed: boolean): Observable<VenueTemplateAvetCompetition[]> {
        const params = buildHttpParams({ event_entity_id: eventEntityId, skip_used: skipUsed });
        return this._http.get<VenueTemplateAvetCompetition[]>(`${this.VENUE_TEMPLATES_API}/${venueTplId}/avet-competitions`, { params });
    }

    // to map not numbered zones to venue-map (proto) format
    private mapApiNotNumberedZone(apiNNZ: ApiNotNumberedZone): NotNumberedZone {
        return {
            itemType: VenueTemplateItemType.notNumberedZone,
            id: apiNNZ.id,
            name: apiNNZ.name,
            capacity: apiNNZ.capacity,
            sector: apiNNZ.sector_id,
            view: apiNNZ.view_id,
            statusCounters: apiNNZ.status_counters.map(sc => ({ itemType: VenueTemplateItemType.notNumberedZoneStatusCounter, ...sc })),
            blockingReasonCounters: apiNNZ.blocking_reason_counters.map(br =>
                ({ itemType: VenueTemplateItemType.notNumberedZoneBlockingReasonCounter, ...br })),
            priceType: apiNNZ.price_type,
            quotaCounters: apiNNZ.quota_counters.map(qc => ({ itemType: VenueTemplateItemType.notNumberedZoneQuotaCounters, ...qc })),
            accessibility: apiNNZ.accessibility,
            visibility: apiNNZ.visibility,
            gate: apiNNZ.gate
        } as NotNumberedZone;
    }

}
