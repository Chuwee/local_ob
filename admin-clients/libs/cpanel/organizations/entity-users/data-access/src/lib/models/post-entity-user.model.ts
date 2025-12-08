
export interface PostEntityUser {
    username: string;
    entity_id: number;
    name: string;
    operator_id?: number;
    last_name?: string;
    language?: string;
    job_title?: string;
    send_email?: boolean;
}
