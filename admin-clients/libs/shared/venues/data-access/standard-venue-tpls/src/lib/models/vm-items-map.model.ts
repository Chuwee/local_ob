import { NotNumberedZone, Seat } from './vm-item.model';

export interface VmItemsMap {
    seats: Map<number, Seat>;
    nnzs: Map<number, NotNumberedZone>;
}
