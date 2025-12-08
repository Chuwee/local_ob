import { MatcherStatus } from './matcher.status';

export interface GetMatcherStatusResponse {
    id: string;
    status: MatcherStatus;
    start_date: Date;
    end_date: Date;
    supplier: string;
    shi_events: number;
    supplier_events: number;
    processed_events: number;
    message: string;
    ratio: {
        mapped: number;
        matched: number;
        notRelated: number;
    };
}
