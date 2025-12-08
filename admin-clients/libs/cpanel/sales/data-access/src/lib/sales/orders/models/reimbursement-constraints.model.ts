import { ReimbursementAction } from './reimbursement-action.enum';

export interface ReimbursementConstraints {
    allowed: boolean;
    actions: ReimbursementAction;
}
