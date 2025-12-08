import { Session } from './session.model';

export interface SessionWrapper {
    session: Session;
    selected?: boolean;
    isActiveFromInProgress?: boolean;
}
