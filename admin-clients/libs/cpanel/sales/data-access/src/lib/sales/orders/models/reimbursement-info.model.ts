import { ReimbursementStatus } from './reimbursement-status.enum';

export interface ReimbursementInfo {
    transaction_id: string;
    date: string;
    amount: number;
    message: string;
    status: ReimbursementStatus;
    retry?: boolean;
    manual_retry?: boolean;
    gateway_additional_info?: {
        [key: string]: {
            [key: string]: any;
        };
    };
    item_ids?: [];
    action?: {
        user: {
            id: number;
            username: string;
        };
        channel: {
            id: number;
            name: string;
        };
        additional_info?: {
            [key: string]: {
                [key: string]: any;
            };
        };
    };
}
