export interface VmSessionAvailability {
    price_type?: string;
    available: number;
    promoter_blocked?: number;
    kill?: number;
    session_pack?: number;
    purchase: number;
    invitation: number;
    booking: number;
    issue: number;
    in_progress: number;
    busy: number;
    total: number;
}
