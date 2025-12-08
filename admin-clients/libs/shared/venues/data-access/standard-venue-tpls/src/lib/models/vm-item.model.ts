import { GateUpdateType } from './gate-update-type.enum';
import { Accessibility } from './seat-accessibility.enum';
import { SeatLinkable } from './seat-linkable.enum';
import { SeatLinked } from './seat-linked.enum';
import { SeatNotLinkableReason } from './seat-not-linkable-reason.enum';
import { SeatStatus } from './seat-status.enum';
import { Visibility } from './seat-visibility.enum';
import { VenueTemplateItemType } from './vm-item-type.enum';

// Only use to Define VenueTemplateItem.
interface VenueTemplateItemTyped<T extends VenueTemplateItemType> {
    itemType: T;
}

// Every interface implementing VenueTemplateItemTyped must be set here
export type VenueTemplateItem = Sector | Row | Seat | Aisle | NotNumberedZone | StatusCounter | BlockingReasonCounter | SessionPackCounter;

export interface VenueMap {
    sectors: Sector[];
}

export interface Sector extends VenueTemplateItemTyped<VenueTemplateItemType.sector> {
    id: number;
    code?: string;
    type?: number;
    order?: number;
    venueTemplate?: number;
    name: string;
    default?: boolean;
    rows?: Row[];
    notNumberedZones?: NotNumberedZone[];
    record?: {
        pendingToCreate?: boolean;
        initialState?: Sector;
    };
}

export interface Row extends VenueTemplateItemTyped<VenueTemplateItemType.row> {
    id: number;
    name: string;
    sector: number;
    order: number;
    seats: Seat[];
}

export interface Seat extends VenueTemplateItemTyped<VenueTemplateItemType.seat> {
    id: number;
    name: string;
    status: SeatStatus;
    ticketId?: number;
    view: number;
    rowId?: number;
    rowBlock: string;
    quota: number;
    priceType: number;
    blockingReason: number;
    visibility: Visibility;
    accessibility: Accessibility;
    gate: number;
    gateUpdateType?: GateUpdateType;
    external: number;
    order: number;
    weight: number;
    linkable: SeatLinkable; // only for seasonTickets
    notAssignableReason?: SeatNotLinkableReason; // only for seasonTickets, not required in calls
    linked: SeatLinked; // only for sessionPacks
    sessionPack: number;
    seatRecord?: {
        initialSeat?: Seat;
        labelGroupRecord?: Set<string>;
    };
    rowName?: string; // not required in calls, only used for client tooltips
    sectorName?: string; // not required in calls, only used for client tooltips
    firstCustomTag?: number;
    secondCustomTag?: number;
    posX: number;
    posY: number;
}

export type Aisle = VenueTemplateItemTyped<VenueTemplateItemType.aisle>;

export interface NotNumberedZone extends VenueTemplateItemTyped<VenueTemplateItemType.notNumberedZone> {
    id: number;
    name: string;
    sector?: number;
    priceType?: number;
    view?: number;
    gate?: number;
    gateUpdateType?: GateUpdateType;
    capacity: number;
    visibility?: Visibility;
    accessibility?: Accessibility;
    statusCounters?: StatusCounter[];
    blockingReasonCounters?: BlockingReasonCounter[];
    sessionPackCounters?: SessionPackCounter[];
    quotaCounters?: QuotaCounter[];
    sectorName?: string; // not required in calls, only used for client tooltips
    // capacity increase
    record?: {
        pendingToCreate?: boolean;
        initialState?: NotNumberedZone;
    };
    linkableSeats?: number;
    firstCustomTag?: number;
    secondCustomTag?: number;
}

export interface Counter {
    count: number;
    initialCount?: number;
    source?: string;
}

export interface StatusCounter extends Counter, VenueTemplateItemTyped<VenueTemplateItemType.notNumberedZoneStatusCounter> {
    status: SeatStatus;
    linked?: SeatLinked; // only for sessionPacks
}

export interface BlockingReasonCounter extends Counter,
    VenueTemplateItemTyped<VenueTemplateItemType.notNumberedZoneBlockingReasonCounter> {
    blocking_reason: number;
}

export interface QuotaCounter extends Counter, VenueTemplateItemTyped<VenueTemplateItemType.notNumberedZoneQuotaCounters> {
    quota: number;
    available?: number;
}

export interface SessionPackCounter extends VenueTemplateItemTyped<VenueTemplateItemType.notNumberedZoneSessionPackCounter> {
    sessionPack: number;
    count: number;
}
