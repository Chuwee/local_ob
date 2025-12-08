import { InjectionToken } from '@angular/core';
import { Observable } from 'rxjs';
import { NotNumberedZone, Seat, VenueMap } from './models/vm-item.model';

export interface VenueMapService {
    loadVenueMap(idData: IdData): void;

    getVenueMap$(): Observable<VenueMap>;

    clearVenueMap(): void;

    isVenueMapLoading$(): Observable<boolean>;

    updateVenueMap(idData: IdData, seats: Seat[], notNumberedZones: NotNumberedZone[]): Observable<boolean>;

    get isCapacityUpdateAsync(): boolean;

    isVenueMapSaving$(): Observable<boolean>;
}

interface IdData {
    tplId?: number;
    eventId?: number;
    sessionId?: number;
    updatingCapacity?: boolean;
}

export const VENUE_MAP_SERVICE = new InjectionToken<VenueMapService>('VENUE_MAP_SERVICE');
