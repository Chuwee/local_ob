import { ListResponse } from '@OneboxTM/utils-state';

export interface ContentLinkResponse extends ListResponse<ContentLink> {
}

export interface ContentLink {
    id?: number;
    name: string;
    start_date?: string;
    link: string;
    enabled?: boolean;
    virtual_queue?: VirtualQueue;
    pending_generation?: boolean;
}

export interface VirtualQueue {
    enabled: boolean;
    event: string;
    action_name: string;
}

export interface ContentLinkRequest {
    language?: string;
    event_link?: string;
    detail_link?: string;
    select_link?: string;
}
