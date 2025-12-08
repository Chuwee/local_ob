import {
    AssignedSessionsNumber,
    SeasonTicketSessionAssignment, SeasonTicketSessionsAssignments, SeasonTicketSessionStatus, SeasonTicketSessionsValidations,
    SeasonTicketSessionUnAssignment, SeasonTicketSessionValidation, UnassignedSessionsNumber, ValidatedSessionsNumber
} from '@admin-clients/cpanel/promoters/season-tickets/sessions/data-access';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { VmSeasonTicketSession } from './models/vm-season-ticket-session.model';
import { SeasonTicketSessionsListState } from './state/season-ticket-sessions-list.state';

@Injectable()
export class SeasonTicketSessionsListService {
    readonly #listState = inject(SeasonTicketSessionsListState);

    #sessionsList: VmSeasonTicketSession[];
    // REASSIGNMENTS
    #numberOfReassignedSessions: AssignedSessionsNumber;
    // VALIDATIONS
    #numberOfValidatedSessions: ValidatedSessionsNumber;
    #groupsOfSessionsToValidate: Map<number, VmSeasonTicketSession>[];
    // REMOVE STATUS
    #numberOfSessionsToUnAssign: number;
    #numberOfUnvalidatedSessions: number;
    // SAVE VALID SESSIONS
    #numberOfAssignedSessions: AssignedSessionsNumber;
    #groupedValidSessionsToSave: Map<number, VmSeasonTicketSession>[];
    // SAVE UNASSIGNED SESSIONS
    #numberOfUnassignedSessions: UnassignedSessionsNumber;
    #groupedUnassignedSessionsToSave: Map<number, VmSeasonTicketSession>[];

    get numberOfAssignedSessions(): AssignedSessionsNumber {
        return this.#numberOfAssignedSessions;
    }

    get numberOfUnassignedSessions(): UnassignedSessionsNumber {
        return this.#numberOfUnassignedSessions;
    }

    get numberOfReassignedSessions(): AssignedSessionsNumber {
        return this.#numberOfReassignedSessions;
    }

    get numberOfValidatedSessions(): ValidatedSessionsNumber {
        return this.#numberOfValidatedSessions;
    }

    get numberOfSessionsToUnAssign(): number {
        return this.#numberOfSessionsToUnAssign;
    }

    get numberOfUnvalidatedSessions(): number {
        return this.#numberOfUnvalidatedSessions;
    }

    constructor() {
        this.initNumberOfReassignedSessions();
        this.initNumberOfValidatedSessions();
        this.initNumberOfAssignedSessions();
        this.initNumberOfUnassignedSessions();
        this.initNumberOfUnvalidatedSessions();
        this.initNumberOfSessionsToUnAssign();
    }

    getSessionsList$(): Observable<VmSeasonTicketSession[]> {
        return this.#listState.vmSessions.getValue$();
    }

    setSessionsList(sessionsList: VmSeasonTicketSession[]): void {
        this.#sessionsList = sessionsList;
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    // TABLE INTERACTION
    isSessionRowSelectable(sessionRow: VmSeasonTicketSession): boolean {
        return sessionRow.is_session_row_selectable;
    }

    toggleSessionRowSelection(sessionRow: VmSeasonTicketSession): void {
        this.#sessionsList = this.#sessionsList.map(session => {
            if (sessionRow.session_id === session.session_id) {
                return { ...session, is_selected: !session.is_selected };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    toggleAllSessionRows(): void {
        if (this.isAllSelectableSessionRowsSelected()) {
            this.#sessionsList = this.#sessionsList.map(session =>
                ({ ...session, is_selected: false }));
        } else {
            this.#sessionsList = this.#sessionsList.map(session =>
                ({ ...session, is_selected: session.is_session_row_selectable }));
        }
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    isAllSelectableSessionRowsSelected(): boolean {
        const numSelected = this.#sessionsList.filter(session => session.is_selected).length;
        const numRowsSelectable = this.#sessionsList.filter(session => session.is_session_row_selectable).length;
        return numSelected !== 0 && numSelected === numRowsSelectable;
    }

    isSomeSessionSelected(): boolean {
        return this.#sessionsList.some(session => session.is_selected);
    }

    deselectAllSessions(): void {
        this.#sessionsList = this.#sessionsList.map(session => ({ ...session, is_selected: false }));
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    // SAVE VALID SESSIONS
    isSomeSessionValid(): boolean {
        return this.#sessionsList.some(session => session.is_session_valid);
    }

    setValidSessionsToSave(): void {
        this.initNumberOfAssignedSessions();
        const validSessionsToSave = this.getValidSessionsToSave();
        this.setNumberOfSessionsToAssign(validSessionsToSave.length);
        this.#groupedValidSessionsToSave = this.getGroupsOfSessions(validSessionsToSave);
    }

    getGroupOfValidSessionsToSave(): Map<number, VmSeasonTicketSession> {
        const [sessions] = this.#groupedValidSessionsToSave;
        return sessions;
    }

    setValidSessionsWithSavingInProgress(groupOfValidSessionsToSave: Map<number, VmSeasonTicketSession>): void {
        this.#sessionsList = this.#sessionsList.map(session => {
            if (groupOfValidSessionsToSave.has(session.session_id)) {
                return { ...session, is_assignment_in_progress: true };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    isSomeGroupOfValidSessionToSave(): boolean {
        return !!this.#groupedValidSessionsToSave.length;
    }

    setAssignedSessions(assignedSessions: SeasonTicketSessionsAssignments): void {
        const validSessionsBeingSaved = this.getGroupOfValidSessionsToSave();
        const assignedSessionsToAdd = assignedSessions.result;
        this.setNumberOfAssignedSessions(validSessionsBeingSaved, assignedSessionsToAdd);
        this.#sessionsList = this.#sessionsList.map(session => {
            if (validSessionsBeingSaved.has(session.session_id)) {
                const assignment = assignedSessionsToAdd.find(assignedSessionToAdd =>
                    assignedSessionToAdd.session_id === session.session_id);
                if (assignment) {
                    return {
                        ...session,
                        status: assignment.session_assigned ?
                            SeasonTicketSessionStatus.assigned : SeasonTicketSessionStatus.notAssigned,
                        is_process_session_assignment_done: true,
                        is_session_validated: !assignment.session_assigned,
                        is_session_valid: false,
                        is_session_not_valid: !assignment.session_assigned,
                        session_not_valid_reason: assignment.reason
                    };
                }
            }
            return session;
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    setNextValidSessionToSave(): void {
        const validSessionsBeingSaved = this.getGroupOfValidSessionsToSave();
        this.setRestOfGroupsOfValidSessionsToSave();
        this.#sessionsList = this.#sessionsList.map(session => {
            if (validSessionsBeingSaved.has(session.session_id)) {
                return { ...session, is_assignment_in_progress: false };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    resetAssigmentSessionProcess(): void {
        this.#sessionsList = this.#sessionsList.map(session => ({ ...session, is_process_session_assignment_done: false }));
        this.initNumberOfAssignedSessions();
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    hasSomeSessionNotBeenAssigned(): boolean {
        return this.#numberOfAssignedSessions.assigned !== this.#numberOfAssignedSessions.toAssign;
    }

    // SAVE UNASSIGNED SESSIONS
    isSomeSessionToBeUnassigned(): boolean {
        return this.#sessionsList.some(session => session.is_session_assignment_to_be_unassigned);
    }

    setUnassignedSessionsToSave(): void {
        this.initNumberOfUnassignedSessions();
        const unassignedSessionsToSave = this.getUnassignedSessionsToSave();
        this.setNumberOfSessionsToUnAssign(unassignedSessionsToSave.length);
        this.#groupedUnassignedSessionsToSave = this.getGroupsOfSessions(unassignedSessionsToSave);
    }

    getGroupOfUnassignedSessionsToSave(): Map<number, VmSeasonTicketSession> {
        const [sessions] = this.#groupedUnassignedSessionsToSave;
        return sessions;
    }

    setUnassignedSessionsWithSavingInProgress(groupOfUnassignedSessionsToSave: Map<number, VmSeasonTicketSession>): void {
        this.#sessionsList = this.#sessionsList.map(session => {
            if (groupOfUnassignedSessionsToSave.has(session.session_id)) {
                return { ...session, is_unassignment_in_progress: true };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    setUnassignedSession(unassignedSession: SeasonTicketSessionUnAssignment): void {
        const unassignedSessionsBeingSaved = this.getGroupOfUnassignedSessionsToSave();
        this.setNumberOfUnassignedSessions(unassignedSessionsBeingSaved, unassignedSession);
        this.#sessionsList = this.#sessionsList.map(session => {
            if (unassignedSessionsBeingSaved.has(session.session_id)) {
                if (unassignedSession.session_id === session.session_id) {
                    return {
                        ...session,
                        status: unassignedSession.session_unassigned ?
                            SeasonTicketSessionStatus.notAssigned : SeasonTicketSessionStatus.assigned,
                        is_process_session_unassignment_done: true,
                        is_session_assignment_to_be_unassigned: false,
                        sessions_not_unassigned_reason: unassignedSession.reason
                    };
                }
            }
            return session;
        });
    }

    setNextUnassignedSessionsToSave(): void {
        const unassignedSessionsBeingSaved = this.getGroupOfUnassignedSessionsToSave();
        this.setRestOfGroupsOfUnassignedSessionsToSave();
        this.#sessionsList = this.#sessionsList.map(session => {
            if (unassignedSessionsBeingSaved.has(session.session_id)) {
                return { ...session, is_unassignment_in_progress: false };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    resetUnAssignmentSessionProcess(): void {
        this.#sessionsList = this.#sessionsList.map(session => ({ ...session, is_unassignment_in_progress: false }));
        this.initNumberOfUnassignedSessions();
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    hasSomeSessionNotBeenUnassigned(): boolean {
        return this.#numberOfUnassignedSessions.unAssigned !== this.#numberOfUnassignedSessions.toUnAssign;
    }

    // REMOVE STATUS
    isSomeSessionToUnAssign(): boolean {
        return this.#sessionsList.some(session => this.isSessionToUnAssign(session));
    }

    isSomeSessionValidationToRemove(): boolean {
        return this.#sessionsList.some(session => this.isSessionValidationToRemove(session));
    }

    setSessionsToUnAssign(): void {
        this.initNumberOfSessionsToUnAssign();
        this.#sessionsList = this.#sessionsList.map(session => {
            if (this.isSessionToUnAssign(session)) {
                this.#numberOfSessionsToUnAssign++;
                return {
                    ...session,
                    status: SeasonTicketSessionStatus.notAssigned,
                    is_session_assignment_to_be_unassigned: true,
                    is_selected: false,
                    sessions_not_unassigned_reason: null
                };
            } else if (this.isSessionValidationToRemove(session)) {
                return session;
            } else {
                return { ...session, is_selected: false };
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    resetSessionsToUnAssignParams(): void {
        this.initNumberOfSessionsToUnAssign();
    }

    setSessionValidationsToRemove(): void {
        this.initNumberOfUnvalidatedSessions();
        this.#sessionsList = this.#sessionsList.map(session => {
            if (this.isSessionValidationToRemove(session)) {
                // Remove validation
                this.#numberOfUnvalidatedSessions++;
                return {
                    ...session,
                    is_session_valid: false,
                    session_not_valid_reason: null,
                    is_session_validated: false,
                    is_selected: false
                };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    resetUnvalidatedSessionsParams(): void {
        this.initNumberOfUnvalidatedSessions();
    }

    // ASSIGN REASSIGN
    isSomeSessionToReassign(): boolean {
        return this.#sessionsList.some(session => this.isSessionToReassign(session));
    }

    reassignSessions(): void {
        this.initNumberOfReassignedSessions();
        this.#sessionsList = this.#sessionsList.map(session => {
            if (this.isSessionToReassign(session)) {
                this.updateNumberOfReassignedSessions();
                return {
                    ...session,
                    is_session_assignment_to_be_unassigned: false,
                    status: SeasonTicketSessionStatus.assigned,
                    is_selected: false
                };
            } else if (this.isSessionAlreadyValidOrAssigned(session)) {
                return { ...session, is_selected: false };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    resetReassignedSessionsParams(): void {
        this.initNumberOfReassignedSessions();
    }

    // ASSIGN VALIDATION
    isSomeSessionToValidate(): boolean {
        return this.#sessionsList.some(session => session.is_selected &&
            session.status === SeasonTicketSessionStatus.notAssigned &&
            !session.is_session_assignment_to_be_unassigned &&
            (!session.is_session_validated || session.is_session_not_valid)
        );
    }

    isSomeGroupOfSessionToValidate(): boolean {
        return !!this.#groupsOfSessionsToValidate?.length;
    }

    setSessionsToValidate(): void {
        this.initNumberOfValidatedSessions();
        const sessionsToValidate = this.getSessionsToValidate();
        this.setNumberOfSessionsToValidate(sessionsToValidate.length);
        this.#groupsOfSessionsToValidate = this.getGroupsOfSessions(sessionsToValidate);
        this.#sessionsList = this.getSessionsNotToValidateDeselected();
        this.#listState.vmSessions.setValue(this.#sessionsList);

    }

    setNextSessionsToValidate(): void {
        const sessionsBeingValidated = this.getGroupOfSessionsToValidate();
        this.setRestOfGroupsOfSessionsToValidate();
        this.#sessionsList = this.#sessionsList.map(session => {
            if (sessionsBeingValidated.has(session.session_id)) {
                return { ...session, is_validation_in_progress: false, is_selected: false };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    getGroupOfSessionsToValidate(): Map<number, VmSeasonTicketSession> {
        const [sessions] = this.#groupsOfSessionsToValidate;
        return sessions;
    }

    setSessionsWithValidationInProgress(sessionsToValidate: Map<number, VmSeasonTicketSession>): void {
        this.#sessionsList = this.#sessionsList.map(session => {
            if (sessionsToValidate.has(session.session_id)) {
                return { ...session, is_validation_in_progress: true };
            } else {
                return session;
            }
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    setValidatedSessions(validatedSessions: SeasonTicketSessionsValidations): void {
        const sessionsBeingValidated = this.getGroupOfSessionsToValidate();
        const validatedSessionsToAdd = validatedSessions.result;
        this.setNumberOfValidSessions(sessionsBeingValidated, validatedSessionsToAdd);
        this.#sessionsList = this.#sessionsList.map(session => {
            if (sessionsBeingValidated.has(session.session_id)) {
                const validation = validatedSessionsToAdd.find(validatedSessionToAdd =>
                    validatedSessionToAdd.session_id === session.session_id);
                if (validation) {
                    return {
                        ...session,
                        is_session_validated: true,
                        is_session_valid: validation.session_valid,
                        session_not_valid_reason: validation.reason,
                        is_session_not_valid: !validation.session_valid
                    };
                }
            }
            return session;
        });
        this.#listState.vmSessions.setValue(this.#sessionsList);
    }

    resetValidatedSessionsParams(): void {
        this.initNumberOfValidatedSessions();
    }

    //BARCODES
    isSomeBarcodeToBeUpdated(): boolean {
        return (this.#numberOfAssignedSessions.toAssign !== 0 && this.#numberOfAssignedSessions.assigned !== 0) ||
            (this.#numberOfUnassignedSessions.toUnAssign !== 0 && this.#numberOfUnassignedSessions.unAssigned !== 0);
    }

    // PRIVATE GROUP
    private getGroupsOfSessions(
        sessions: VmSeasonTicketSession[],
        numberOfSessions = 1
    ): Map<number, VmSeasonTicketSession>[] {
        return sessions.reduce((groupedSessions, session) => {
            const sessionKey = session.session_id;
            const index = groupedSessions.findIndex(group => group.size < numberOfSessions);
            if (index > -1) {
                groupedSessions[index].set(sessionKey, session);
            } else {
                groupedSessions.push(new Map<number, VmSeasonTicketSession>().set(sessionKey, session));
            }
            return groupedSessions;
        }, [new Map<number, VmSeasonTicketSession>()]);
    }

    // PRIVATE TABLE

    // PRIVATE ASSIGN REASSIGN
    private initNumberOfReassignedSessions(): void {
        this.#numberOfReassignedSessions = { assigned: 0, toAssign: 0 };
    }

    private updateNumberOfReassignedSessions(): void {
        this.#numberOfReassignedSessions = {
            toAssign: this.#numberOfReassignedSessions.toAssign + 1,
            assigned: this.#numberOfReassignedSessions.assigned + 1
        };
    }

    private isSessionToReassign(session: VmSeasonTicketSession): boolean {
        return session.is_selected && session.is_session_assignment_to_be_unassigned;
    }

    // PRIVATE ASSIGN VALIDATE
    private getSessionsToValidate(): VmSeasonTicketSession[] {
        return this.#sessionsList.filter(session => session.is_selected &&
            !session.is_session_valid &&
            session.status !== SeasonTicketSessionStatus.assigned);
    }

    private initNumberOfValidatedSessions(): void {
        this.#numberOfValidatedSessions = { valid: 0, validated: 0 };
    }

    private setNumberOfSessionsToValidate(numberOfSessionsToValidate: number): void {
        this.#numberOfValidatedSessions = { validated: numberOfSessionsToValidate, valid: 0 };
    }

    private getSessionsNotToValidateDeselected(): VmSeasonTicketSession[] {
        return this.#sessionsList.map(session => {
            if (this.isSessionAlreadyValidOrAssigned(session)) {
                return { ...session, is_selected: false };
            } else {
                return session;
            }
        });
    }

    private isSessionAlreadyValidOrAssigned(session: VmSeasonTicketSession): boolean {
        return session.is_selected &&
            (session.is_session_valid || session.status === SeasonTicketSessionStatus.assigned);
    }

    private setNumberOfValidSessions(
        sessionsBeingValidated: Map<number, VmSeasonTicketSession>,
        validatedSessionsToAdd: SeasonTicketSessionValidation[]
    ): void {
        validatedSessionsToAdd.forEach(validatedSession => {
            if (sessionsBeingValidated.has(validatedSession.session_id)) {
                this.#numberOfValidatedSessions = {
                    ...this.#numberOfValidatedSessions,
                    valid: validatedSession.session_valid ?
                        this.#numberOfValidatedSessions.valid + 1 :
                        this.#numberOfValidatedSessions.valid
                };
            }
        });
    }

    private setRestOfGroupsOfSessionsToValidate(): void {
        const [, ...rest] = this.#groupsOfSessionsToValidate;
        this.#groupsOfSessionsToValidate = rest;
    }

    // PRIVATE REMOVE STATUS
    private initNumberOfUnvalidatedSessions(): void {
        this.#numberOfUnvalidatedSessions = 0;
    }

    private isSessionValidationToRemove(session: VmSeasonTicketSession): boolean {
        return session.is_selected && session.is_session_valid;
    }

    private initNumberOfSessionsToUnAssign(): void {
        this.#numberOfSessionsToUnAssign = 0;
    }

    private isSessionToUnAssign(session: VmSeasonTicketSession): boolean {
        return session.is_selected && session.status === SeasonTicketSessionStatus.assigned;
    }

    // PRIVATE SAVE VALID SESSIONS
    private initNumberOfAssignedSessions(): void {
        this.#numberOfAssignedSessions = { assigned: 0, toAssign: 0 };
    }

    private getValidSessionsToSave(): VmSeasonTicketSession[] {
        return this.#sessionsList.filter(session => session.is_session_valid);
    }

    private setNumberOfSessionsToAssign(numberOfSessionsToAssign: number): void {
        this.#numberOfAssignedSessions = { assigned: 0, toAssign: numberOfSessionsToAssign };
    }

    private setNumberOfAssignedSessions(
        validSessionsBeingSaved: Map<number, VmSeasonTicketSession>,
        assignedSessionsToAdd: SeasonTicketSessionAssignment[]
    ): void {
        assignedSessionsToAdd.forEach(assignedSession => {
            if (validSessionsBeingSaved.has(assignedSession.session_id)) {
                this.#numberOfAssignedSessions = {
                    ...this.#numberOfAssignedSessions,
                    assigned: assignedSession.session_assigned ?
                        this.#numberOfAssignedSessions.assigned + 1 :
                        this.#numberOfAssignedSessions.assigned
                };
            }
        });
    }

    private setRestOfGroupsOfValidSessionsToSave(): void {
        const [, ...rest] = this.#groupedValidSessionsToSave;
        this.#groupedValidSessionsToSave = rest;
    }

    // PRIVATE SAVE UNASSIGNED SESSIONS
    private initNumberOfUnassignedSessions(): void {
        this.#numberOfUnassignedSessions = { unAssigned: 0, toUnAssign: 0 };
    }

    private getUnassignedSessionsToSave(): VmSeasonTicketSession[] {
        return this.#sessionsList.filter(session => session.is_session_assignment_to_be_unassigned);
    }

    private setNumberOfSessionsToUnAssign(numberOfSessionsToUnAssign: number): void {
        this.#numberOfUnassignedSessions = { unAssigned: 0, toUnAssign: numberOfSessionsToUnAssign };
    }

    private setNumberOfUnassignedSessions(
        unassignedSessionsBeingSaved: Map<number, VmSeasonTicketSession>,
        unassignedSession: SeasonTicketSessionUnAssignment
    ): void {
        if (unassignedSessionsBeingSaved.has(unassignedSession.session_id)) {
            this.#numberOfUnassignedSessions = {
                ...this.#numberOfUnassignedSessions,
                unAssigned: unassignedSession.session_unassigned ?
                    this.#numberOfUnassignedSessions.unAssigned + 1 :
                    this.#numberOfUnassignedSessions.unAssigned
            };
        }
    }

    private setRestOfGroupsOfUnassignedSessionsToSave(): void {
        const [, ...rest] = this.#groupedUnassignedSessionsToSave;
        this.#groupedUnassignedSessionsToSave = rest;
    }
}
