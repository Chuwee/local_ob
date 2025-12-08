export interface CloneSessionRequest {
    name: string;
    start_date: string;
    end_date?: string;
    reference?: string;
    session_pack_seats_target?: string;
}
