import { cloneObject } from '@admin-clients/shared/utility/utils';
import { es } from '../models/proto/venue-map';
import { Accessibility } from '../models/seat-accessibility.enum';
import { SeatLinkable } from '../models/seat-linkable.enum';
import { SeatLinked } from '../models/seat-linked.enum';
import { SeatStatus } from '../models/seat-status.enum';
import { Visibility } from '../models/seat-visibility.enum';
import { VenueTemplateItemType } from '../models/vm-item-type.enum';
import {
    BlockingReasonCounter, NotNumberedZone, QuotaCounter, Row, Seat, Sector, SessionPackCounter, StatusCounter, VenueMap
} from '../models/vm-item.model';
import ProtoVenueMapClass = es.onebox.venue.venuetemplates.VenueMap;
import ProtoVenueMap = es.onebox.venue.venuetemplates.IVenueMap;
import ProtoSectorMap = es.onebox.venue.venuetemplates.ISectorMap;
import ProtoRowMap = es.onebox.venue.venuetemplates.IRowMap;
import ProtoSeatMap = es.onebox.venue.venuetemplates.ISeatMap;
import ProtoNotNumberedZone = es.onebox.venue.venuetemplates.INotNumberedZoneMap;
import ProtoStatusCounterMap = es.onebox.venue.venuetemplates.IStatusCounterMap;
import ProtoSeatStatus = es.onebox.venue.venuetemplates.Enums.SeatStatus;
import ProtoAccessibility = es.onebox.venue.venuetemplates.Enums.Accessibility;
import ProtoVisibility = es.onebox.venue.venuetemplates.Enums.Visibility;
import ProtoBlockingReasonsMap = es.onebox.venue.venuetemplates.IBlockingReasonsCounterMap;
import SessionPackCounterMap = es.onebox.venue.venuetemplates.SessionPackCounterMap;
import QuotaCountersMap = es.onebox.venue.venuetemplates.QuotaCountersMap;

export function decodeAndMapVenueMap(arrayBuffer: ArrayBuffer): VenueMap {
    return arrayBuffer && mapVenueMap(ProtoVenueMapClass.decode(new Uint8Array(arrayBuffer)));
}

function mapVenueMap(protoVenueMap: ProtoVenueMap): VenueMap {
    // if you want to check received venueMap protobuf data (good luck)
    // console.log('proto venueMap: ', protoVenueMap);
    // if you want to check received venueMap mapped data
    // console.log('mapped venueMap: ', protoVenueMap && { sectors: protoVenueMap.sectorMap.map(mapSectorMap) });
    if (!protoVenueMap?.sectorMap?.length) {
        console.warn('Empty venue map loaded');
    }
    return protoVenueMap && { sectors: protoVenueMap.sectorMap.map(mapSectorMap) };
}

function mapSectorMap(protoSectorMap: ProtoSectorMap): Sector {
    return protoSectorMap
        && {
            itemType: VenueTemplateItemType.sector,
            id: protoSectorMap.id,
            code: protoSectorMap.code,
            type: protoSectorMap.type,
            order: protoSectorMap.order,
            venueTemplate: protoSectorMap.venuetemplate,
            name: protoSectorMap.description,
            default: protoSectorMap.default,
            rows: protoSectorMap.rowMap?.map(row => mapRowMap(row, protoSectorMap)) || null,
            notNumberedZones: protoSectorMap.notNumberedZoneMap?.map(nnz => mapNotNumberedZoneMap(nnz, protoSectorMap)) || null
        };
}

function mapRowMap(protoRowMap: ProtoRowMap, protoSectorMap: ProtoSectorMap): Row {
    return protoRowMap
        && {
            itemType: VenueTemplateItemType.row,
            id: protoRowMap.id,
            sector: protoRowMap.sectorId,
            name: protoRowMap.name,
            order: protoRowMap.order,
            seats: protoRowMap.seatMap?.map(seat => mapSeatMap(seat, protoRowMap, protoSectorMap)) || null
        };
}

function mapSeatMap(protoSeatMap: ProtoSeatMap, protoRowMap: ProtoRowMap, protoSectorMap: ProtoSectorMap): Seat {
    return protoSeatMap
        && {
            itemType: VenueTemplateItemType.seat,
            id: protoSeatMap.id,
            name: protoSeatMap.name,
            status: mapSeatStatus(protoSeatMap.status),
            accessibility: mapAccessibility(protoSeatMap.accessibility),
            blockingReason: protoSeatMap.blockingReason,
            external: protoSeatMap.external,
            gate: protoSeatMap.gate,
            order: protoSeatMap.order,
            priceType: protoSeatMap.priceType,
            quota: protoSeatMap.quota,
            rowBlock: protoSeatMap.rowBlock,
            ticketId: protoSeatMap.ticketId,
            view: protoSeatMap.view,
            visibility: mapVisibility(protoSeatMap.visibility),
            weight: protoSeatMap.weight,
            linkable: protoSeatMap.linkable ? SeatLinkable.linkable : SeatLinkable.notLinkable,
            linked: protoSeatMap.linked ? SeatLinked.linked : SeatLinked.unlinked,
            sessionPack: protoSeatMap.sessionPack,
            rowId: protoRowMap.id,
            rowName: protoRowMap.name,
            sectorName: protoSectorMap.description,
            firstCustomTag: protoSeatMap.dynamicTag1,
            secondCustomTag: protoSeatMap.dynamicTag2,
            posX: protoSeatMap.posX,
            posY: protoSeatMap.posY
        };
}

function mapNotNumberedZoneMap(protoNotNumberedZone: ProtoNotNumberedZone, protoSectorMap: ProtoSectorMap): NotNumberedZone {
    if (protoNotNumberedZone) {
        const result: NotNumberedZone = {
            itemType: VenueTemplateItemType.notNumberedZone,
            id: protoNotNumberedZone.id,
            name: protoNotNumberedZone.name,
            sector: protoNotNumberedZone.sector,
            priceType: protoNotNumberedZone.priceType,
            view: protoNotNumberedZone.view,
            quotaCounters: protoNotNumberedZone.quotaCounters?.map(mapQuotaCounters) || null,
            gate: protoNotNumberedZone.gate,
            capacity: protoNotNumberedZone.capacity,
            visibility: mapVisibility(protoNotNumberedZone.visibility),
            accessibility: mapAccessibility(protoNotNumberedZone.accessibility),
            statusCounters: protoNotNumberedZone.statusCounters ? mapStatusCounters(protoNotNumberedZone) : null,
            blockingReasonCounters: protoNotNumberedZone.blockingReasonCounters?.map(mapBlockingReasonCounters) || null,
            sessionPackCounters: protoNotNumberedZone.sessionPackCounters?.map(mapSessionPackCounters) || null,
            sectorName: protoSectorMap.description,
            linkableSeats: protoNotNumberedZone.linkableSeats,
            firstCustomTag: protoNotNumberedZone.dynamicTag1,
            secondCustomTag: protoNotNumberedZone.dynamicTag2
        };
        //TODO: migrate cloneObject
        result.record = { initialState: cloneObject(result) };
        return result;
    } else {
        return null;
    }
}

function mapStatusCounters(protoNotNumberedZone: ProtoNotNumberedZone): StatusCounter[] {
    return Object.values(protoNotNumberedZone.statusCounters
        ?.reduce<Partial<Record<SeatStatus, StatusCounter>>>((acc, statusCounterMap: ProtoStatusCounterMap) => {
            if (acc[mapSeatStatus(statusCounterMap.status)]) {
                acc[mapSeatStatus(statusCounterMap.status)].count += statusCounterMap.count;
            } else {
                acc[mapSeatStatus(statusCounterMap.status)] = {
                    itemType: VenueTemplateItemType.notNumberedZoneStatusCounter,
                    status: mapSeatStatus(statusCounterMap.status),
                    count: statusCounterMap.count,
                    linked: statusCounterMap.linked ? SeatLinked.linked : SeatLinked.unlinked
                };
            }
            return acc;
        }, {}));
}

function mapQuotaCounters(quotaCounterMap: QuotaCountersMap): QuotaCounter {
    return quotaCounterMap
        && {
            itemType: VenueTemplateItemType.notNumberedZoneQuotaCounters,
            count: quotaCounterMap.count,
            quota: quotaCounterMap.quota,
            available: quotaCounterMap.available
        };
}

function mapBlockingReasonCounters(blockingReasonsMap: ProtoBlockingReasonsMap): BlockingReasonCounter {
    return blockingReasonsMap && {
        itemType: VenueTemplateItemType.notNumberedZoneBlockingReasonCounter,
        blocking_reason: blockingReasonsMap.blockingReason,
        count: blockingReasonsMap.count
    };
}

function mapSessionPackCounters(sessionPackCounterMap: SessionPackCounterMap): SessionPackCounter {
    return sessionPackCounterMap && {
        itemType: VenueTemplateItemType.notNumberedZoneSessionPackCounter,
        sessionPack: sessionPackCounterMap.sessionPack,
        count: sessionPackCounterMap.count
    };
}

const seatStatusMap = {
    [ProtoSeatStatus.FREE]: SeatStatus.free,
    [ProtoSeatStatus.SOLD]: SeatStatus.sold,
    [ProtoSeatStatus.VALIDATED]: SeatStatus.sold,
    [ProtoSeatStatus.IN_REFUND]: SeatStatus.sold,
    [ProtoSeatStatus.CANCELLED]: SeatStatus.sold,
    [ProtoSeatStatus.BOOKED]: SeatStatus.booked,
    [ProtoSeatStatus.GIFT]: SeatStatus.gift,
    [ProtoSeatStatus.EMITTED]: SeatStatus.emitted,
    [ProtoSeatStatus.PROMOTOR_LOCKED]: SeatStatus.promotorLocked,
    [ProtoSeatStatus.SYSTEM_LOCKED]: SeatStatus.systemLocked,
    [ProtoSeatStatus.EXTERNAL_DELETE]: SeatStatus.systemLocked,
    [ProtoSeatStatus.SOLD_LOCKED]: SeatStatus.systemLocked,
    [ProtoSeatStatus.PRESOLD_LOCKED]: SeatStatus.presoldLocked,
    [ProtoSeatStatus.KILL]: SeatStatus.kill,
    [ProtoSeatStatus.SEASON_LOCKED]: SeatStatus.seasonLocked,
    [ProtoSeatStatus.EXTERNAL_LOCKED]: SeatStatus.externalLocked
} as const;

function mapSeatStatus(status: ProtoSeatStatus): SeatStatus {
    return seatStatusMap[status] ?? null;
}

const accessibilityMap = {
    [ProtoAccessibility.NORMAL]: Accessibility.normal,
    [ProtoAccessibility.REDUCED_MOBILITY]: Accessibility.reducedMobility
} as const;

function mapAccessibility(accessibility: ProtoAccessibility): Accessibility {
    return accessibilityMap[accessibility] ?? null;
}

const visibilityMap = {
    [ProtoVisibility.FULL]: Visibility.full,
    [ProtoVisibility.PARTIAL]: Visibility.partial,
    [ProtoVisibility.NONE]: Visibility.none,
    [ProtoVisibility.SIDE]: Visibility.side
} as const;

function mapVisibility(visibility: ProtoVisibility): Visibility {
    return visibilityMap[visibility] ?? null;
}
