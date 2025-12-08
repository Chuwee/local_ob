import { NotNumberedZone, Row, Seat, Sector, VenueMap } from '@admin-clients/shared/venues/data-access/standard-venue-tpls';
import { EdNotNumberedZone, EdRow, EdSeat, EdSector, EdVenueMap } from './venue-tpl-editor-venue-map-items.model';

export function mapVenueMap(venueMap: VenueMap): EdVenueMap {
    return { sectors: mapSectors(venueMap.sectors) };
}

function mapSectors(sectors: Sector[]): EdSector[] {
    return sectors.map(sector => ({
        itemType: sector.itemType,
        id: sector.id,
        name: sector.name,
        order: sector.order,
        code: sector.code,
        default: sector.default,
        rows: mapRows(sector.rows),
        notNumberedZones: mapNotNumberedZones(sector.notNumberedZones)
    })).sort((a, b) => a.order - b.order);
}

function mapRows(rows: Row[]): EdRow[] {
    return rows.map(row => ({
        itemType: row.itemType,
        id: row.id,
        name: row.name,
        sector: row.sector,
        order: row.order,
        seats: mapSeats(row.seats)
    })).sort((a, b) => a.order - b.order);
}

function mapSeats(seats: Seat[]): EdSeat[] {
    return seats.map(seat => ({
        itemType: seat.itemType,
        id: seat.id,
        name: seat.name,
        view: seat.view,
        external: seat.external,
        rowId: seat.rowId,
        rowBlock: seat.rowBlock,
        order: seat.order,
        posX: seat.posX,
        posY: seat.posY,
        weight: seat.weight
    })).sort((a, b) => a.order - b.order);
}

function mapNotNumberedZones(zones: NotNumberedZone[]): EdNotNumberedZone[] {
    return zones.map(zone => ({
        itemType: zone.itemType,
        id: zone.id,
        name: zone.name,
        view: zone.view,
        sector: zone.sector,
        capacity: zone.capacity
    }));
}
