import { IdName } from '@admin-clients/shared/data-access/models';
import { PaymentData } from '../../orders/models/payment-data.model';
import { MemberOrderItemState } from './member-order-item-state.enum';
import { MemberOrderState } from './member-order-state.enum';
import { MemberOrderType } from './member-order-type.enum';

interface Price {
    final: number;
    charges: number;
    discounts: number;
    base: number;
    currency: string;
}

interface ExternalProperties {
    avet_zone_id: string;
    avet_seat_id: number;
}

interface ExternalEvent {
    date: string;
    id: number;
    name: string;
}

interface Allocation {
    zone: Partial<IdName>;
    sector: string;
    row: IdName;
    seat: IdName;
    periodicity: Partial<IdName>;
    external_properties?: ExternalProperties;
    external_event?: ExternalEvent;
    price: Price;
}

export interface Member {
    partner_id: string;
    person_id: string;
    name: string;
    surname: string;
    id_number: string;
    email: string;
    tutor: MemberTutor;
}

export interface MemberOrderItem {
    state: MemberOrderItemState;
    season: string;
    member: Member;
    allocation?: Allocation;
    previous_allocation?: Allocation;
    subscription: { code: string; name?: string };
    role: Partial<IdName>;
    additional_information: {
        membership_total_price: number;
    };
    membership?: { price: Price };
}

export interface MemberOrderBuyerData {
    name: string;
    surname: string;
    email: string;
    partner_id: string;
    person_id: string;
}

export interface MemberOrderDetail {
    code: string;
    state: MemberOrderState;
    language: string;
    date: string;
    type: MemberOrderType;
    channel: IdName & { entity?: IdName };
    price: Price;
    payment_data: PaymentData[];
    buyer_data: MemberOrderBuyerData;
    items_count?: number;
    items: MemberOrderItem[];
}

export interface MemberTutor {
    name: string;
    lastname?: string;
    surname2?: string;
    email: string;
}

